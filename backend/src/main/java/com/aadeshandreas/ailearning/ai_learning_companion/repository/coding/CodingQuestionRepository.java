package com.aadeshandreas.ailearning.ai_learning_companion.repository.coding;

import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.CodingQuestion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashMap;
import java.util.Map;

/**
 * Session-scoped repository for caching generated coding questions.
 * Stores questions by their UUID for submission validation.
 */
@Component
@SessionScope
@Setter
@Getter
@NoArgsConstructor
public class CodingQuestionRepository {
    private Map<String, CodingQuestion> questions = new HashMap<>();

    /**
     * Stores a coding question by its ID.
     */
    public void save(CodingQuestion question) {
        questions.put(question.getId(), question);
    }

    /**
     * Retrieves a coding question by its ID.
     */
    public CodingQuestion findById(String id) {
        return questions.get(id);
    }

    /**
     * Clears all cached questions (called when new document is uploaded).
     */
    public void clear() {
        questions.clear();
    }
}