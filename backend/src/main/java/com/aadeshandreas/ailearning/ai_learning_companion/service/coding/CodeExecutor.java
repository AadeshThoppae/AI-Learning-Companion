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
    private static final int MAX_OUTPUT_LENGTH = 10000; // Prevent memory issues

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

            // Load the compiled class
            byte[] classBytes = fileManager.getClassBytes(className);
            InMemoryClassLoader classLoader = new InMemoryClassLoader(classBytes, className);
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
        // Simple regex to find "public class ClassName"
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("public\\s+class\\s+(\\w+)");
        java.util.regex.Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
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

        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            // Parse method name and parameters from signature
            String methodName = extractMethodName(methodSignature);
            Object[] args = parseTestInput(testCase.getInput());

            // Execute with timeout
            Future<Object> future = executor.submit(() -> {
                try {
                    Object instance = compiledClass.getDeclaredConstructor().newInstance();
                    Method method = findMethod(compiledClass, methodName, args);
                    return method.invoke(instance, args);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            Object actualResult = future.get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
            String actualOutput = String.valueOf(actualResult);

            // Compare results
            boolean passed = actualOutput.equals(testCase.getExpectedOutput());
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
            result.setError(cause != null ? cause.getMessage() : "Runtime error");
        } catch (Exception e) {
            result.setPassed(false);
            result.setError("Error: " + e.getMessage());
        } finally {
            executor.shutdownNow();
        }

        return result;
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
     * Parses test input string into actual Java objects.
     * This is a simplified version - you may need to expand based on your needs.
     */
    private Object[] parseTestInput(String input) {
        // Simple parsing for common types
        // For more complex types, you'd need a JSON parser
        input = input.trim();

        // Handle arrays: "[1,2,3]"
        if (input.startsWith("[") && input.endsWith("]")) {
            String content = input.substring(1, input.length() - 1);
            if (content.isEmpty()) {
                return new Object[]{new int[0]};
            }
            String[] parts = content.split(",");
            int[] arr = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                arr[i] = Integer.parseInt(parts[i].trim());
            }
            return new Object[]{arr};
        }

        // Handle strings: "\"hello\""
        if (input.startsWith("\"") && input.endsWith("\"")) {
            return new Object[]{input.substring(1, input.length() - 1)};
        }

        // Handle integers
        try {
            return new Object[]{Integer.parseInt(input)};
        } catch (NumberFormatException e) {
            // Handle booleans
            if (input.equals("true") || input.equals("false")) {
                return new Object[]{Boolean.parseBoolean(input)};
            }
        }

        return new Object[]{input};
    }

    /**
     * Finds the method in the class that matches the method name and parameter types.
     */
    private Method findMethod(Class<?> clazz, String methodName, Object[] args) throws NoSuchMethodException {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName) && method.getParameterCount() == args.length) {
                return method;
            }
        }
        throw new NoSuchMethodException("Method " + methodName + " not found");
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

        public byte[] getClassBytes(String className) {
            return classBytes.get(className).toByteArray();
        }
    }

    /**
     * ClassLoader for loading classes from byte arrays.
     */
    private static class InMemoryClassLoader extends ClassLoader {
        private final byte[] classBytes;
        private final String className;

        public InMemoryClassLoader(byte[] classBytes, String className) {
            this.classBytes = classBytes;
            this.className = className;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (name.equals(className)) {
                return defineClass(name, classBytes, 0, classBytes.length);
            }
            return super.findClass(name);
        }
    }
}