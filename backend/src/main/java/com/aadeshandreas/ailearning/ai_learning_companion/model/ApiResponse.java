package com.aadeshandreas.ailearning.ai_learning_companion.model;

/**
 * A generic wrapper class for all API responses, implementing the "envelope" pattern.
 * <p>
 * This class standardizes the structure of responses from the server. Every response,
 * whether success or error, will be wrapped in this object, providing a consistent
 * format for the client to parse.
 *
 * @param <T> The type of the data payload being sent in the response.
 */
public record ApiResponse<T>(String message, String code, T data) {
    /**
     * Constructs a new ApiResponse.
     *
     * @param message A human-readable message describing the result of the operation.
     * @param code    A machine-readable code representing the status.
     * @param data    The data payload to be included in the response.
     */
    public ApiResponse {
    }
}
