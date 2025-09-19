package com.aadeshandreas.ailearning.ai_learning_companion.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.model.chat.ChatModel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DocumentService {
    private final ChatModel chatModel;

    public DocumentService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String summarizePDF(MultipartFile pdfFile) throws Exception {
        // Load the document from the uploaded file
        Path tempFile = File.createTempFile("temp-", ".pdf").toPath();
        pdfFile.transferTo(tempFile);

        Document document = FileSystemDocumentLoader.loadDocument(tempFile, new ApachePdfBoxDocumentParser());

        // Create the AI Prompt
        String sb = "Please create a clear and structured summary of the following text. The summary should:" +
                System.lineSeparator() +
                "Use simple language that is easy for students to understand." +
                "Break the information into short sections or bullet points." +
                "Highlight the most important concepts, definitions, and examples." +
                "Include key takeaways or lessons at the end." +
                document.text();

        // Generate the summary
        String summaryText = chatModel.chat(sb);

        Files.delete(tempFile);

        return summaryText;
    }
}
