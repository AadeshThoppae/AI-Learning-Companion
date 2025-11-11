package com.aadeshandreas.ailearning.ai_learning_companion.model.coding;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a sample input/output example for a coding question.
 * These examples are shown to the user to help them understand the problem.
 */
@Getter
@Setter
public class Example {
    private String input;           // e.g., "root = [2,1,3]"
    private String output;          // e.g., "true"
    private String explanation;     // Why this output is correct
}