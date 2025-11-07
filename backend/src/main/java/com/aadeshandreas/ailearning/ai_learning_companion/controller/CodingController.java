package com.aadeshandreas.ailearning.ai_learning_companion.controller;

import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.*;
import com.aadeshandreas.ailearning.ai_learning_companion.model.common.ApiResponse;
import com.aadeshandreas.ailearning.ai_learning_companion.service.CodingQuestionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/content")
public class CodingController {
    private static final Logger logger = LoggerFactory.getLogger(CodingController.class);
    private final CodingQuestionService codingQuestionService;

    public CodingController(CodingQuestionService codingQuestionService) {
        this.codingQuestionService = codingQuestionService;
    }

    /**
     * Extracts 5 programming topics from the document previously uploaded in the user's session.
     *
     * @return A {@link ResponseEntity} wrapping a generic {@link ApiResponse}. On success,
     * the ApiResponse's data field will contain a {@link CodingTopicList} object with 5 topics.
     * On failure, it will contain an error message and code with null data.
     */
    @GetMapping(value = "/coding-topics")
    public ResponseEntity<ApiResponse<?>> extractCodingTopics() {
        try {
            CodingTopicList topics = codingQuestionService.extractCodingTopics();
            ApiResponse<CodingTopicList> successResponse = new ApiResponse<>("Success", "200_OK", topics);
            return ResponseEntity.ok(successResponse);
        } catch (IllegalStateException e) {
            logger.warn("Coding topic extraction failed because no document was uploaded: {}", e.getMessage());

            ApiResponse<Void> errorResponse = new ApiResponse<>(
                    "No document found. Please upload a document first.",
                    "NO_DOCUMENT_UPLOADED",
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            logger.error("Error extracting coding topics", e);

            ApiResponse<Void> errorResponse = new ApiResponse<>(
                    "Unable to extract coding topics",
                    "INTERNAL_SERVER_ERROR",
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Generates a coding question for the selected topic and difficulty level.
     *
     * @param request The {@link CodingQuestionRequest} containing topicId and difficulty
     * @return A {@link ResponseEntity} wrapping a generic {@link ApiResponse}. On success,
     * the ApiResponse's data field will contain a {@link CodingQuestion} object.
     * On failure, it will contain an error message and code with null data.
     */
    @PostMapping(value = "/coding-question")
    public ResponseEntity<ApiResponse<?>> generateCodingQuestion(
            @Valid @RequestBody CodingQuestionRequest request) {
        try {
            CodingQuestion question = codingQuestionService.generateCodingQuestion(
                    request.getTopicId(),
                    request.getDifficulty()
            );
            ApiResponse<CodingQuestion> successResponse = new ApiResponse<>("Success", "200_OK", question);
            return ResponseEntity.ok(successResponse);
        } catch (IllegalStateException e) {
            logger.warn("Coding question generation failed: {}", e.getMessage());

            ApiResponse<Void> errorResponse = new ApiResponse<>(
                    e.getMessage(),
                    "NO_TOPICS_FOUND",
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid topic ID: {}", e.getMessage());

            ApiResponse<Void> errorResponse = new ApiResponse<>(
                    e.getMessage(),
                    "INVALID_TOPIC_ID",
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            logger.error("Error generating coding question", e);

            ApiResponse<Void> errorResponse = new ApiResponse<>(
                    "Unable to generate coding question",
                    "INTERNAL_SERVER_ERROR",
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Submits user code for validation against all test cases (visible + hidden).
     * This is the final submission that determines if the solution is correct.
     *
     * @param submission The {@link CodeSubmission} containing questionId and user code
     * @return A {@link ResponseEntity} wrapping a generic {@link ApiResponse}. On success,
     * the ApiResponse's data field will contain an {@link ExecutionResult} with all test results.
     */
    @PostMapping(value = "/coding-question/submit")
    public ResponseEntity<ApiResponse<?>> submitCodingQuestion(
            @Valid @RequestBody CodeSubmission submission) {
        try {
            ExecutionResult result = codingQuestionService.submitCodingQuestion(
                    submission.getQuestionId(),
                    submission.getCode()
            );
            ApiResponse<ExecutionResult> successResponse = new ApiResponse<>("Success", "200_OK", result);
            return ResponseEntity.ok(successResponse);
        } catch (IllegalArgumentException e) {
            logger.warn("Code submission failed: {}", e.getMessage());

            ApiResponse<Void> errorResponse = new ApiResponse<>(
                    e.getMessage(),
                    "INVALID_QUESTION_ID",
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            logger.error("Error executing submitted code", e);

            ApiResponse<Void> errorResponse = new ApiResponse<>(
                    "Unable to execute code: " + e.getMessage(),
                    "EXECUTION_ERROR",
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Tests user code against only visible test cases (for practice/debugging).
     * This allows users to test their solution before final submission.
     *
     * @param submission The {@link CodeSubmission} containing questionId and user code
     * @return A {@link ResponseEntity} wrapping a generic {@link ApiResponse}. On success,
     * the ApiResponse's data field will contain an {@link ExecutionResult} with visible test results only.
     */
    @PostMapping(value = "/coding-question/test")
    public ResponseEntity<ApiResponse<?>> testCodingQuestion(
            @Valid @RequestBody CodeSubmission submission) {
        try {
            ExecutionResult result = codingQuestionService.testCodingQuestion(
                    submission.getQuestionId(),
                    submission.getCode()
            );
            ApiResponse<ExecutionResult> successResponse = new ApiResponse<>("Success", "200_OK", result);
            return ResponseEntity.ok(successResponse);
        } catch (IllegalArgumentException e) {
            logger.warn("Code test failed: {}", e.getMessage());

            ApiResponse<Void> errorResponse = new ApiResponse<>(
                    e.getMessage(),
                    "INVALID_QUESTION_ID",
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            logger.error("Error testing code", e);

            ApiResponse<Void> errorResponse = new ApiResponse<>(
                    "Unable to test code: " + e.getMessage(),
                    "EXECUTION_ERROR",
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
