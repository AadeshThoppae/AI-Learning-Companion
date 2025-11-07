package com.aadeshandreas.ailearning.ai_learning_companion.repository.coding;

import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.CodingTopicList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

/**
 * Session-scoped repository for caching coding topics.
 * Each user session maintains its own set of extracted topics.
 */
@Component
@SessionScope
@Setter
@Getter
@NoArgsConstructor
public class CodingTopicRepository {
    private CodingTopicList codingTopics;
}