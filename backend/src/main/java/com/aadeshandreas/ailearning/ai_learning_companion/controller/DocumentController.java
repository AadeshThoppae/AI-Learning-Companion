package com.aadeshandreas.ailearning.ai_learning_companion.controller;

import com.aadeshandreas.ailearning.ai_learning_companion.model.*;
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
            Summary summary = documentService.generateSummary();
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
            FlashcardList flashcardList = documentService.generateFlashcards();
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
            Quiz q = documentService.generateQuiz();
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


    /**
     * Generates an Interview from the document previously uploaded in the user's session.
     *
     * @return A {@link ResponseEntity} wrapping a generic {@link ApiResponse}. On success,
     * the ApiResponse's data field will contain a {@link Interview} object. On failure,
     * it will contain an error message and code with null data.
     */
    @GetMapping(value = "/interview")
    public ResponseEntity<ApiResponse<?>> uploadAndGenerateInterview() {
        try {
            Interview q = documentService.generateInterview();
            ApiResponse<Interview> successResponse = new ApiResponse<>("Success", "200_OK", q);
            return ResponseEntity.ok(successResponse);
        } catch (IllegalStateException e) {
            logger.warn("Interview generation failed because no document was uploaded: {}", e.getMessage());

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
     * Grades a user's interview answer using AI
     *
     * @return A {@link ResponseEntity} wrapping a generic {@link ApiResponse}. On success,
     * the ApiResponse's data field will contain a {@link InterviewResponse} object. On failure,
     * it will contain an error message and code with null data.
     */
    @GetMapping(value = "/interview/grade")
    public ResponseEntity<ApiResponse<?>> gradeInterviewAnswer(InterviewGradingRequest request) {
        try {
            InterviewResponse q = documentService.gradeInterviewAnswer(request.getQuestionId(),request.getUserAnswer());
            ApiResponse<InterviewResponse> successResponse = new ApiResponse<>("Success", "200_OK", q);
            return ResponseEntity.ok(successResponse);
        } catch (IllegalStateException e) {
            logger.warn("Interview grading failed: {}", e.getMessage());

            ApiResponse<Void> errorResponse = new ApiResponse<>(
                    e.getMessage(),
                    "INTERVIEW_NOT_FOUND",
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            logger.error("Error grading interview answer", e);

            ApiResponse<Void> errorResponse = new ApiResponse<>(
                    "Unable to grade answer",
                    "INTERNAL_SERVER_ERROR",
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
