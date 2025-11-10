package com.aadeshandreas.ailearning.ai_learning_companion.service.coding;

import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.CodingQuestion;
import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.ExecutionResult;
import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.TestCase;
import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.TestResult;
import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;
import java.util.concurrent.*;

/**
 * Service for compiling and executing user-submitted Java code in a sandboxed environment.
 * Provides secure code execution with timeout limits and security validation.
 */
@Service
public class CodeExecutor {

    private static final long TIMEOUT_MS = 2000; // 2 seconds per test case

    /**
     * Executes user code against test cases.
     *
     * @param userCode The complete Java code submitted by the user
     * @param question The coding question containing test cases
     * @param visibleOnly If true, only run visible test cases (for "Run Code" button)
     * @return ExecutionResult containing test results and metrics
     */
    public ExecutionResult executeCode(String userCode, CodingQuestion question, boolean visibleOnly) {
        ExecutionResult result = new ExecutionResult();
        List<TestResult> testResults = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        try {
            // Step 1: Validate code for security
            validateCode(userCode);

            // Step 2: Compile the code
            Class<?> compiledClass = compileCode(userCode);

            // Step 3: Run test cases
            List<TestCase> testCases = visibleOnly
                    ? question.getTestCases().stream().filter(tc -> !tc.isHidden()).toList()
                    : question.getTestCases();

            for (TestCase testCase : testCases) {
                TestResult testResult = runTestCase(compiledClass, testCase, question.getMethodSignature());
                testResults.add(testResult);
            }

            // Step 4: Calculate results
            long passedCount = testResults.stream().filter(TestResult::isPassed).count();
            result.setSuccess(passedCount == testResults.size());
            result.setPassedTests((int) passedCount);
            result.setTotalTests(testResults.size());
            result.setResults(testResults);

        } catch (SecurityException e) {
            result.setSuccess(false);
            result.setError("Security violation: " + e.getMessage());
            result.setTotalTests(0);
            result.setPassedTests(0);
        } catch (CompilationException e) {
            result.setSuccess(false);
            result.setError("Compilation error: " + e.getMessage());
            result.setTotalTests(0);
            result.setPassedTests(0);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setError("Runtime error: " + e.getMessage());
            result.setTotalTests(testResults.size());
            result.setPassedTests((int) testResults.stream().filter(TestResult::isPassed).count());
            result.setResults(testResults);
        }

        long executionTime = System.currentTimeMillis() - startTime;
        result.setExecutionTime(executionTime);

        return result;
    }

    /**
     * Validates user code for security threats.
     */
    private void validateCode(String code) throws SecurityException {
        // Blacklist dangerous APIs and operations
        List<String> forbidden = Arrays.asList(
                "System.exit",
                "Runtime.getRuntime()",
                "Runtime.exec",
                "ProcessBuilder",
                "java.io.File",
                "java.nio.file.Files",
                "java.net.Socket",
                "java.net.ServerSocket",
                "java.net.URL",
                "java.net.HttpURLConnection",
                "ClassLoader",
                "sun.misc.Unsafe",
                "reflection.Field.setAccessible",
                "System.setSecurityManager",
                "Thread.stop()",
                "native "  // Prevent native method calls
        );

        for (String bad : forbidden) {
            if (code.contains(bad)) {
                throw new SecurityException("Forbidden API detected: " + bad);
            }
        }

        // Check for excessive length (prevent DoS)
        if (code.length() > 50000) {
            throw new SecurityException("Code exceeds maximum length of 50,000 characters");
        }
    }

    /**
     * Compiles Java code in-memory using the Java Compiler API.
     */
    private Class<?> compileCode(String code) throws CompilationException {
        try {
            // Extract class name from code
            String className = extractClassName(code);
            if (className == null) {
                throw new CompilationException("Could not find public class declaration");
            }

            // Get system Java compiler
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                throw new CompilationException("Java compiler not available. Ensure you're running on JDK, not JRE.");
            }

            // Create in-memory file manager
            InMemoryFileManager fileManager = new InMemoryFileManager(
                    compiler.getStandardFileManager(null, null, null)
            );

            // Create in-memory source file
            JavaFileObject sourceFile = new InMemoryJavaFile(className, code);

            // Compile
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            JavaCompiler.CompilationTask task = compiler.getTask(
                    null,
                    fileManager,
                    diagnostics,
                    null,
                    null,
                    Collections.singletonList(sourceFile)
            );

            boolean success = task.call();
            if (!success) {
                StringBuilder errorMsg = new StringBuilder();
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    errorMsg.append("Line ").append(diagnostic.getLineNumber())
                            .append(": ").append(diagnostic.getMessage(null))
                            .append("\n");
                }
                throw new CompilationException(errorMsg.toString());
            }

