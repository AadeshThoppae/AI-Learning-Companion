package com.aadeshandreas.ailearning.ai_learning_companion.service.coding.parsing;

import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.parsing.util.ParsingUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of InputParser that handles various input formats.
 */
@Component
public class DefaultInputParser implements InputParser {

    @Override
    public Object[] parseTestInput(String input, Class<?>[] parameterTypes) {
        input = input.trim();

        // Handle parameter assignments: "param1 = value1, param2 = value2"
        if (input.contains("=")) {
            List<Object> params = new ArrayList<>();

            // Split by top-level commas (not inside brackets)
            List<String> assignments = ParsingUtils.splitByTopLevelComma(input);

            for (int i = 0; i < assignments.size(); i++) {
                String assignment = assignments.get(i);
                String[] parts = assignment.split("=", 2);
                if (parts.length == 2) {
                    String value = parts[1].trim();
                    Class<?> expectedType = i < parameterTypes.length ? parameterTypes[i] : null;
                    params.add(parseValueWithType(value, expectedType));
                }
            }

            return params.toArray();
        }

        // Handle single array or list parameter: "[1,2,3]" or ["AR", "RR"]
        if (parameterTypes.length == 1 && input.startsWith("[") && input.endsWith("]")) {
            // Check that there are no top-level commas
            List<String> topLevelParts = ParsingUtils.splitByTopLevelComma(input);
            if (topLevelParts.size() == 1) {
                Class<?> expectedType = parameterTypes[0];
                return new Object[]{parseValueWithType(input, expectedType)};
            }
        }

        // Handle multiple comma-separated values: "2, [100, 50]" or "2, 5"
        if (parameterTypes.length > 1) {
            // Use splitByTopLevelComma to avoid splitting inside brackets
            List<String> values = ParsingUtils.splitByTopLevelComma(input);
            List<Object> params = new ArrayList<>();

            for (int i = 0; i < values.size() && i < parameterTypes.length; i++) {
                String value = values.get(i).trim();
                Class<?> expectedType = parameterTypes[i];
                params.add(parseValueWithType(value, expectedType));
            }

            return params.toArray();
        }

        // Single value
        Class<?> expectedType = parameterTypes.length > 0 ? parameterTypes[0] : null;
        return new Object[]{parseValueWithType(input, expectedType)};
    }

