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
 * Service class that manages the process of summarizing uploaded PDF documents using an AI model.
 */
@Service
public class DocumentService {
    private final Map<String, ContentGenerator<?>> generatorMap;

    @Autowired
    public DocumentService(Map<String, ContentGenerator<?>> generatorMap) {
        this.generatorMap = generatorMap;
    }


    public Object generateContent(MultipartFile pdfFile, String generatorType) throws Exception {
        ContentGenerator<?> selectedGenerator = generatorMap.get(generatorType);

        if (selectedGenerator == null) {
            throw new IllegalArgumentException("Unknown generator type: " + generatorType);
        }

        // Load the document from the uploaded file
        Path tempFile = File.createTempFile("temp-", ".pdf").toPath();
        try {
            pdfFile.transferTo(tempFile);

            Document document = FileSystemDocumentLoader.loadDocument(tempFile, new ApachePdfBoxDocumentParser());

            // Get the structured summary object from the AI and return it
            return selectedGenerator.generate(document.text());
        } finally {
            // Ensure the temporary file is deleted even if an error occurs
            Files.deleteIfExists(tempFile);
        }
    }
}