            // Load the compiled class (and any nested classes)
            Map<String, byte[]> allClassBytes = fileManager.getAllClassBytes();
            InMemoryClassLoader classLoader = new InMemoryClassLoader(allClassBytes);
            return classLoader.loadClass(className);

        } catch (Exception e) {
            if (e instanceof CompilationException) {
                throw (CompilationException) e;
            }
            throw new CompilationException("Compilation failed: " + e.getMessage());
        }
    }

    /**
     * Extracts the public class name from Java source code.
     */
    private String extractClassName(String code) {
        // Try to find "public class ClassName" first
        java.util.regex.Pattern publicPattern = java.util.regex.Pattern.compile("public\\s+class\\s+(\\w+)");
        java.util.regex.Matcher publicMatcher = publicPattern.matcher(code);
        if (publicMatcher.find()) {
            return publicMatcher.group(1);
        }
        
        // Fallback: try to find just "class ClassName" (without public modifier)
        // But prioritize non-nested classes (not preceded by "static class")
        java.util.regex.Pattern classPattern = java.util.regex.Pattern.compile("(?<!static\\s)\\bclass\\s+(\\w+)");
        java.util.regex.Matcher classMatcher = classPattern.matcher(code);
        if (classMatcher.find()) {
            return classMatcher.group(1);
        }
        
        return null;
    }

    /**
     * Runs a single test case against the compiled code.
     */
    private TestResult runTestCase(Class<?> compiledClass, TestCase testCase, String methodSignature) {
        TestResult result = new TestResult();
        result.setTestId(testCase.getId());
        result.setInput(testCase.getInput());
        result.setExpectedOutput(testCase.getExpectedOutput());

        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            // Check if this is an operation-based test case
            if (testCase.getInput().contains("Operations:")) {
                return runOperationBasedTestCase(compiledClass, testCase, executor);
            }
            
            // Check if this is a counter simulation test (e.g., MutexCounter)
            if (isCounterSimulationTest(compiledClass, testCase)) {
                return runCounterSimulationTest(compiledClass, testCase, executor);
            }
            
            // Original single-method execution logic
            // Parse method name and parameter types from signature
            String methodName = extractMethodName(methodSignature);
            Class<?>[] parameterTypes = extractParameterTypes(methodSignature);
            Object[] args = parseTestInput(testCase.getInput(), parameterTypes);

            // Execute with timeout
            Future<Object> future = executor.submit(() -> {
                try {
                    Method method = findMethod(compiledClass, methodName, parameterTypes);
                    
                    // Make method accessible in case the class is not public
                    method.setAccessible(true);

                    // If method is static, invoke with null instance; otherwise create instance
                    if (java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
                        return method.invoke(null, args);
                    } else {
                        Object instance = compiledClass.getDeclaredConstructor().newInstance();
                        return method.invoke(instance, args);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            Object actualResult = future.get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
            String actualOutput = formatOutput(actualResult);

            // Compare results
            boolean passed = compareOutputs(actualOutput, testCase.getExpectedOutput());
            result.setPassed(passed);
            result.setActualOutput(actualOutput);

            if (!passed) {
                result.setError("Expected: " + testCase.getExpectedOutput() + ", but got: " + actualOutput);
            }

        } catch (TimeoutException e) {
            result.setPassed(false);
            result.setError("Time Limit Exceeded (2 seconds)");
        } catch (ExecutionException e) {
            result.setPassed(false);
            Throwable cause = e.getCause();
            String errorMsg;
            if (cause != null) {
                errorMsg = cause.getClass().getSimpleName();
                if (cause.getMessage() != null && !cause.getMessage().isEmpty()) {
                    errorMsg += ": " + cause.getMessage();
                }
                // Get the root cause if there's a chain
                Throwable rootCause = cause;
                while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
                    rootCause = rootCause.getCause();
                }
                if (rootCause != cause) {
                    if (rootCause.getMessage() != null && !rootCause.getMessage().isEmpty()) {
                        errorMsg += " (Caused by: " + rootCause.getClass().getSimpleName() + ": " + rootCause.getMessage() + ")";
                    } else {
                        errorMsg += " (Caused by: " + rootCause.getClass().getSimpleName() + ")";
                    }
                }
            } else {
                errorMsg = "Runtime error";
            }
            result.setError(errorMsg);
            if (cause != null) {
                cause.printStackTrace(); // For debugging
            }
        } catch (Exception e) {
            result.setPassed(false);
            String errorMsg = e.getClass().getSimpleName();
            if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                errorMsg += ": " + e.getMessage();
            }
            result.setError(errorMsg);
            e.printStackTrace(); // For debugging
        }

        return result;
    }

    /**
     * Handles operation-based test cases where multiple method calls are executed in sequence.
     */
    private TestResult runOperationBasedTestCase(Class<?> compiledClass, TestCase testCase, ExecutorService executor) {
        TestResult result = new TestResult();
        result.setTestId(testCase.getId());
        result.setInput(testCase.getInput());
        result.setExpectedOutput(testCase.getExpectedOutput());

        try {
            String input = testCase.getInput();
            
            // Parse operations and arguments
            String[] operations = parseArrayFromInput(input, "Operations:");
            String[] argumentSets = parseArgumentSets(input);
            
            if (operations.length != argumentSets.length) {
                result.setPassed(false);
                result.setError("Mismatch between operations and arguments count");
                return result;
            }

            // Execute operations
            Future<String> future = executor.submit(() -> {
                List<String> results = new ArrayList<>();
                Object instance = null;

                for (int i = 0; i < operations.length; i++) {
                    String operation = operations[i].replace("\"", "").trim();
                    String args = argumentSets[i];

                    if (operation.equals(compiledClass.getSimpleName())) {
                        // Constructor call
                        instance = compiledClass.getDeclaredConstructor().newInstance();
                        results.add("null");
                    } else {
                        // Method call
                        Object[] parsedArgs = parseOperationArguments(args);
                        Method method = findMethodByName(compiledClass, operation, parsedArgs.length);
                        method.setAccessible(true);
                        
                        Object returnValue = method.invoke(instance, parsedArgs);
                        results.add(returnValue == null ? "null" : String.valueOf(returnValue));
                    }
                }

                return "[" + String.join(", ", results) + "]";
            });

            String actualOutput = future.get(TIMEOUT_MS * operations.length, TimeUnit.MILLISECONDS);
            result.setActualOutput(actualOutput);

            boolean passed = actualOutput.equals(testCase.getExpectedOutput());
            result.setPassed(passed);

            if (!passed) {
                result.setError("Expected: " + testCase.getExpectedOutput() + ", but got: " + actualOutput);
            }

        } catch (TimeoutException e) {
            result.setPassed(false);
            result.setError("Time Limit Exceeded");
        } catch (Exception e) {
            result.setPassed(false);
            result.setError("Error executing operations: " + e.getMessage());
            e.printStackTrace(); // For debugging
        }

        return result;
    }

    /**
     * Parses array notation from input string.
     */
    private String[] parseArrayFromInput(String input, String prefix) {
        int startIdx = input.indexOf(prefix);
        if (startIdx == -1) return new String[0];
        
        startIdx = input.indexOf("[", startIdx);
        int endIdx = input.indexOf("]", startIdx);
        
        String arrayContent = input.substring(startIdx + 1, endIdx);
        return arrayContent.split(",\\s*(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    }

    /**
     * Parses argument sets from the input.
     */
    private String[] parseArgumentSets(String input) {
        int startIdx = input.indexOf("Arguments:");
        if (startIdx == -1) return new String[0];
        
        startIdx = input.indexOf("[", startIdx);
        int endIdx = findMatchingBracket(input, startIdx);
        
        String content = input.substring(startIdx + 1, endIdx);
        List<String> argSets = new ArrayList<>();
        
        int depth = 0;
        StringBuilder current = new StringBuilder();
        
        for (char c : content.toCharArray()) {
            if (c == '[') depth++;
            else if (c == ']') depth--;
            else if (c == ',' && depth == 0) {
                argSets.add(current.toString().trim());
                current = new StringBuilder();
                continue;
            }
            current.append(c);
        }
        argSets.add(current.toString().trim());
        
        return argSets.toArray(new String[0]);
    }

    /**
     * Finds the matching closing bracket.
     */
    private int findMatchingBracket(String s, int openIdx) {
        int depth = 1;
        for (int i = openIdx + 1; i < s.length(); i++) {
            if (s.charAt(i) == '[') depth++;
            else if (s.charAt(i) == ']') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    /**
     * Parses arguments for a single operation.
     */
    private Object[] parseOperationArguments(String args) {
        args = args.trim();
        if (args.equals("[]") || args.isEmpty()) {
            return new Object[0];
        }
        
        // Remove outer brackets
        if (args.startsWith("[") && args.endsWith("]")) {
            args = args.substring(1, args.length() - 1);
        }
        
        if (args.isEmpty()) return new Object[0];
        
        List<Object> result = new ArrayList<>();
        String[] parts = args.split(",\\s*(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        
        for (String part : parts) {
            result.add(parseValueHeuristic(part.trim()));
        }
        
        return result.toArray();
    }

    /**
     * Finds method by name and parameter count.
     */
    private Method findMethodByName(Class<?> clazz, String methodName, int paramCount) throws NoSuchMethodException {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName) && method.getParameterCount() == paramCount) {
                return method;
            }
        }
        throw new NoSuchMethodException("Method " + methodName + " with " + paramCount + " parameters not found");
    }
    
    /**
     * Checks if this is a counter simulation test (MutexCounter, etc.)
     */
    private boolean isCounterSimulationTest(Class<?> clazz, TestCase testCase) {
        // Check if the class has increment() and getValue() methods
        try {
            clazz.getDeclaredMethod("increment");
            clazz.getDeclaredMethod("getValue");
            // Input format is "initialValue,numThreads,incrementsPerThread"
            String input = testCase.getInput();
            return input.matches("\\d+,\\d+,\\d+");
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
    
    /**
     * Runs a counter simulation test (e.g., MutexCounter)
     */
    private TestResult runCounterSimulationTest(Class<?> compiledClass, TestCase testCase, ExecutorService executor) {
        TestResult result = new TestResult();
        result.setTestId(testCase.getId());
        result.setInput(testCase.getInput());
        result.setExpectedOutput(testCase.getExpectedOutput());

        try {
            String input = testCase.getInput();
            String[] parts = input.split(",");
            
            if (parts.length != 3) {
                result.setPassed(false);
                result.setError("Invalid input format for counter simulation test");
                return result;
            }
            
            int initialValue = Integer.parseInt(parts[0].trim());
            int numThreads = Integer.parseInt(parts[1].trim());
            int incrementsPerThread = Integer.parseInt(parts[2].trim());

            Future<String> future = executor.submit(() -> {
                try {
                    // Create instance with initial value
                    Object instance;
                    try {
                        java.lang.reflect.Constructor<?> constructor = compiledClass.getDeclaredConstructor(int.class);
                        constructor.setAccessible(true);
                        instance = constructor.newInstance(initialValue);
                    } catch (NoSuchMethodException e) {
                        // Try no-arg constructor
                        java.lang.reflect.Constructor<?> constructor = compiledClass.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        instance = constructor.newInstance();
                    }
                    
                    // Get increment and getValue methods
                    Method incrementMethod = compiledClass.getDeclaredMethod("increment");
                    Method getValueMethod = compiledClass.getDeclaredMethod("getValue");
                    incrementMethod.setAccessible(true);
                    getValueMethod.setAccessible(true);
                    
                    final Object finalInstance = instance;
                    
                    // Create and start threads
                    List<Thread> threads = new ArrayList<>();
                    for (int i = 0; i < numThreads; i++) {
                        Thread thread = new Thread(() -> {
                            try {
                                for (int j = 0; j < incrementsPerThread; j++) {
                                    incrementMethod.invoke(finalInstance);
                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                        threads.add(thread);
                        thread.start();
                    }
                    
                    // Wait for all threads to complete
                    for (Thread thread : threads) {
                        thread.join();
                    }
                    
                    // Get final value
                    Object value = getValueMethod.invoke(finalInstance);
                    return String.valueOf(value);
                    
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            String actualOutput = future.get(TIMEOUT_MS * numThreads, TimeUnit.MILLISECONDS);
            result.setActualOutput(actualOutput);

            boolean passed = actualOutput.equals(testCase.getExpectedOutput());
            result.setPassed(passed);

            if (!passed) {
                result.setError("Expected: " + testCase.getExpectedOutput() + ", but got: " + actualOutput);
            }

        } catch (TimeoutException e) {
            result.setPassed(false);
            result.setError("Time Limit Exceeded");
        } catch (Exception e) {
            result.setPassed(false);
            result.setError("Error executing counter simulation: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Formats the output of a method call to a string representation.
     * Handles Lists specially to match expected output format.
     */
    private String formatOutput(Object result) {
        if (result == null) {
            return "null";
        }
        
        // Handle List output - convert to JSON-like array format
        if (result instanceof List) {
            List<?> list = (List<?>) result;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) sb.append(", ");
                Object item = list.get(i);
                if (item instanceof String) {
                    sb.append("\"").append(item).append("\"");
                } else {
                    sb.append(item);
                }
            }
            sb.append("]");
            return sb.toString();
        }

        // Handle array output - convert to readable format
        if (result.getClass().isArray()) {
            Class<?> componentType = result.getClass().getComponentType();

            // Handle primitive arrays
            if (componentType == int.class) {
                return Arrays.toString((int[]) result);
            } else if (componentType == long.class) {
                return Arrays.toString((long[]) result);
            } else if (componentType == double.class) {
                return Arrays.toString((double[]) result);
            } else if (componentType == float.class) {
                return Arrays.toString((float[]) result);
            } else if (componentType == boolean.class) {
                return Arrays.toString((boolean[]) result);
            } else if (componentType == char.class) {
                return Arrays.toString((char[]) result);
            } else if (componentType == byte.class) {
                return Arrays.toString((byte[]) result);
            } else if (componentType == short.class) {
                return Arrays.toString((short[]) result);
            } else if (componentType.isArray()) {
                // Handle multi-dimensional arrays
                return Arrays.deepToString((Object[]) result);
            } else {
                // Handle object arrays (String[], Integer[], etc.)
                return Arrays.toString((Object[]) result);
            }
        }

        return String.valueOf(result);
    }

    /**
     * Compares actual and expected outputs.
     * Handles both exact matching and logical ordering for concurrent outputs.
     */
    private boolean compareOutputs(String actual, String expected) {
        // First try exact match
        if (actual.equals(expected)) {
            return true;
        }
        
        // Check if this is a concurrent execution test (contains thread/worker messages)
        if (isConcurrentOutput(expected)) {
            return validateConcurrentOutput(actual, expected);
        }
        
        return false;
    }
    
    /**
     * Checks if the output appears to be from concurrent execution.
     */
    private boolean isConcurrentOutput(String output) {
        // Check for common concurrent programming patterns
        boolean hasWorkerPattern = output.contains("Worker") && 
            (output.contains("ready") || output.contains("finished") || output.contains("processing"));
        boolean hasThreadPattern = output.contains("Thread") && 
            (output.contains("started") || output.contains("Barrier") || output.contains("Phase"));
        boolean hasProcessPattern = output.contains("Main process") || 
            output.contains("All workers finished");
        
        return hasWorkerPattern || hasThreadPattern || hasProcessPattern;
    }
    
    /**
     * Validates concurrent output by checking logical ordering constraints.
     */
    private boolean validateConcurrentOutput(String actual, String expected) {
        try {
            // Parse both outputs - handle both list format and newline-separated format
            List<String> actualList = parseOutputToList(actual);
            List<String> expectedList = parseOutputToList(expected);
            
            // Must have same number of messages
            if (actualList.size() != expectedList.size()) {
                return false;
            }
            
            // Check if all expected messages are present
            List<String> actualSorted = new ArrayList<>(actualList);
            List<String> expectedSorted = new ArrayList<>(expectedList);
            Collections.sort(actualSorted);
            Collections.sort(expectedSorted);
            
            if (!actualSorted.equals(expectedSorted)) {
                return false; // Different messages entirely
            }
            
            // Validate ordering constraints for CountDownLatch pattern
            if (expected.contains("Main process") && expected.contains("Worker")) {
                return validateCountDownLatchOrdering(actualList);
            }
            
            // Validate ordering constraints for "Worker processing/finished" pattern
            if (expected.contains("processing") && expected.contains("finished")) {
                return validateWorkerTaskOrdering(actualList);
            }
            
            // Validate ordering constraints for Barrier pattern
            if (expected.contains("Barrier") || expected.contains("Phase")) {
                return validateBarrierOrdering(actualList);
            }
            
            // For other concurrent patterns, just check that all messages are present
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Parses output to a list - handles both JSON array format and newline-separated format.
     */
    private List<String> parseOutputToList(String output) {
        output = output.trim();
        
        // Try JSON array format first
        if (output.startsWith("[") && output.endsWith("]")) {
            return parseListOutput(output);
        }
        
        // Otherwise, split by newlines
        if (output.contains("\n")) {
            String[] lines = output.split("\n");
            List<String> result = new ArrayList<>();
            for (String line : lines) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    result.add(trimmed);
                }
            }
            return result;
        }
        
        // Single line
        return output.isEmpty() ? new ArrayList<>() : List.of(output);
    }
    
    /**
     * Parses a list-formatted string into a List of strings.
     */
    private List<String> parseListOutput(String output) {
        output = output.trim();
        if (!output.startsWith("[") || !output.endsWith("]")) {
            return Collections.emptyList();
        }
        
        String content = output.substring(1, output.length() - 1);
        if (content.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<String> result = new ArrayList<>();
        int depth = 0;
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            
            if (c == '"' && (i == 0 || content.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes && depth == 0) {
                String item = current.toString().trim();
                if (item.startsWith("\"") && item.endsWith("\"")) {
                    item = item.substring(1, item.length() - 1);
                }
                result.add(item);
                current = new StringBuilder();
                continue;
            } else if (c == '[' && !inQuotes) {
                depth++;
            } else if (c == ']' && !inQuotes) {
                depth--;
            }
            
            current.append(c);
        }
        
        String item = current.toString().trim();
        if (!item.isEmpty()) {
            if (item.startsWith("\"") && item.endsWith("\"")) {
                item = item.substring(1, item.length() - 1);
            }
            result.add(item);
        }
        
        return result;
    }
    
    /**
     * Validates ordering constraints for CountDownLatch pattern.
     */
    private boolean validateCountDownLatchOrdering(List<String> messages) {
        int initiatedIdx = -1;
        int waitingIdx = -1;
        int proceedingIdx = -1;
        List<Integer> workerIndices = new ArrayList<>();
        
        for (int i = 0; i < messages.size(); i++) {
            String msg = messages.get(i);
            if (msg.contains("initiated")) {
                initiatedIdx = i;
            } else if (msg.contains("waiting")) {
                waitingIdx = i;
            } else if (msg.contains("proceeding")) {
                proceedingIdx = i;
            } else if (msg.contains("Worker") && msg.contains("ready")) {
                workerIndices.add(i);
            }
        }
        
        // "initiated" must be first
        if (initiatedIdx != 0) {
            return false;
        }
        
        // "proceeding" must be last
        if (proceedingIdx != messages.size() - 1) {
            return false;
        }
        
        // "waiting" must come before "proceeding"
        if (waitingIdx >= proceedingIdx) {
            return false;
        }
        
        // All worker messages must come before "proceeding"
        for (int workerIdx : workerIndices) {
            if (workerIdx >= proceedingIdx) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Validates ordering constraints for Worker processing/finished pattern.
     */
    private boolean validateWorkerTaskOrdering(List<String> messages) {
        int lastFinished = -1;
        int reportStartIdx = -1;
        int reportCompleteIdx = -1;
        
        for (int i = 0; i < messages.size(); i++) {
            String msg = messages.get(i);
            if (msg.contains("Worker") && msg.contains("finished")) {
                lastFinished = i;
            } else if (msg.contains("All workers finished") || msg.contains("Starting report generation")) {
                reportStartIdx = i;
            } else if (msg.contains("Report generation complete")) {
                reportCompleteIdx = i;
            }
        }
        
        // All "Worker X finished" messages must come before "All workers finished"
        if (lastFinished >= 0 && reportStartIdx >= 0 && lastFinished >= reportStartIdx) {
            return false;
        }
        
        // "All workers finished" must come before "Report generation complete"
        if (reportStartIdx >= 0 && reportCompleteIdx >= 0 && reportStartIdx >= reportCompleteIdx) {
            return false;
        }
        
        // "Report generation complete" should be last (if it exists)
        if (reportCompleteIdx >= 0 && reportCompleteIdx != messages.size() - 1) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Validates ordering constraints for CyclicBarrier pattern.
     */
    private boolean validateBarrierOrdering(List<String> messages) {
        // Group messages by phase
        Map<Integer, List<String>> phaseMessages = new HashMap<>();
        int currentPhase = 0;
        
        for (String msg : messages) {
            if (msg.contains("Phase")) {
                // Extract phase number
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("Phase (\\d+)");
                java.util.regex.Matcher matcher = pattern.matcher(msg);
                if (matcher.find()) {
                    currentPhase = Integer.parseInt(matcher.group(1));
                }
            }
            
            phaseMessages.computeIfAbsent(currentPhase, k -> new ArrayList<>()).add(msg);
        }
        
        // For each phase, validate that:
        // - "started" messages come before "finished" messages
        // - "finished" messages come before "passed Barrier" messages
        for (List<String> phasemsgs : phaseMessages.values()) {
            int lastFinished = -1;
            int lastBarrier = -1;
            
            for (int i = 0; i < phasemsgs.size(); i++) {
                String msg = phasemsgs.get(i);
                if (msg.contains("finished")) {
                    lastFinished = i;
                } else if (msg.contains("Barrier")) {
                    lastBarrier = i;
                }
            }
            
            // All barriers must come after all finished messages
            if (lastBarrier >= 0 && lastFinished >= 0 && lastBarrier < lastFinished) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Extracts method name from method signature.
     * Example: "public static boolean isValid(String s)" -> "isValid"
     */
    private String extractMethodName(String signature) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\s+(\\w+)\\s*\\(");
        java.util.regex.Matcher matcher = pattern.matcher(signature);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "solution"; // Default fallback
    }

    /**
     * Extracts parameter types from method signature.
     * Example: "public static String foo(int a, long b)" -> [int.class, long.class]
     */
    private Class<?>[] extractParameterTypes(String signature) {
        // Extract parameter list from signature
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\(([^)]*)\\)");
        java.util.regex.Matcher matcher = pattern.matcher(signature);

        if (!matcher.find()) {
            return new Class<?>[0];
        }

        String paramList = matcher.group(1).trim();
        if (paramList.isEmpty()) {
            return new Class<?>[0];
        }

        // Split parameters by comma (simple split - doesn't handle generic types perfectly)
        String[] params = paramList.split(",");
        List<Class<?>> types = new ArrayList<>();

        for (String param : params) {
            param = param.trim();
            // Extract just the type (first word before the parameter name)
            String[] parts = param.split("\\s+");
            if (parts.length > 0) {
                String typeName = parts[0].trim();
                Class<?> type = getClassForTypeName(typeName);
                if (type != null) {
                    types.add(type);
                }
            }
        }

        return types.toArray(new Class<?>[0]);
    }

    /**
     * Maps type name strings to Class objects.
     */
    private Class<?> getClassForTypeName(String typeName) {
        // Handle generic types like List<Integer>, java.util.List<String>, etc.
        if (typeName.startsWith("List<") || typeName.equals("List") || 
            typeName.startsWith("java.util.List<") || typeName.equals("java.util.List")) {
            return List.class;
        }

        return switch (typeName) {
            case "int" -> int.class;
            case "long" -> long.class;
            case "double" -> double.class;
            case "float" -> float.class;
            case "boolean" -> boolean.class;
            case "char" -> char.class;
            case "byte" -> byte.class;
            case "short" -> short.class;
            case "String" -> String.class;
            // 1D arrays
            case "int[]" -> int[].class;
            case "long[]" -> long[].class;
            case "double[]" -> double[].class;
            case "float[]" -> float[].class;
            case "boolean[]" -> boolean[].class;
            case "char[]" -> char[].class;
            case "byte[]" -> byte[].class;
            case "short[]" -> short[].class;
            case "String[]" -> String[].class;
            // 2D arrays
            case "int[][]" -> int[][].class;
            case "long[][]" -> long[][].class;
            case "double[][]" -> double[][].class;
            case "float[][]" -> float[][].class;
            case "boolean[][]" -> boolean[][].class;
            case "char[][]" -> char[][].class;
            case "byte[][]" -> byte[][].class;
            case "short[][]" -> short[][].class;
            case "String[][]" -> String[][].class;
            default -> {
                // Try to load the class by name
                try {
                    yield Class.forName(typeName);
                } catch (ClassNotFoundException e) {
                    yield null;
                }
            }
        };
    }

    /**
     * Parses test input string into actual Java objects based on expected parameter types.
     */
    private Object[] parseTestInput(String input, Class<?>[] parameterTypes) {
        input = input.trim();

        // Handle parameter assignments: "param1 = value1, param2 = value2"
        if (input.contains("=")) {
            List<Object> params = new ArrayList<>();
            
            // Split by top-level commas (not inside brackets)
            List<String> assignments = splitByTopLevelComma(input);
            
            for (int i = 0; i < assignments.size(); i++) {
                String assignment = assignments.get(i);
                String[] parts = assignment.split("=", 2);
                if (parts.length == 2) {
                    String value = parts[1].trim();
                    Class<?> expectedType = i < parameterTypes.length ? parameterTypes[i] : null;
                    params.add(parseValueWithType(value, expectedType));
                }
            }

            return params.toArray();
        }

        // Handle single array or list parameter: "[1,2,3]" or ["AR", "RR"]
        if (parameterTypes.length == 1 && input.startsWith("[") && input.endsWith("]")) {
            // Check that there are no top-level commas
            List<String> topLevelParts = splitByTopLevelComma(input);
            if (topLevelParts.size() == 1) {
                Class<?> expectedType = parameterTypes[0];
                return new Object[]{parseValueWithType(input, expectedType)};
            }
        }

        // Handle multiple comma-separated values: "2, [100, 50]" or "2, 5"
        if (parameterTypes.length > 1) {
            // Use splitByTopLevelComma to avoid splitting inside brackets
            List<String> values = splitByTopLevelComma(input);
            List<Object> params = new ArrayList<>();

            for (int i = 0; i < values.size() && i < parameterTypes.length; i++) {
                String value = values.get(i).trim();
                Class<?> expectedType = parameterTypes[i];
                params.add(parseValueWithType(value, expectedType));
            }

            return params.toArray();
        }

        // Single value
        Class<?> expectedType = parameterTypes.length > 0 ? parameterTypes[0] : null;
        return new Object[]{parseValueWithType(input, expectedType)};
    }
    
    /**
     * Splits input by top-level commas (not inside brackets or quotes).
     */
    private List<String> splitByTopLevelComma(String input) {
        List<String> result = new ArrayList<>();
        int depth = 0;
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            
            // Track quote state (handle escaped quotes)
            if (c == '"' && (i == 0 || input.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
                current.append(c);
            } else if (c == '[' && !inQuotes) {
                depth++;
                current.append(c);
            } else if (c == ']' && !inQuotes) {
                depth--;
                current.append(c);
            } else if (c == ',' && depth == 0 && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        if (current.length() > 0) {
            result.add(current.toString());
        }
        
        return result;
    }

    /**
     * Parses a single value string into the appropriate Java type based on expected type.
     */
    private Object parseValueWithType(String value, Class<?> expectedType) {
        value = value.trim();

        // If no expected type, use heuristics
        if (expectedType == null) {
            return parseValueHeuristic(value);
        }

        // Handle array types (e.g., int[], String[])
        if (expectedType.isArray()) {
            return parseArrayValue(value, expectedType);
        }

        // Handle List types (e.g., List<Integer>)
        if (expectedType == List.class || expectedType.getName().contains("List")) {
            return parseListValue(value);
        }

        // Parse based on expected type
        if (expectedType == String.class) {
            // Handle strings: "\"hello\"" -> "hello"
            if (value.startsWith("\"") && value.endsWith("\"")) {
                return value.substring(1, value.length() - 1);
            }
            return value;
        } else if (expectedType == int.class || expectedType == Integer.class) {
            return Integer.parseInt(value);
        } else if (expectedType == long.class || expectedType == Long.class) {
            // Remove trailing L/l if present
            if (value.endsWith("L") || value.endsWith("l")) {
                value = value.substring(0, value.length() - 1);
            }
            return Long.parseLong(value);
        } else if (expectedType == double.class || expectedType == Double.class) {
            return Double.parseDouble(value);
        } else if (expectedType == float.class || expectedType == Float.class) {
            return Float.parseFloat(value);
        } else if (expectedType == boolean.class || expectedType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (expectedType == char.class || expectedType == Character.class) {
            return value.charAt(0);
        } else if (expectedType == byte.class || expectedType == Byte.class) {
            return Byte.parseByte(value);
        } else if (expectedType == short.class || expectedType == Short.class) {
            return Short.parseShort(value);
        }

        // Fallback to heuristic parsing
        return parseValueHeuristic(value);
    }
    
    /**
     * Parses an array value from string like "[1, 2, 3]" -> int[]{1, 2, 3}
     * Also handles 2D arrays like "[[1, 2], [3, 4]]" -> int[][]{{1, 2}, {3, 4}}
     */
    private Object parseArrayValue(String value, Class<?> arrayType) {
        value = value.trim();
        if (!value.startsWith("[") || !value.endsWith("]")) {
            // Return empty array if format is wrong
            return java.lang.reflect.Array.newInstance(arrayType.getComponentType(), 0);
        }

        String content = value.substring(1, value.length() - 1).trim();
        if (content.isEmpty()) {
            return java.lang.reflect.Array.newInstance(arrayType.getComponentType(), 0);
        }

        Class<?> componentType = arrayType.getComponentType();

        // Handle 2D arrays (when component type is itself an array)
        if (componentType.isArray()) {
            // Split by top-level commas to get each inner array
            List<String> innerArrayStrings = splitByTopLevelComma(content);
            Object result = java.lang.reflect.Array.newInstance(componentType, innerArrayStrings.size());

            for (int i = 0; i < innerArrayStrings.size(); i++) {
                String innerArrayString = innerArrayStrings.get(i).trim();
                // Recursively parse each inner array
                Object innerArray = parseArrayValue(innerArrayString, componentType);
                java.lang.reflect.Array.set(result, i, innerArray);
            }

            return result;
        }

        // Handle 1D arrays - split by comma for primitive/simple types
        String[] parts = content.split(",");

        // Handle int[]
        if (componentType == int.class) {
            int[] result = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                result[i] = Integer.parseInt(parts[i].trim());
            }
            return result;
        }

        // Handle long[]
        if (componentType == long.class) {
            long[] result = new long[parts.length];
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i].trim();
                if (part.endsWith("L") || part.endsWith("l")) {
                    part = part.substring(0, part.length() - 1);
                }
                result[i] = Long.parseLong(part);
            }
            return result;
        }

        // Handle double[]
        if (componentType == double.class) {
            double[] result = new double[parts.length];
            for (int i = 0; i < parts.length; i++) {
                result[i] = Double.parseDouble(parts[i].trim());
            }
            return result;
        }

        // Handle String[]
        if (componentType == String.class) {
            // Use quote-aware splitting (same regex as parseListValue)
            String[] stringParts = content.split(",\\s*(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            String[] result = new String[stringParts.length];
            for (int i = 0; i < stringParts.length; i++) {
                String part = stringParts[i].trim();
                if (part.startsWith("\"") && part.endsWith("\"")) {
                    part = part.substring(1, part.length() - 1);
                }
                result[i] = part;
            }
            return result;
        }

        // Generic fallback
        return java.lang.reflect.Array.newInstance(componentType, 0);
    }
    
    /**
     * Parses a List value from string. Detects element type automatically:
     * - "[\"ARRIVE\", \"LEAVE\"]" -> List.of("ARRIVE", "LEAVE") (List<String>)
     * - "[100, 200]" -> List.of(100, 200) (List<Integer>)
     * - "[[1,2], [3,4]]" -> List.of(List.of(1,2), List.of(3,4)) (List<List<Integer>>)
     */
    private List<?> parseListValue(String value) {
        value = value.trim();
        if (!value.startsWith("[") || !value.endsWith("]")) {
            return new ArrayList<>();
        }

        String content = value.substring(1, value.length() - 1).trim();
        if (content.isEmpty()) {
            return new ArrayList<>();
        }

        // Use splitByTopLevelComma to handle nested structures
        List<String> parts = splitByTopLevelComma(content);

        // Detect type based on first element
        if (parts.size() > 0) {
            String firstPart = parts.get(0).trim();
            
            // Check if first element is a nested list: [[...]]
            if (firstPart.startsWith("[") && firstPart.endsWith("]")) {
                // Parse as List<List<?>> - nested list
                List<List<?>> result = new ArrayList<>();
                for (String part : parts) {
                    part = part.trim();
                    // Recursively parse each inner list
                    result.add(parseListValue(part));
                }
                return result;
            }
            
            // Check if first element is a quoted string
            if (firstPart.startsWith("\"") && firstPart.endsWith("\"")) {
                // Parse as List<String>
                List<String> result = new ArrayList<>();
                for (String part : parts) {
                    part = part.trim();
                    // Remove surrounding quotes
                    if (part.startsWith("\"") && part.endsWith("\"")) {
                        result.add(part.substring(1, part.length() - 1));
                    } else {
                        result.add(part);
                    }
                }
                return result;
            }
            
            // Check if first element contains a decimal point (indicates Double)
            if (firstPart.contains(".")) {
                // Parse as List<Double>
                List<Double> result = new ArrayList<>();
                for (String part : parts) {
                    part = part.trim();
                    try {
                        result.add(Double.parseDouble(part));
                    } catch (NumberFormatException e) {
                        result.add(0.0);
                    }
                }
                return result;
            }
        }

        // Parse as List<Integer> (default for whole numbers)
        List<Integer> result = new ArrayList<>();
        for (String part : parts) {
            part = part.trim();
            try {
                result.add(Integer.parseInt(part));
            } catch (NumberFormatException e) {
                // Try parsing as other types if needed
                result.add(0);
            }
        }
        return result;
    }

    /**
     * Parses a value using heuristics when no type information is available.
     */
    private Object parseValueHeuristic(String value) {
        value = value.trim();

        // Handle strings: "\"hello\""
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }

        // Handle booleans
        if (value.equals("true") || value.equals("false")) {
            return Boolean.parseBoolean(value);
        }

        // Handle long integers (ends with L or l)
        if (value.endsWith("L") || value.endsWith("l")) {
            return Long.parseLong(value.substring(0, value.length() - 1));
        }

        // Handle doubles (contains decimal point)
        if (value.contains(".")) {
            return Double.parseDouble(value);
        }

        // Handle integers
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // If all else fails, return as string
            return value;
        }
    }

    /**
     * Finds the method in the class that matches the method name and parameter types.
     */
    private Method findMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes) throws NoSuchMethodException {
        try {
            // Try exact match first
            return clazz.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            // If exact match fails, try to find by name and parameter count
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName) && method.getParameterCount() == parameterTypes.length) {
                    return method;
                }
            }
            throw new NoSuchMethodException("Method " + methodName + " not found with " + parameterTypes.length + " parameters");
        }
    }

    /**
     * Custom exception for compilation errors.
     */
    public static class CompilationException extends Exception {
        public CompilationException(String message) {
            super(message);
        }
    }

    /**
     * In-memory representation of a Java source file.
     */
    private static class InMemoryJavaFile extends SimpleJavaFileObject {
        private final String code;

        protected InMemoryJavaFile(String className, String code) {
            super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }

    /**
     * In-memory file manager for compiled bytecode.
     */
    private static class InMemoryFileManager extends ForwardingJavaFileManager<JavaFileManager> {
        private final Map<String, ByteArrayOutputStream> classBytes = new HashMap<>();

        protected InMemoryFileManager(JavaFileManager fileManager) {
            super(fileManager);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            classBytes.put(className, baos);
            return new SimpleJavaFileObject(URI.create("bytes:///" + className), kind) {
                @Override
                public OutputStream openOutputStream() {
                    return baos;
                }
            };
        }

        public Map<String, byte[]> getAllClassBytes() {
            Map<String, byte[]> result = new HashMap<>();
            for (Map.Entry<String, ByteArrayOutputStream> entry : classBytes.entrySet()) {
                result.put(entry.getKey(), entry.getValue().toByteArray());
            }
            return result;
        }
    }

    /**
     * ClassLoader for loading classes from byte arrays.
     */
    private static class InMemoryClassLoader extends ClassLoader {
        private final Map<String, byte[]> classBytes;

        public InMemoryClassLoader(Map<String, byte[]> classBytes) {
            this.classBytes = classBytes;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] bytes = classBytes.get(name);
            if (bytes != null) {
                return defineClass(name, bytes, 0, bytes.length);
            }
            return super.findClass(name);
        }
    }
}