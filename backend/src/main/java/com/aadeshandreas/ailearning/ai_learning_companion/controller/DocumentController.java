package com.aadeshandreas.ailearning.ai_learning_companion.controller;

import com.aadeshandreas.ailearning.ai_learning_companion.model.common.ApiResponse;
import com.aadeshandreas.ailearning.ai_learning_companion.model.common.DocumentText;
import com.aadeshandreas.ailearning.ai_learning_companion.service.DocumentService;
import jakarta.validation.Valid;
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

    /**
     * Handles the HTTP POST request to upload a PDF document. The document's text is
     * extracted and stored in the user's session for subsequent processing, such as
     * generating summaries or flashcards.
     *
     * @param file The PDF file uploaded by the client as part of a multipart/form-data request.
     * @return A {@link ResponseEntity} wrapping a generic {@link ApiResponse}. On successful upload,
     * it returns a 200 OK status with a success message. If an {@link IOException}
     * occurs during file processing, it returns a 500 Internal Server Error with an error message.
     */
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
     * Handles the HTTP POST request to upload raw text content for processing.
     * The uploaded text is wrapped in a {@link DocumentText} model and stored
     * in the user's session for further operations, such as generating summaries
     * or flashcards.
     *
     * <p>Validation is performed by the {@link DocumentText} model itself;
     * empty or null text will trigger an {@link IllegalArgumentException}.</p>
     *
     * @param documentText The {@link DocumentText} object containing the text input.
     * @return A {@link ResponseEntity} wrapping a generic {@link ApiResponse}. On success,
     *         it returns a 200 OK status with a success message. If the text is invalid,
     *         it returns a 400 Bad Request. Unexpected errors result in a 500 Internal Server Error.
     */
    @PostMapping(value = "/upload-text")
    public ResponseEntity<ApiResponse<?>> uploadText(@Valid @RequestBody DocumentText documentText){
        try {
            documentService.uploadText(documentText.text());
            return ResponseEntity.ok(new ApiResponse<>("Success", "200_OK", null));
        } catch (Exception e){
            logger.error("Error processing text upload", e);

            ApiResponse<Void> errorResponse = new ApiResponse<>(
                    "Unable to process text",
                    "TEXT_UPLOAD_ERROR",
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
