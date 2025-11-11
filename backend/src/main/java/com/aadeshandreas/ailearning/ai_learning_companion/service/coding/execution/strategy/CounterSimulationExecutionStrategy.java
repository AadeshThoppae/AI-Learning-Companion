package com.aadeshandreas.ailearning.ai_learning_companion.service.coding.execution.strategy;

import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.CodingQuestion;
import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.TestCase;
import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.TestResult;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Execution strategy for counter simulation tests (e.g., MutexCounter).
 * These tests involve concurrent thread execution to verify thread-safety.
 */
public class CounterSimulationExecutionStrategy implements ExecutionStrategy {

    private static final long TIMEOUT_MS = 2000; // Base timeout

    @Override
    public boolean canHandle(Class<?> compiledClass, TestCase testCase) {
        // Check if the class has increment() and getValue() methods
        try {
            compiledClass.getDeclaredMethod("increment");
            compiledClass.getDeclaredMethod("getValue");
            // Input format is "initialValue,numThreads,incrementsPerThread"
            String input = testCase.getInput();
            return input.matches("\\d+,\\d+,\\d+");
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    @Override
    public TestResult execute(Class<?> compiledClass, TestCase testCase, CodingQuestion question, ExecutorService executor) {
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
                        Constructor<?> constructor = compiledClass.getDeclaredConstructor(int.class);
                        constructor.setAccessible(true);
                        instance = constructor.newInstance(initialValue);
                    } catch (NoSuchMethodException e) {
                        // Try no-arg constructor
                        Constructor<?> constructor = compiledClass.getDeclaredConstructor();
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
}
