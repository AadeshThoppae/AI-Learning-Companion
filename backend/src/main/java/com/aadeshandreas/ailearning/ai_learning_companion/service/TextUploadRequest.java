package com.aadeshandreas.ailearning.ai_learning_companion.service;
/**
 * DTO for accepting raw text input from the frontend.
 * Used when users paste notes directly instead of uploading a PDF.
 */

public record TextUploadRequest(String text){
    /**
     * Validates that the text is not null or empty.
     */
    public TextUploadRequest {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Text cannot be empty");
        }
    }
}
