package com.aadeshandreas.ailearning.ai_learning_companion.service.coding.validation;

/**
 * Interface for validating user-submitted code for security threats and constraints.
 */
public interface CodeValidator {

    /**
     * Validates the provided code for security violations.
     *
     * @param code The Java source code to validate
     * @throws SecurityException if the code contains forbidden operations or exceeds limits
     */
    void validateCode(String code) throws SecurityException;
}
