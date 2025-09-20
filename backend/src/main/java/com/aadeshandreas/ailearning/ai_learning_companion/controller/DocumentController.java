package com.aadeshandreas.ailearning.ai_learning_companion.controller;

import com.aadeshandreas.ailearning.ai_learning_companion.model.ApiResponse;
import com.aadeshandreas.ailearning.ai_learning_companion.model.ErrorResponse;
import com.aadeshandreas.ailearning.ai_learning_companion.model.Summary;
import com.aadeshandreas.ailearning.ai_learning_companion.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(value = "/summary", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> uploadAndSummarize(@RequestParam("file")MultipartFile file) {
        try {
            Summary summary = documentService.summarizePDF(file);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            // Log details server-side
            logger.error("Error processing uploaded PDF", e);

            // Return a safe, structured error
            ErrorResponse errorResponse = new ErrorResponse(
                    "Unable to process document",
                    "DOCUMENT_PROCESSING_ERROR"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
