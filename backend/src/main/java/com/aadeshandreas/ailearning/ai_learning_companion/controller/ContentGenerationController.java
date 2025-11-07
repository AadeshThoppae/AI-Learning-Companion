package com.aadeshandreas.ailearning.ai_learning_companion.controller;

import com.aadeshandreas.ailearning.ai_learning_companion.model.common.ApiResponse;
import com.aadeshandreas.ailearning.ai_learning_companion.model.content.FlashcardList;
import com.aadeshandreas.ailearning.ai_learning_companion.model.content.Quiz;
import com.aadeshandreas.ailearning.ai_learning_companion.model.content.Summary;
import com.aadeshandreas.ailearning.ai_learning_companion.service.ContentGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/content")
public class ContentGenerationController {
    private static final Logger logger = LoggerFactory.getLogger(ContentGenerationController.class);
    private final ContentGenerationService contentGenerationService;

    public ContentGenerationController(ContentGenerationService contentGenerationService) {
        this.contentGenerationService = contentGenerationService;
    }

    /**
     * Generates a structured summary from the document previously uploaded in the user's session.
     *
     * @return A {@link ResponseEntity} wrapping a generic {@link ApiResponse}. On success,
     * the ApiResponse's data field will contain a {@link Summary} object. On failure,
     * it will contain an error message and code with null data.
     */
    @GetMapping(value = "/summary")
    public ResponseEntity<ApiResponse<?>> uploadAndSummarize() {
        try {
            Summary summary = contentGenerationService.generateSummary();
            ApiResponse<Summary> successResponse = new ApiResponse<>("Success", "200_OK", summary);
            return ResponseEntity.ok(successResponse);
        } catch (IllegalStateException e) {
            logger.warn("Summary generation failed because no document was uploaded: {}", e.getMessage());

            ApiResponse<Void> errorResponse = new ApiResponse<>(
                    "No document found. Please upload a document first.",
                    "NO_DOCUMENT_UPLOADED",
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception e) {
            logger.error("An unexpected error occurred during summary generation", e);

            ApiResponse<Void> errorResponse = new ApiResponse<>(
                    "Unable to process document",
                    "INTERNAL_SERVER_ERROR",
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Generates a list of flashcards from the document previously uploaded in the user's session.
     *
     * @return A {@link ResponseEntity} wrapping a generic {@link ApiResponse}. On success,
     * the ApiResponse's data field will contain a {@link FlashcardList} object. On failure,
     * it will contain an error message and code with null data.
     */
    @GetMapping(value = "/flashcards")
    public ResponseEntity<ApiResponse<?>> uploadAndGenerateFlashcard() {
        try {
            FlashcardList flashcardList = contentGenerationService.generateFlashcards();
            ApiResponse<FlashcardList> successResponse = new ApiResponse<>("Success", "200_OK", flashcardList);
            return ResponseEntity.ok(successResponse);
        } catch (IllegalStateException e) {
            logger.warn("Flashcard generation failed because no document was uploaded: {}", e.getMessage());

            ApiResponse<Void> errorResponse = new ApiResponse<>(
                    "No document found. Please upload a document first.",
                    "NO_DOCUMENT_UPLOADED",
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            logger.error("Error processing uploaded PDF", e);

            ApiResponse<Void> errorResponse = new ApiResponse<>(
                    "Unable to process document",
                    "INTERNAL_SERVER_ERROR",
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Generates a quiz from the document previously uploaded in the user's session.
     *
     * @return A {@link ResponseEntity} wrapping a generic {@link ApiResponse}. On success,
     * the ApiResponse's data field will contain a {@link Quiz} object. On failure,
     * it will contain an error message and code with null data.
     */
    @GetMapping(value = "/quiz")
    public ResponseEntity<ApiResponse<?>> uploadAndGenerateQuiz() {
        try {
            Quiz q = contentGenerationService.generateQuiz();
            ApiResponse<Quiz> successResponse = new ApiResponse<>("Success", "200_OK", q);
            return ResponseEntity.ok(successResponse);
        } catch (IllegalStateException e) {
            logger.warn("Quiz generation failed because no document was uploaded: {}", e.getMessage());

            ApiResponse<Void> errorResponse = new ApiResponse<>(
                    "No document found. Please upload a document first.",
                    "NO_DOCUMENT_UPLOADED",
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            logger.error("Error processing uploaded PDF", e);

            ApiResponse<Void> errorResponse = new ApiResponse<>(
                    "Unable to process document",
                    "INTERNAL_SERVER_ERROR",
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
