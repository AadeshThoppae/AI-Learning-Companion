package com.aadeshandreas.ailearning.ai_learning_companion.service.coding.compilation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for extracting class names from Java source code.
 */
public class ClassNameExtractor {

    private static final Pattern PUBLIC_CLASS_PATTERN = Pattern.compile("public\\s+class\\s+(\\w+)");
    private static final Pattern CLASS_PATTERN = Pattern.compile("(?<!static\\s)\\bclass\\s+(\\w+)");

    /**
     * Extracts the public class name from Java source code.
     *
     * @param code The Java source code
     * @return The class name, or null if not found
     */
    public static String extractClassName(String code) {
        // Try to find "public class ClassName" first
        Matcher publicMatcher = PUBLIC_CLASS_PATTERN.matcher(code);
        if (publicMatcher.find()) {
            return publicMatcher.group(1);
        }

        // Fallback: try to find just "class ClassName" (without public modifier)
        // But prioritize non-nested classes (not preceded by "static class")
        Matcher classMatcher = CLASS_PATTERN.matcher(code);
        if (classMatcher.find()) {
            return classMatcher.group(1);
        }

        return null;
    }
}
