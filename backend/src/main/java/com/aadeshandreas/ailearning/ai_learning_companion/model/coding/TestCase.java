package com.aadeshandreas.ailearning.ai_learning_companion.model.coding;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a test case for validating a user's code solution.
 * Test cases can be visible (shown to user) or hidden (for final validation).
 */
@Getter
@Setter
public class TestCase {
    private int id;
    private String input;           // Serialized input parameters (e.g., "[2,1,3]")
    private String expectedOutput;  // Expected return value (e.g., "true")
    private boolean hidden;         // false = shown to user, true = validation only
}