    @Override
    public Object parseValueWithType(String value, Class<?> expectedType) {
        value = value.trim();

        // If no expected type, use heuristics
        if (expectedType == null) {
            return parseValueHeuristic(value);
        }

        // Handle array types (e.g., int[], String[])
        if (expectedType.isArray()) {
            return parseArrayValue(value, expectedType);
        }

        // Handle List types (e.g., List<Integer>)
        if (expectedType == List.class || expectedType.getName().contains("List")) {
            return parseListValue(value);
        }

        // Parse based on expected type
        if (expectedType == String.class) {
            // Handle strings: "\"hello\"" -> "hello"
            if (value.startsWith("\"") && value.endsWith("\"")) {
                return value.substring(1, value.length() - 1);
            }
            return value;
        } else if (expectedType == int.class || expectedType == Integer.class) {
            return Integer.parseInt(value);
        } else if (expectedType == long.class || expectedType == Long.class) {
            // Remove trailing L/l if present
            if (value.endsWith("L") || value.endsWith("l")) {
                value = value.substring(0, value.length() - 1);
            }
            return Long.parseLong(value);
        } else if (expectedType == double.class || expectedType == Double.class) {
            return Double.parseDouble(value);
        } else if (expectedType == float.class || expectedType == Float.class) {
            return Float.parseFloat(value);
        } else if (expectedType == boolean.class || expectedType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (expectedType == char.class || expectedType == Character.class) {
            return value.charAt(0);
        } else if (expectedType == byte.class || expectedType == Byte.class) {
            return Byte.parseByte(value);
        } else if (expectedType == short.class || expectedType == Short.class) {
            return Short.parseShort(value);
        }

        // Fallback to heuristic parsing
        return parseValueHeuristic(value);
    }

    /**
     * Parses an array value from string like "[1, 2, 3]" -> int[]{1, 2, 3}
     * Also handles 2D arrays like "[[1, 2], [3, 4]]" -> int[][]{{1, 2}, {3, 4}}
     */
    private Object parseArrayValue(String value, Class<?> arrayType) {
        value = value.trim();
        if (!value.startsWith("[") || !value.endsWith("]")) {
            // Return empty array if format is wrong
            return java.lang.reflect.Array.newInstance(arrayType.getComponentType(), 0);
        }

        String content = value.substring(1, value.length() - 1).trim();
        if (content.isEmpty()) {
            return java.lang.reflect.Array.newInstance(arrayType.getComponentType(), 0);
        }

        Class<?> componentType = arrayType.getComponentType();

        // Handle 2D arrays (when component type is itself an array)
        if (componentType.isArray()) {
            // Split by top-level commas to get each inner array
            List<String> innerArrayStrings = ParsingUtils.splitByTopLevelComma(content);
            Object result = java.lang.reflect.Array.newInstance(componentType, innerArrayStrings.size());

            for (int i = 0; i < innerArrayStrings.size(); i++) {
                String innerArrayString = innerArrayStrings.get(i).trim();
                // Recursively parse each inner array
                Object innerArray = parseArrayValue(innerArrayString, componentType);
                java.lang.reflect.Array.set(result, i, innerArray);
            }

            return result;
        }

        // Handle 1D arrays - split by comma for primitive/simple types
        String[] parts = content.split(",");

        // Handle int[]
        if (componentType == int.class) {
            int[] result = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                result[i] = Integer.parseInt(parts[i].trim());
            }
            return result;
        }

        // Handle long[]
        if (componentType == long.class) {
            long[] result = new long[parts.length];
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i].trim();
                if (part.endsWith("L") || part.endsWith("l")) {
                    part = part.substring(0, part.length() - 1);
                }
                result[i] = Long.parseLong(part);
            }
            return result;
        }

        // Handle double[]
        if (componentType == double.class) {
            double[] result = new double[parts.length];
            for (int i = 0; i < parts.length; i++) {
                result[i] = Double.parseDouble(parts[i].trim());
            }
            return result;
        }

        // Handle String[]
        if (componentType == String.class) {
            // Use quote-aware splitting (same regex as parseListValue)
            String[] stringParts = content.split(",\\s*(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            String[] result = new String[stringParts.length];
            for (int i = 0; i < stringParts.length; i++) {
                String part = stringParts[i].trim();
                if (part.startsWith("\"") && part.endsWith("\"")) {
                    part = part.substring(1, part.length() - 1);
                }
                result[i] = part;
            }
            return result;
        }

        // Generic fallback
        return java.lang.reflect.Array.newInstance(componentType, 0);
    }

    /**
     * Parses a List value from string. Detects element type automatically:
     * - "[\"ARRIVE\", \"LEAVE\"]" -> List.of("ARRIVE", "LEAVE") (List<String>)
     * - "[100, 200]" -> List.of(100, 200) (List<Integer>)
     * - "[[1,2], [3,4]]" -> List.of(List.of(1,2), List.of(3,4)) (List<List<Integer>>)
     */
    private List<?> parseListValue(String value) {
        value = value.trim();
        if (!value.startsWith("[") || !value.endsWith("]")) {
            return new ArrayList<>();
        }

        String content = value.substring(1, value.length() - 1).trim();
        if (content.isEmpty()) {
            return new ArrayList<>();
        }

        // Use splitByTopLevelComma to handle nested structures
        List<String> parts = ParsingUtils.splitByTopLevelComma(content);

        // Detect type based on first element
        if (!parts.isEmpty()) {
            String firstPart = parts.get(0).trim();

            // Check if first element is a nested list: [[...]]
            if (firstPart.startsWith("[") && firstPart.endsWith("]")) {
                // Parse as List<List<?>> - nested list
                List<List<?>> result = new ArrayList<>();
                for (String part : parts) {
                    part = part.trim();
                    // Recursively parse each inner list
                    result.add(parseListValue(part));
                }
                return result;
            }

            // Check if first element is a quoted string
            if (firstPart.startsWith("\"") && firstPart.endsWith("\"")) {
                // Parse as List<String>
                List<String> result = new ArrayList<>();
                for (String part : parts) {
                    part = part.trim();
                    // Remove surrounding quotes
                    if (part.startsWith("\"") && part.endsWith("\"")) {
                        result.add(part.substring(1, part.length() - 1));
                    } else {
                        result.add(part);
                    }
                }
                return result;
            }

            // Check if first element contains a decimal point (indicates Double)
            if (firstPart.contains(".")) {
                // Parse as List<Double>
                List<Double> result = new ArrayList<>();
                for (String part : parts) {
                    part = part.trim();
                    try {
                        result.add(Double.parseDouble(part));
                    } catch (NumberFormatException e) {
                        result.add(0.0);
                    }
                }
                return result;
            }
        }

        // Parse as List<Integer> (default for whole numbers)
        List<Integer> result = new ArrayList<>();
        for (String part : parts) {
            part = part.trim();
            try {
                result.add(Integer.parseInt(part));
            } catch (NumberFormatException e) {
                // Try parsing as other types if needed
                result.add(0);
            }
        }
        return result;
    }

    /**
     * Parses a value using heuristics when no type information is available.
     */
    private Object parseValueHeuristic(String value) {
        value = value.trim();

        // Handle strings: "\"hello\""
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }

        // Handle booleans
        if (value.equals("true") || value.equals("false")) {
            return Boolean.parseBoolean(value);
        }

        // Handle long integers (ends with L or l)
        if (value.endsWith("L") || value.endsWith("l")) {
            return Long.parseLong(value.substring(0, value.length() - 1));
        }

        // Handle doubles (contains decimal point)
        if (value.contains(".")) {
            return Double.parseDouble(value);
        }

        // Handle integers
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // If all else fails, return as string
            return value;
        }
    }

    /**
     * Parses arguments for a single operation (used by OperationBasedExecutionStrategy).
     */
    public Object[] parseOperationArguments(String args) {
        args = args.trim();
        if (args.equals("[]") || args.isEmpty()) {
            return new Object[0];
        }

        // Remove outer brackets
        if (args.startsWith("[") && args.endsWith("]")) {
            args = args.substring(1, args.length() - 1);
        }

        if (args.isEmpty()) return new Object[0];

        List<Object> result = new ArrayList<>();
        String[] parts = args.split(",\\s*(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        for (String part : parts) {
            result.add(parseValueHeuristic(part.trim()));
        }

        return result.toArray();
    }
}
