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
 * Maintains a mapping between topic IDs and question IDs for caching.
 */
@Component
@SessionScope
@Setter
@Getter
@NoArgsConstructor
public class CodingQuestionRepository {
    private Map<String, CodingQuestion> questions = new HashMap<>();
    private Map<Integer, String> topicToQuestionMap = new HashMap<>();

    /**
     * Stores a coding question by its ID and maintains the topic-to-question mapping.
     */
    public void save(CodingQuestion question) {
        questions.put(question.getId(), question);
        topicToQuestionMap.put(question.getTopicId(), question.getId());
    }

    /**
     * Retrieves a coding question by its ID.
     */
    public CodingQuestion findById(String id) {
        return questions.get(id);
    }

    /**
     * Retrieves a coding question by its topic ID.
     * Returns null if no question exists for the given topic.
     */
    public CodingQuestion findByTopicId(int topicId) {
        String questionId = topicToQuestionMap.get(topicId);
        return questionId != null ? questions.get(questionId) : null;
    }

    /**
     * Clears all cached questions and topic mappings (called when new document is uploaded).
     */
    public void clear() {
        questions.clear();
        topicToQuestionMap.clear();
    }
}