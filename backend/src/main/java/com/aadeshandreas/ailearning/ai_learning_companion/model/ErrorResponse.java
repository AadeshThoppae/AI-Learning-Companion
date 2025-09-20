package com.aadeshandreas.ailearning.ai_learning_companion.model;

import lombok.Getter;

@Getter
public class ErrorResponse implements ApiResponse {
    private final String message;
    private final String code;

    public ErrorResponse(String message, String code) {
        this.message = message;
        this.code = code;
    }
}
