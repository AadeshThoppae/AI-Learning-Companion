package com.aadeshandreas.ailearning.ai_learning_companion.service.coding.execution.strategy;

import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.CodingQuestion;
import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.TestCase;
import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.TestResult;
import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.comparison.OutputComparator;
import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.formatting.OutputFormatter;
import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.parsing.InputParser;
import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.parsing.MethodSignatureParser;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Execution strategy for single method invocation tests.
 * This is the default strategy for most test cases.
 */
public class SingleMethodExecutionStrategy implements ExecutionStrategy {

    private static final long TIMEOUT_MS = 2000; // 2 seconds per test case

    private final InputParser inputParser;
    private final OutputFormatter outputFormatter;
    private final OutputComparator outputComparator;

    public SingleMethodExecutionStrategy(InputParser inputParser, OutputFormatter outputFormatter, OutputComparator outputComparator) {
        this.inputParser = inputParser;
        this.outputFormatter = outputFormatter;
        this.outputComparator = outputComparator;
    }

    @Override
    public boolean canHandle(Class<?> compiledClass, TestCase testCase) {
        // This is the fallback strategy - it can handle anything that doesn't match other strategies
        // It will be checked last by the ExecutionStrategyFactory
        return true;
    }

    @Override
    public TestResult execute(Class<?> compiledClass, TestCase testCase, CodingQuestion question, ExecutorService executor) {
        TestResult result = new TestResult();
        result.setTestId(testCase.getId());
        result.setInput(testCase.getInput());
        result.setExpectedOutput(testCase.getExpectedOutput());

        try {
            // Parse method name and parameter types from signature
            String methodName = MethodSignatureParser.extractMethodName(question.getMethodSignature());
            Class<?>[] parameterTypes = MethodSignatureParser.extractParameterTypes(question.getMethodSignature());
            Object[] args = inputParser.parseTestInput(testCase.getInput(), parameterTypes);

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
            String actualOutput = outputFormatter.formatOutput(actualResult);

            // Compare results
            boolean passed = outputComparator.compareOutputs(actualOutput, testCase.getExpectedOutput());
            result.setPassed(passed);
            result.setActualOutput(actualOutput);

            if (!passed) {
                result.setError("Expected: " + testCase.getExpectedOutput() + ", but got: " + actualOutput);
            }

        } catch (TimeoutException e) {
            result.setPassed(false);
            result.setError("Time Limit Exceeded (2 seconds)");
        } catch (Exception e) {
            result.setPassed(false);
            result.setError(buildErrorMessage(e));
        }

        return result;
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
     * Builds an error message from an exception with proper cause chain handling.
     */
    private String buildErrorMessage(Exception e) {
        Throwable cause = e.getCause();
        if (cause != null) {
            String errorMsg = cause.getClass().getSimpleName();
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
            return errorMsg;
        }

        String errorMsg = e.getClass().getSimpleName();
        if (e.getMessage() != null && !e.getMessage().isEmpty()) {
            errorMsg += ": " + e.getMessage();
        }
        return errorMsg;
    }
}
