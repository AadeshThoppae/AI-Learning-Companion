package com.aadeshandreas.ailearning.ai_learning_companion.service;

import com.aadeshandreas.ailearning.ai_learning_companion.model.Summary;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DocumentService {
    private final Summarizer summarizer;
    private final ObjectMapper objectMapper;

    public DocumentService(ChatModel chatModel, ObjectMapper objectMapper) {
        this.summarizer = AiServices.create(Summarizer.class, chatModel);
        this.objectMapper = objectMapper;
    }

    public String summarizePDF(MultipartFile pdfFile) throws Exception {
        // Load the document from the uploaded file
        Path tempFile = File.createTempFile("temp-", ".pdf").toPath();
        try {
            pdfFile.transferTo(tempFile);

            Document document = FileSystemDocumentLoader.loadDocument(tempFile, new ApachePdfBoxDocumentParser());

            // Get the structured summary object from the AI
            Summary summaryObject = summarizer.summarize(document.text());

            // Convert to JSON string
            String jsonResponse = objectMapper.writeValueAsString(summaryObject);

            return jsonResponse;
        } finally {
            Files.deleteIfExists(tempFile);
        }
}
