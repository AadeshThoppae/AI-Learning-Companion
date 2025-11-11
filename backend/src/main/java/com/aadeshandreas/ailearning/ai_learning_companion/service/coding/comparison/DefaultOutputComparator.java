package com.aadeshandreas.ailearning.ai_learning_companion.service.coding.comparison;

import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.comparison.concurrent.BarrierOrderingValidator;
import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.comparison.concurrent.CountDownLatchOrderingValidator;
import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.comparison.concurrent.WorkerTaskOrderingValidator;
import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.parsing.util.ParsingUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Default implementation of OutputComparator that handles both exact matching
 * and concurrent output validation.
 */
@Component
public class DefaultOutputComparator implements OutputComparator {

    private final CountDownLatchOrderingValidator countDownLatchValidator = new CountDownLatchOrderingValidator();
    private final WorkerTaskOrderingValidator workerTaskValidator = new WorkerTaskOrderingValidator();
    private final BarrierOrderingValidator barrierValidator = new BarrierOrderingValidator();

    @Override
    public boolean compareOutputs(String actual, String expected) {
        // First try exact match
        if (actual.equals(expected)) {
            return true;
        }

        // Check if this is a concurrent execution test (contains thread/worker messages)
        if (isConcurrentOutput(expected)) {
            return validateConcurrentOutput(actual, expected);
        }

        return false;
    }

    /**
     * Checks if the output appears to be from concurrent execution.
     */
    private boolean isConcurrentOutput(String output) {
        // Check for common concurrent programming patterns
        boolean hasWorkerPattern = output.contains("Worker") &&
                (output.contains("ready") || output.contains("finished") || output.contains("processing"));
        boolean hasThreadPattern = output.contains("Thread") &&
                (output.contains("started") || output.contains("Barrier") || output.contains("Phase"));
        boolean hasProcessPattern = output.contains("Main process") ||
                output.contains("All workers finished");

        return hasWorkerPattern || hasThreadPattern || hasProcessPattern;
    }

    /**
     * Validates concurrent output by checking logical ordering constraints.
     */
    private boolean validateConcurrentOutput(String actual, String expected) {
        try {
            // Parse both outputs - handle both list format and newline-separated format
            List<String> actualList = parseOutputToList(actual);
            List<String> expectedList = parseOutputToList(expected);

            // Must have same number of messages
            if (actualList.size() != expectedList.size()) {
                return false;
            }

            // Check if all expected messages are present
            List<String> actualSorted = new ArrayList<>(actualList);
            List<String> expectedSorted = new ArrayList<>(expectedList);
            Collections.sort(actualSorted);
            Collections.sort(expectedSorted);

            if (!actualSorted.equals(expectedSorted)) {
                return false; // Different messages entirely
            }

            // Validate ordering constraints for CountDownLatch pattern
            if (expected.contains("Main process") && expected.contains("Worker")) {
                return countDownLatchValidator.validate(actualList);
            }

            // Validate ordering constraints for "Worker processing/finished" pattern
            if (expected.contains("processing") && expected.contains("finished")) {
                return workerTaskValidator.validate(actualList);
            }

            // Validate ordering constraints for Barrier pattern
            if (expected.contains("Barrier") || expected.contains("Phase")) {
                return barrierValidator.validate(actualList);
            }

            // For other concurrent patterns, just check that all messages are present
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Parses output to a list - handles both JSON array format and newline-separated format.
     */
    private List<String> parseOutputToList(String output) {
        output = output.trim();

        // Try JSON array format first
        if (output.startsWith("[") && output.endsWith("]")) {
            return ParsingUtils.parseListOutput(output);
        }

        // Otherwise, split by newlines
        if (output.contains("\n")) {
            String[] lines = output.split("\n");
            List<String> result = new ArrayList<>();
            for (String line : lines) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    result.add(trimmed);
                }
            }
            return result;
        }

        // Single line
        return output.isEmpty() ? new ArrayList<>() : List.of(output);
    }
}
