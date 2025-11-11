package com.aadeshandreas.ailearning.ai_learning_companion.service.coding.formatting;

/**
 * Interface for formatting method execution results to string representation.
 */
public interface OutputFormatter {

    /**
     * Formats the output of a method call to a string representation.
     *
     * @param result The result object from method execution
     * @return String representation of the result
     */
    String formatOutput(Object result);
}
