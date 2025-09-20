package com.aadeshandreas.ailearning.ai_learning_companion.model;

import lombok.Getter;

/**
 * A data model representing a standardized error response for the API.
 * This class is used to send consistent error information to the client
 * when a request fails.
 */
@Getter
public class ErrorResponse implements ApiResponse {
    private final String message;
    private final String code;

    /**
     * Constructs a new ErrorResponse with a specific message and code.
     *
     * @param message The detailed message explaining the error.
     * @param code    The specific error code.
     */
    public ErrorResponse(String message, String code) {
        this.message = message;
        this.code = code;
    }
}
