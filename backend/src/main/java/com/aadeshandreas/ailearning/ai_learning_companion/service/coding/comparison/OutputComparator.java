package com.aadeshandreas.ailearning.ai_learning_companion.service.coding.comparison;

/**
 * Interface for comparing actual and expected outputs.
 */
public interface OutputComparator {

    /**
     * Compares actual and expected outputs.
     * Handles both exact matching and logical ordering for concurrent outputs.
     *
     * @param actual The actual output from execution
     * @param expected The expected output from the test case
     * @return true if outputs match (exactly or logically)
     */
    boolean compareOutputs(String actual, String expected);
}
