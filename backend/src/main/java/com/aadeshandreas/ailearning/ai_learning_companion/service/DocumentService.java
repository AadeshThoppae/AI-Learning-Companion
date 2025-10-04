package com.aadeshandreas.ailearning.ai_learning_companion.service;

import com.aadeshandreas.ailearning.ai_learning_companion.model.FlashcardList;
import com.aadeshandreas.ailearning.ai_learning_companion.model.Summary;
import com.aadeshandreas.ailearning.ai_learning_companion.repository.DocumentRepository;
import com.aadeshandreas.ailearning.ai_learning_companion.repository.FlashcardRepository;
import com.aadeshandreas.ailearning.ai_learning_companion.repository.SummaryRepository;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Service class that orchestrates processing of uploaded documents. It extracts text from
 * a file and uses a dynamic map of AI generators to produce the desired content.
 */
@Service
public class DocumentService {
    /**
     * A map of all available ContentGenerator beans, injected by Spring.
     * The key is the bean's name (e.g., "summarizer"), and the value is the service instance.
     * This allows for a flexible Strategy Pattern implementation.
     */
    private final DocumentRepository documentRepository;
    private final SummaryRepository summaryRepository;
    private final FlashcardRepository flashcardRepository;
    private final Summarizer summarizer;
    private final FlashcardGenerator flashcardGenerator;

    /**
     * Constructs the DocumentService with all its required dependencies, which are
     * automatically provided by Spring's dependency injection.
     *
     * @param documentRepository    The session-scoped repository for storing the uploaded document's text.
     * @param summaryRepository     The session-scoped cache for storing the generated summary.
     * @param flashcardRepository   The session-scoped cache for storing the generated flashcards.
     * @param summarizer            The AI service bean responsible for generating summaries.
     * @param flashcardGenerator    The AI service bean responsible for generating flashcards.
     */
    @Autowired
    public DocumentService(
            DocumentRepository documentRepository,
            SummaryRepository summaryRepository,
            FlashcardRepository flashcardRepository,
            Summarizer summarizer,
            FlashcardGenerator flashcardGenerator
    ) {
        this.documentRepository = documentRepository;
        this.summaryRepository = summaryRepository;
        this.flashcardRepository = flashcardRepository;
        this.summarizer = summarizer;
        this.flashcardGenerator = flashcardGenerator;
    }

    /**
     * Parses an uploaded PDF file, extracts its text, and stores it in the repository
     * for the current user session.
     *
     * @param pdfFile       The PDF file uploaded by the client.
     * @throws IOException  if there is an error reading or parsing the file.
     */
    public void uploadDocument(MultipartFile pdfFile) throws IOException {
        // Create a temporary file to store the uploaded content for parsing.
        Path tempFile = File.createTempFile("temp-", ".pdf").toPath();
        try {
            // Transfer the uploaded file's content to the temporary file.
            pdfFile.transferTo(tempFile);

            // Use LangChain4j's loaders to parse the PDF file into a standard Document object.
            Document document = FileSystemDocumentLoader.loadDocument(tempFile, new ApachePdfBoxDocumentParser());

            documentRepository.setDocumentText(document.text());

        } finally {
            // Ensure the temporary file is deleted even if an error occurs
            Files.deleteIfExists(tempFile);
        }
    }
    /**
     * Accepts raw text input and stores it in the current session's repository
     * clears previously stored summaries/flashcards
     * @param text raw text tha's provided by user
     */
    public void uploadText(String text){
        if(text == null || text.trim().isEmpty()){
            throw new IllegalArgumentException("Text cannot be empty");
        }

        documentRepository.setDocumentText(text.trim());
        clearCache();
    }
    /**
     * clears cahced summaries/flashcards so when new content is uploaded, we get a fresh AI generation
     */
    private void clearCache() {
        summaryRepository.setSummary(null);
        flashcardRepository.setFlashcardList(null);
    }

    /**
     * Generates a summary from the currently stored document, using a cache to avoid repeat AI calls.
     * @return The generated or cached Summary object.
     */
    public Summary generateSummary() {
        if (summaryRepository.getSummary() != null) {
            return summaryRepository.getSummary();
        }

        String documentText = documentRepository.getDocumentText();
        Summary summary = summarizer.generate(documentText);
        summaryRepository.setSummary(summary);
        return summary;
    }

    /**
     * Generates flashcards from the currently stored document, using a cache.
     * @return The generated or cached FlashcardList object.
     */
    public FlashcardList generateFlashcards() {
        if (flashcardRepository.getFlashcardList() != null) {
            return flashcardRepository.getFlashcardList();
        }

        String documentText = documentRepository.getDocumentText();
        FlashcardList flashcardList = flashcardGenerator.generate(documentText);
        flashcardRepository.setFlashcardList(flashcardList);
        return flashcardList;
    }
}
