package com.aadeshandreas.ailearning.ai_learning_companion.model.coding;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Response DTO for code execution results.
 * Contains test results, success status, and execution metrics.
 */
@Getter
@Setter
public class ExecutionResult {
    private boolean success;            // All tests passed
    private int passedTests;
    private int totalTests;
    private List<TestResult> results;   // Individual test results
    private String error;               // Compilation or runtime error
    private long executionTime;         // Total execution time in milliseconds
}