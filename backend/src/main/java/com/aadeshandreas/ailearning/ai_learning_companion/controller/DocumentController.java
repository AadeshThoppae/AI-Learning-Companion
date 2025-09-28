package com.aadeshandreas.ailearning.ai_learning_companion.controller;

import com.aadeshandreas.ailearning.ai_learning_companion.model.ApiResponse;
import com.aadeshandreas.ailearning.ai_learning_companion.model.ErrorResponse;
import com.aadeshandreas.ailearning.ai_learning_companion.model.Flashcard;
import com.aadeshandreas.ailearning.ai_learning_companion.model.Summary;
import com.aadeshandreas.ailearning.ai_learning_companion.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * Handles the HTTP POST request to upload a PDF file and generate a structured summary.
     *
     * @param file The PDF file uploaded by the client in a multipart/form-data request.
     * @return A {@link ResponseEntity} containing a {@link Summary} object on success (HTTP 200 OK),
     * or an {@link ErrorResponse} on failure (HTTP 500 Internal Server Error).
     */
    @PostMapping(value = "/summary", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> uploadAndSummarize(@RequestParam("file")MultipartFile file) {
        try {
            Summary summary = (Summary) documentService.generateContent(file, "summarizer");
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            // Log the detailed exception for debugging purposes on the server-side
            logger.error("Error processing uploaded PDF", e);

            // Return a generic, safe error response to the client
            ErrorResponse errorResponse = new ErrorResponse(
                    "Unable to process document",
                    "DOCUMENT_PROCESSING_ERROR"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping(value = "/flashcards", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAndGenerateFlashcard(@RequestParam("file")MultipartFile file) {
        try {
            Flashcard flashcards = (Flashcard) documentService.generateContent(file, "flashcardGenerator");
            return ResponseEntity.ok(flashcards);
        } catch (Exception e) {
            // Log the detailed exception for debugging purposes on the server-side
            logger.error("Error processing uploaded PDF", e);

            // Return a generic, safe error response to the client
            ErrorResponse errorResponse = new ErrorResponse(
                    "Unable to process document",
                    "DOCUMENT_PROCESSING_ERROR"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
