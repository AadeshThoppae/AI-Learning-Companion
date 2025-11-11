package com.aadeshandreas.ailearning.ai_learning_companion.service.coding;

import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.CodingTopicList;
import com.aadeshandreas.ailearning.ai_learning_companion.service.generation.ContentGenerator;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * AI service interface for extracting programming topics from text.
 * Implemented automatically by LangChain4j at runtime.
 */
public interface CodingTopicExtractor extends ContentGenerator<CodingTopicList> {

    /**
     * Extracts 5 programming concepts/topics from the provided text.
     * This method will be implemented by LangChain4j, which sends the prompt
     * to the AI model and parses the response into a {@link CodingTopicList}.
     *
     * @param text The source programming-related text
     * @return A {@link CodingTopicList} containing 5 extracted topics
     */
    @UserMessage("""
            Given the following programming-related text, extract exactly 5 key programming concepts or topics
            that would be suitable for coding practice questions.

            For each topic:
            - Provide a clear, concise title (2-5 words)
            - Write a brief description (1-2 sentences explaining what the topic involves)
            - Assign a difficulty level: EASY, MEDIUM, or HARD based on the complexity of the concept
            - List 2-3 relevant keywords that relate to the topic

            Focus on concepts that can be tested with algorithmic problems such as:
            - Data structures (arrays, linked lists, trees, graphs, hash maps, etc.)
            - Algorithms (sorting, searching, recursion, dynamic programming, etc.)
            - Design patterns and object-oriented programming concepts
            - Problem-solving techniques

            Ensure variety in difficulty levels (mix of EASY, MEDIUM, and HARD topics).
            Make topics specific enough to generate meaningful coding questions.

            Text:
            {{text}}
            """)
    @Override
    CodingTopicList generate(@V("text") String text);
}