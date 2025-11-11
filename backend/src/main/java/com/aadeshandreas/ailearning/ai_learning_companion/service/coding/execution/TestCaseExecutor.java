package com.aadeshandreas.ailearning.ai_learning_companion.service.coding.execution;

import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.CodingQuestion;
import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.TestCase;
import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.TestResult;

/**
 * Interface for executing test cases against compiled code.
 */
public interface TestCaseExecutor {

    /**
     * Executes a single test case against the compiled class.
     *
     * @param compiledClass The compiled class to test
     * @param testCase The test case to run
     * @param question The coding question containing method signature and context
     * @return TestResult containing the outcome of the test
     */
    TestResult executeTestCase(Class<?> compiledClass, TestCase testCase, CodingQuestion question);
}
