package com.aadeshandreas.ailearning.ai_learning_companion.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

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
    private final Map<String, ContentGenerator<?>> generatorMap;

    /**
     * Constructs the service with a map of all beans that implement ContentGenerator.
     *
     * @param generatorMap A map of AI content generators provided by Spring's dependency injection.
     */
    @Autowired
    public DocumentService(Map<String, ContentGenerator<?>> generatorMap) {
        this.generatorMap = generatorMap;
    }

    /**
     * Generic method to generate content from a PDF file using a specified AI generator.
     *
     * @param pdfFile       The uploaded PDF file from the controller.
     * @param generatorType The string key identifying which generator to use (e.g., "summarizer").
     * @return An {@code Object} containing the generated content (e.g., a {@code Summary} or {@code FlashcardList}).
     * @throws Exception if there is an error during file processing or AI interaction.
     */
    public Object generateContent(MultipartFile pdfFile, String generatorType) throws Exception {
        // Select the appropriate generator strategy from the map based on the key.
        ContentGenerator<?> selectedGenerator = generatorMap.get(generatorType);

        if (selectedGenerator == null) {
            throw new IllegalArgumentException("Unknown generator type: " + generatorType);
        }

        /// Create a temporary file to store the uploaded content for parsing.
        Path tempFile = File.createTempFile("temp-", ".pdf").toPath();
        try {
            // Transfer the uploaded file's content to the temporary file.
            pdfFile.transferTo(tempFile);

            // Use LangChain4j's loaders to parse the PDF file into a standard Document object.
            Document document = FileSystemDocumentLoader.loadDocument(tempFile, new ApachePdfBoxDocumentParser());

            // Call the selected generator with the extracted text and return the result.
            return selectedGenerator.generate(document.text());
        } finally {
            // Ensure the temporary file is deleted even if an error occurs
            Files.deleteIfExists(tempFile);
        }
    }
}
