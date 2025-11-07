package com.aadeshandreas.ailearning.ai_learning_companion.model.coding;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Represents the constraints and complexity expectations for a coding question.
 */
@Getter
@Setter
public class Constraints {
    private String timeComplexity;  // e.g., "O(n)"
    private String spaceComplexity; // e.g., "O(log n)"
    private List<String> rules;     // ["1 <= n <= 10^4", "Node values are unique"]
}