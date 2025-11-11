package com.aadeshandreas.ailearning.ai_learning_companion.service.coding.parsing.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for parsing input strings.
 */
public class ParsingUtils {

    /**
     * Splits input by top-level commas (not inside brackets or quotes).
     */
    public static List<String> splitByTopLevelComma(String input) {
        List<String> result = new ArrayList<>();
        int depth = 0;
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            // Track quote state (handle escaped quotes)
            if (c == '"' && (i == 0 || input.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
                current.append(c);
            } else if (c == '[' && !inQuotes) {
                depth++;
                current.append(c);
            } else if (c == ']' && !inQuotes) {
                depth--;
                current.append(c);
            } else if (c == ',' && depth == 0 && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }

        if (!current.isEmpty()) {
            result.add(current.toString());
        }

        return result;
    }

    /**
     * Finds the matching closing bracket.
     */
    public static int findMatchingBracket(String s, int openIdx) {
        int depth = 1;
        for (int i = openIdx + 1; i < s.length(); i++) {
            if (s.charAt(i) == '[') {
                depth++;
            } else if (s.charAt(i) == ']') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Parses array notation from input string.
     */
    public static String[] parseArrayFromInput(String input, String prefix) {
        int startIdx = input.indexOf(prefix);
        if (startIdx == -1) return new String[0];

        startIdx = input.indexOf("[", startIdx);
        int endIdx = input.indexOf("]", startIdx);

        String arrayContent = input.substring(startIdx + 1, endIdx);
        return arrayContent.split(",\\s*(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    }

    /**
     * Parses argument sets from the input (for operation-based tests).
     */
    public static String[] parseArgumentSets(String input) {
        int startIdx = input.indexOf("Arguments:");
        if (startIdx == -1) return new String[0];

        startIdx = input.indexOf("[", startIdx);
        int endIdx = findMatchingBracket(input, startIdx);

        String content = input.substring(startIdx + 1, endIdx);
        List<String> argSets = new ArrayList<>();

        int depth = 0;
        StringBuilder current = new StringBuilder();

        for (char c : content.toCharArray()) {
            if (c == '[') {
                depth++;
            } else if (c == ']') {
                depth--;
            } else if (c == ',' && depth == 0) {
                argSets.add(current.toString().trim());
                current = new StringBuilder();
                continue;
            }
            current.append(c);
        }
        argSets.add(current.toString().trim());

        return argSets.toArray(new String[0]);
    }

    /**
     * Parses a list-formatted string into a List of strings.
     */
    public static List<String> parseListOutput(String output) {
        output = output.trim();
        if (!output.startsWith("[") || !output.endsWith("]")) {
            return new ArrayList<>();
        }

        String content = output.substring(1, output.length() - 1);
        if (content.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<String> result = new ArrayList<>();
        int depth = 0;
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            if (c == '"' && (i == 0 || content.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes && depth == 0) {
                String item = current.toString().trim();
                if (item.startsWith("\"") && item.endsWith("\"")) {
                    item = item.substring(1, item.length() - 1);
                }
                result.add(item);
                current = new StringBuilder();
                continue;
            } else if (c == '[' && !inQuotes) {
                depth++;
            } else if (c == ']' && !inQuotes) {
                depth--;
            }

            current.append(c);
        }

        String item = current.toString().trim();
        if (!item.isEmpty()) {
            if (item.startsWith("\"") && item.endsWith("\"")) {
                item = item.substring(1, item.length() - 1);
            }
            result.add(item);
        }

        return result;
    }
}
