package com.aadeshandreas.ailearning.ai_learning_companion.service.coding.validation;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Validates user-submitted code for security threats using a blacklist approach.
 */
@Component
public class SecurityCodeValidator implements CodeValidator {

    private static final int MAX_CODE_LENGTH = 50000;

    private static final List<String> FORBIDDEN_APIS = Arrays.asList(
            "System.exit",
            "Runtime.getRuntime()",
            "Runtime.exec",
            "ProcessBuilder",
            "java.io.File",
            "java.nio.file.Files",
            "java.net.Socket",
            "java.net.ServerSocket",
            "java.net.URL",
            "java.net.HttpURLConnection",
            "ClassLoader",
            "sun.misc.Unsafe",
            "reflection.Field.setAccessible",
            "System.setSecurityManager",
            "Thread.stop()",
            "native "  // Prevent native method calls
    );

    @Override
    public void validateCode(String code) throws SecurityException {
        // Check for forbidden APIs
        for (String forbidden : FORBIDDEN_APIS) {
            if (code.contains(forbidden)) {
                throw new SecurityException("Forbidden API detected: " + forbidden);
            }
        }

        // Check for excessive length (prevent DoS)
        if (code.length() > MAX_CODE_LENGTH) {
            throw new SecurityException("Code exceeds maximum length of " + MAX_CODE_LENGTH + " characters");
        }
    }
}
