package com.aadeshandreas.ailearning.ai_learning_companion.service.coding.parsing;

/**
 * Interface for parsing test input strings into Java objects.
 */
public interface InputParser {

    /**
     * Parses test input string into an array of objects based on expected parameter types.
     *
     * @param input The input string from the test case
     * @param parameterTypes The expected parameter types
     * @return Array of parsed objects
     */
    Object[] parseTestInput(String input, Class<?>[] parameterTypes);

    /**
     * Parses a single value with type information.
     *
     * @param value The value string to parse
     * @param expectedType The expected type
     * @return The parsed object
     */
    Object parseValueWithType(String value, Class<?> expectedType);
}
