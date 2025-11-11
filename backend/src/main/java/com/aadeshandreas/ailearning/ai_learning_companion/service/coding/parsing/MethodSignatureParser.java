package com.aadeshandreas.ailearning.ai_learning_companion.service.coding.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses method signatures to extract method names and parameter types.
 */
public class MethodSignatureParser {

    private static final Pattern METHOD_NAME_PATTERN = Pattern.compile("\\s+(\\w+)\\s*\\(");
    private static final Pattern PARAMETERS_PATTERN = Pattern.compile("\\(([^)]*)\\)");

    /**
     * Extracts method name from method signature.
     * Example: "public static boolean isValid(String s)" -> "isValid"
     */
    public static String extractMethodName(String signature) {
        Matcher matcher = METHOD_NAME_PATTERN.matcher(signature);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "solution"; // Default fallback
    }

    /**
     * Extracts parameter types from method signature.
     * Example: "public static String foo(int a, long b)" -> [int.class, long.class]
     */
    public static Class<?>[] extractParameterTypes(String signature) {
        Matcher matcher = PARAMETERS_PATTERN.matcher(signature);

        if (!matcher.find()) {
            return new Class<?>[0];
        }

        String paramList = matcher.group(1).trim();
        if (paramList.isEmpty()) {
            return new Class<?>[0];
        }

        // Split parameters by comma (simple split - doesn't handle generic types perfectly)
        String[] params = paramList.split(",");
        List<Class<?>> types = new ArrayList<>();

        for (String param : params) {
            param = param.trim();
            // Extract just the type (first word before the parameter name)
            String[] parts = param.split("\\s+");
            if (parts.length > 0) {
                String typeName = parts[0].trim();
                Class<?> type = getClassForTypeName(typeName);
                if (type != null) {
                    types.add(type);
                }
            }
        }

        return types.toArray(new Class<?>[0]);
    }

    /**
     * Maps type name strings to Class objects.
     */
    private static Class<?> getClassForTypeName(String typeName) {
        // Handle generic types like List<Integer>, java.util.List<String>, etc.
        if (typeName.startsWith("List<") || typeName.equals("List") ||
                typeName.startsWith("java.util.List<") || typeName.equals("java.util.List")) {
            return List.class;
        }

        return switch (typeName) {
            case "int" -> int.class;
            case "long" -> long.class;
            case "double" -> double.class;
            case "float" -> float.class;
            case "boolean" -> boolean.class;
            case "char" -> char.class;
            case "byte" -> byte.class;
            case "short" -> short.class;
            case "String" -> String.class;
            // 1D arrays
            case "int[]" -> int[].class;
            case "long[]" -> long[].class;
            case "double[]" -> double[].class;
            case "float[]" -> float[].class;
            case "boolean[]" -> boolean[].class;
            case "char[]" -> char[].class;
            case "byte[]" -> byte[].class;
            case "short[]" -> short[].class;
            case "String[]" -> String[].class;
            // 2D arrays
            case "int[][]" -> int[][].class;
            case "long[][]" -> long[][].class;
            case "double[][]" -> double[][].class;
            case "float[][]" -> float[][].class;
            case "boolean[][]" -> boolean[][].class;
            case "char[][]" -> char[][].class;
            case "byte[][]" -> byte[][].class;
            case "short[][]" -> short[][].class;
            case "String[][]" -> String[][].class;
            default -> {
                // Try to load the class by name
                try {
                    yield Class.forName(typeName);
                } catch (ClassNotFoundException e) {
                    yield null;
                }
            }
        };
    }
}
