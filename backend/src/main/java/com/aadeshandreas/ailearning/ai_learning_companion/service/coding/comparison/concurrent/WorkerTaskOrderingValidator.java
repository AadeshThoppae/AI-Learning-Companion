package com.aadeshandreas.ailearning.ai_learning_companion.service.coding.comparison.concurrent;

import java.util.List;

/**
 * Validates ordering constraints for Worker processing/finished pattern.
 */
public class WorkerTaskOrderingValidator {

    public boolean validate(List<String> messages) {
        int lastFinished = -1;
        int reportStartIdx = -1;
        int reportCompleteIdx = -1;

        for (int i = 0; i < messages.size(); i++) {
            String msg = messages.get(i);
            if (msg.contains("Worker") && msg.contains("finished")) {
                lastFinished = i;
            } else if (msg.contains("All workers finished") || msg.contains("Starting report generation")) {
                reportStartIdx = i;
            } else if (msg.contains("Report generation complete")) {
                reportCompleteIdx = i;
            }
        }

        // All "Worker X finished" messages must come before "All workers finished"
        if (reportStartIdx >= 0 && lastFinished >= reportStartIdx) {
            return false;
        }

        // "All workers finished" must come before "Report generation complete"
        if (reportCompleteIdx >= 0 && reportStartIdx >= reportCompleteIdx) {
            return false;
        }

        // "Report generation complete" should be last (if it exists)
        return reportCompleteIdx < 0 || reportCompleteIdx == messages.size() - 1;
    }
}
