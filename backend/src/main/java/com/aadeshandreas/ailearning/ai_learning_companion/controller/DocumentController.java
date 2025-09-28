package com.aadeshandreas.ailearning.ai_learning_companion.controller;

import com.aadeshandreas.ailearning.ai_learning_companion.model.*;
import com.aadeshandreas.ailearning.ai_learning_companion.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * REST controller that defines API endpoints for document processing,
 * such as uploading and summarizing PDF files.
 */
@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
    private final DocumentService documentService;

    /**
     * Constructs the controller and injects the required DocumentService dependency.
     *
     * @param documentService The service responsible for handling the core document processing logic.
     */
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            documentService.uploadDocument(file);
            return ResponseEntity.ok(new ApiResponse<>("Success", "200_OK", null));
        } catch (IOException e) {
            logger.error(e.getMessage());

            ApiResponse<Void> errorResponse = new ApiResponse<>("Unable to upload document", "DOCUMENT_UPLOAD_ERROR", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Generates a structured summary from the document previously uploaded in the user's session.
     *
     * @return A {@link ResponseEntity} wrapping a generic {@link ApiResponse}. On success,
     * the ApiResponse's data field will contain a {@link Summary} object. On failure,
     * it will contain an error message and code with null data.
     */
    @PostMapping(value = "/summary")
    public ResponseEntity<ApiResponse<?>> uploadAndSummarize() {
        try {
            Summary summary = (Summary) documentService.generateContent("summarizer");
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
    @PostMapping(value = "/flashcards")
    public ResponseEntity<ApiResponse<?>> uploadAndGenerateFlashcard() {
        try {
            FlashcardList flashcardList = (FlashcardList) documentService.generateContent("flashcardGenerator");
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
}
