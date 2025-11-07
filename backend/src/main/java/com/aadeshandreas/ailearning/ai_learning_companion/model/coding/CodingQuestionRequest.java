package com.aadeshandreas.ailearning.ai_learning_companion.model.coding;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO for generating a coding question.
 * Contains the selected topic ID and desired difficulty level.
 */
@Getter
@Setter
public class CodingQuestionRequest {
    private int topicId;

    @NotNull(message = "Difficulty is required")
    private String difficulty;      // EASY, MEDIUM, HARD (case-insensitive)

    /**
     * Normalizes difficulty to uppercase.
     */
    public String getDifficulty() {
        return difficulty != null ? difficulty.toUpperCase() : null;
    }
}