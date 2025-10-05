package com.aadeshandreas.ailearning.ai_learning_companion.model;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for accepting raw text input from the frontend.
 * Used when users paste notes directly instead of uploading a PDF.
 */

public record DocumentText(@NotBlank(message = "Text cannot be empty") String text){}
