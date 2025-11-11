package com.aadeshandreas.ailearning.ai_learning_companion.service.coding.formatting;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Default implementation of OutputFormatter that handles various output types.
 */
@Component
public class DefaultOutputFormatter implements OutputFormatter {

    @Override
    public String formatOutput(Object result) {
        if (result == null) {
            return "null";
        }

        // Handle List output - convert to JSON-like array format
        if (result instanceof List<?> list) {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) sb.append(", ");
                Object item = list.get(i);
                if (item instanceof String) {
                    sb.append("\"").append(item).append("\"");
                } else {
                    sb.append(item);
                }
            }
            sb.append("]");
            return sb.toString();
        }

        // Handle array output - convert to readable format
        if (result.getClass().isArray()) {
            Class<?> componentType = result.getClass().getComponentType();

            // Handle primitive arrays
            if (componentType == int.class) {
                return Arrays.toString((int[]) result);
            } else if (componentType == long.class) {
                return Arrays.toString((long[]) result);
            } else if (componentType == double.class) {
                return Arrays.toString((double[]) result);
            } else if (componentType == float.class) {
                return Arrays.toString((float[]) result);
            } else if (componentType == boolean.class) {
                return Arrays.toString((boolean[]) result);
            } else if (componentType == char.class) {
                return Arrays.toString((char[]) result);
            } else if (componentType == byte.class) {
                return Arrays.toString((byte[]) result);
            } else if (componentType == short.class) {
                return Arrays.toString((short[]) result);
            } else if (componentType.isArray()) {
                // Handle multi-dimensional arrays
                return Arrays.deepToString((Object[]) result);
            } else {
                // Handle object arrays (String[], Integer[], etc.)
                return Arrays.toString((Object[]) result);
            }
        }

        return String.valueOf(result);
    }
}
