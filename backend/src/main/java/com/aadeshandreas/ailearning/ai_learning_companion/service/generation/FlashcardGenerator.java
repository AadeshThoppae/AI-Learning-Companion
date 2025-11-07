package com.aadeshandreas.ailearning.ai_learning_companion.service.generation;

import com.aadeshandreas.ailearning.ai_learning_companion.model.content.FlashcardList;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * A LangChain4j AI Service interface for generating flashcards from a given text.
 * This interface extends the generic {@link ContentGenerator} to maintain a consistent pattern.
 */
public interface FlashcardGenerator extends ContentGenerator<FlashcardList> {
    /**
     * This method will be implemented by LangChain4j. When called, it will send the
     * user message (prompt) along with the provided text to the AI model and parse
     * the response into a {@link FlashcardList} object.
     *
     * @param text The source text from which to generate flashcards.
     * @return A {@link FlashcardList} DTO containing the generated flashcards.
     */
    @UserMessage("""
            Analyze the following text and identify the 10 most important concepts, terms, or key ideas.
            For each, create a flashcard with a concise question, a clear answer, and a helpful hint.
            The language used must be simple and easy for a student to understand.
            Text: {{text}}
            """)
    @Override
    FlashcardList generate(@V("text") String text);
}
