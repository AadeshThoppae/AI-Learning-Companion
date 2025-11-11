package com.aadeshandreas.ailearning.ai_learning_companion.service.coding.execution.strategy;

import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.CodingQuestion;
import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.TestCase;
import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.TestResult;

import java.util.concurrent.ExecutorService;

/**
 * Strategy interface for different types of test execution.
 */
public interface ExecutionStrategy {

    /**
     * Checks if this strategy can handle the given test case.
     *
     * @param compiledClass The compiled class
     * @param testCase The test case to check
     * @return true if this strategy can handle the test case
     */
    boolean canHandle(Class<?> compiledClass, TestCase testCase);

    /**
     * Executes the test case using this strategy.
     *
     * @param compiledClass The compiled class to test
     * @param testCase The test case to execute
     * @param question The coding question
     * @param executor The executor service for running with timeout
     * @return TestResult containing the outcome
     */
    TestResult execute(Class<?> compiledClass, TestCase testCase, CodingQuestion question, ExecutorService executor);
}
