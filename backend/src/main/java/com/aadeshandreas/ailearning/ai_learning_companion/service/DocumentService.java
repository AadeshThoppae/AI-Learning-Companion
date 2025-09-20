package com.aadeshandreas.ailearning.ai_learning_companion.service;

import com.aadeshandreas.ailearning.ai_learning_companion.model.Summary;
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

    public DocumentService(ChatModel chatModel) {
        this.summarizer = AiServices.create(Summarizer.class, chatModel);
    }

    public Summary summarizePDF(MultipartFile pdfFile) throws Exception {
        // Load the document from the uploaded file
        Path tempFile = File.createTempFile("temp-", ".pdf").toPath();
        try {
            pdfFile.transferTo(tempFile);

            Document document = FileSystemDocumentLoader.loadDocument(tempFile, new ApachePdfBoxDocumentParser());

            // Get the structured summary object from the AI and return it
            return summarizer.summarize(document.text());
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}
