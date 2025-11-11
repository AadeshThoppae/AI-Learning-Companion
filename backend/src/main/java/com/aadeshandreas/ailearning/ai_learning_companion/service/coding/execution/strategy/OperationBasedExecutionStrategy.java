package com.aadeshandreas.ailearning.ai_learning_companion.service.coding.execution.strategy;

import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.CodingQuestion;
import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.TestCase;
import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.TestResult;
import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.parsing.DefaultInputParser;
import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.parsing.util.ParsingUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Execution strategy for operation-based test cases where multiple method calls
 * are executed in sequence (e.g., LRU Cache, Stack, Queue implementations).
 */
public class OperationBasedExecutionStrategy implements ExecutionStrategy {

    private static final long TIMEOUT_MS = 2000; // 2 seconds per operation

    private final DefaultInputParser inputParser;

    public OperationBasedExecutionStrategy(DefaultInputParser inputParser) {
        this.inputParser = inputParser;
    }

    @Override
    public boolean canHandle(Class<?> compiledClass, TestCase testCase) {
        // Check if this is an operation-based test case
        return testCase.getInput().contains("Operations:");
    }

    @Override
    public TestResult execute(Class<?> compiledClass, TestCase testCase, CodingQuestion question, ExecutorService executor) {
        TestResult result = new TestResult();
        result.setTestId(testCase.getId());
        result.setInput(testCase.getInput());
        result.setExpectedOutput(testCase.getExpectedOutput());

        try {
            String input = testCase.getInput();

            // Parse operations and arguments
            String[] operations = ParsingUtils.parseArrayFromInput(input, "Operations:");
            String[] argumentSets = ParsingUtils.parseArgumentSets(input);

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
                        Object[] parsedArgs = inputParser.parseOperationArguments(args);
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
}
