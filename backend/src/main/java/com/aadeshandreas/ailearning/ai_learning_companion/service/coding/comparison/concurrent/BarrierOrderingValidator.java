package com.aadeshandreas.ailearning.ai_learning_companion.service.coding.comparison.concurrent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates ordering constraints for CyclicBarrier pattern.
 */
public class BarrierOrderingValidator {

    private static final Pattern PHASE_PATTERN = Pattern.compile("Phase (\\d+)");

    public boolean validate(List<String> messages) {
        // Group messages by phase
        Map<Integer, List<String>> phaseMessages = new HashMap<>();
        int currentPhase = 0;

        for (String msg : messages) {
            if (msg.contains("Phase")) {
                // Extract phase number
                Matcher matcher = PHASE_PATTERN.matcher(msg);
                if (matcher.find()) {
                    currentPhase = Integer.parseInt(matcher.group(1));
                }
            }

            phaseMessages.computeIfAbsent(currentPhase, k -> new ArrayList<>()).add(msg);
        }

        // For each phase, validate that:
        // - "started" messages come before "finished" messages
        // - "finished" messages come before "passed Barrier" messages
        for (List<String> phasemsgs : phaseMessages.values()) {
            int lastFinished = -1;
            int lastBarrier = -1;

            for (int i = 0; i < phasemsgs.size(); i++) {
                String msg = phasemsgs.get(i);
                if (msg.contains("finished")) {
                    lastFinished = i;
                } else if (msg.contains("Barrier")) {
                    lastBarrier = i;
                }
            }

            // All barriers must come after all finished messages
            if (lastBarrier >= 0 && lastFinished >= 0 && lastBarrier < lastFinished) {
                return false;
            }
        }

        return true;
    }
}
