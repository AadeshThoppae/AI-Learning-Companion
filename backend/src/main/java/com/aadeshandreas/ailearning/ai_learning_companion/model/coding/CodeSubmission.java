package com.aadeshandreas.ailearning.ai_learning_companion.model.coding;

import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO for code submission.
 * Contains the question ID and the user's complete code solution.
 */
@Getter
@Setter
public class CodeSubmission {
    private String questionId;
    private String code;            // User's complete solution
}