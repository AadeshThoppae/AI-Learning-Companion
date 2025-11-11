package com.aadeshandreas.ailearning.ai_learning_companion.model.coding;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the result of running a single test case against user's code.
 */
@Getter
@Setter
public class TestResult {
    private int testId;
    private boolean passed;
    private String input;
    private String expectedOutput;
    private String actualOutput;
    private String error;           // If test failed (exception message)

    /**
     * Creates a failed test result due to timeout.
     */
    public static TestResult timeout(int testId, String input) {
        TestResult result = new TestResult();
        result.setTestId(testId);
        result.setPassed(false);
        result.setInput(input);
        result.setError("Time Limit Exceeded (2 seconds)");
        return result;
    }

    /**
     * Creates a failed test result due to runtime error.
     */
    public static TestResult error(int testId, String input, String errorMessage) {
        TestResult result = new TestResult();
        result.setTestId(testId);
        result.setPassed(false);
        result.setInput(input);
        result.setError(errorMessage);
        return result;
    }
}