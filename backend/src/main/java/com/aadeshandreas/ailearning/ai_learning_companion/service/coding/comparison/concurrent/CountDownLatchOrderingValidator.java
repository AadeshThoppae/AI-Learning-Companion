package com.aadeshandreas.ailearning.ai_learning_companion.service.coding.comparison.concurrent;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates ordering constraints for CountDownLatch pattern.
 */
public class CountDownLatchOrderingValidator {

    public boolean validate(List<String> messages) {
        int initiatedIdx = -1;
        int waitingIdx = -1;
        int proceedingIdx = -1;
        List<Integer> workerIndices = new ArrayList<>();

        for (int i = 0; i < messages.size(); i++) {
            String msg = messages.get(i);
            if (msg.contains("initiated")) {
                initiatedIdx = i;
            } else if (msg.contains("waiting")) {
                waitingIdx = i;
            } else if (msg.contains("proceeding")) {
                proceedingIdx = i;
            } else if (msg.contains("Worker") && msg.contains("ready")) {
                workerIndices.add(i);
            }
        }

        // "initiated" must be first
        if (initiatedIdx != 0) {
            return false;
        }

        // "proceeding" must be last
        if (proceedingIdx != messages.size() - 1) {
            return false;
        }

        // "waiting" must come before "proceeding"
        if (waitingIdx >= proceedingIdx) {
            return false;
        }

        // All worker messages must come before "proceeding"
        for (int workerIdx : workerIndices) {
            if (workerIdx >= proceedingIdx) {
                return false;
            }
        }

        return true;
    }
}
