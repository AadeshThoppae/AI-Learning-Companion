package com.aadeshandreas.ailearning.ai_learning_companion.model;

/**
 * A marker interface for all API response models.
 * <p>
 * Implementing this interface allows classes like {@link Summary} and {@link ErrorResponse}
 * to be treated as a common type. This is useful for methods or controllers that can
 * return different kinds of responses (e.g., a success object or an error object).
 */
public interface ApiResponse {}
