package com.aadeshandreas.ailearning.ai_learning_companion.repository;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

/**
 * An in-memory, session-scoped repository to hold the text of a single document.
 * A new instance of this class is created for each user session, ensuring
 * that document data is not shared between users.
 */
@Component
@SessionScope
@Setter
@NoArgsConstructor
public class DocumentRepository {
    private String documentText;

    public String getDocumentText() {
        if (documentText == null) {
            throw new IllegalStateException("No Document found. Please upload a document first");
        }
        return documentText;
    }
}
