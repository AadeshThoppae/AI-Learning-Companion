package com.aadeshandreas.ailearning.ai_learning_companion.model.coding;

import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO for generating a coding question.
 * Contains the selected topic ID and an optional regenerate flag.
 * The difficulty level is derived from the topic itself.
 */
@Getter
@Setter
public class CodingQuestionRequest {
    private int topicId;

    private boolean regenerate = false;
}