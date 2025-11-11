package com.aadeshandreas.ailearning.ai_learning_companion.model.coding;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * A DTO (Data Transfer Object) that wraps a list of {@link CodingTopic} objects.
 * <p>
 * This pattern ensures the AI model returns a well-structured JSON object
 * (e.g., {"topics": [...]}) rather than a raw JSON array.
 */
@Getter
@Setter
public class CodingTopicList {
    private List<CodingTopic> topics;

    /**
     * Default no-argument constructor.
     * Required by frameworks like Jackson (for JSON deserialization).
     */
    public CodingTopicList() {}

    /**
     * Convenience constructor to create an instance with a pre-populated list of topics.
     *
     * @param topics The list of coding topics to wrap.
     */
    public CodingTopicList(List<CodingTopic> topics) {
        this.topics = topics;
    }
}