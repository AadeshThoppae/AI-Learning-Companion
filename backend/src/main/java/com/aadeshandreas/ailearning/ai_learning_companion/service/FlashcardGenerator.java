package com.aadeshandreas.ailearning.ai_learning_companion.service;

import com.aadeshandreas.ailearning.ai_learning_companion.model.Flashcard;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface FlashcardGenerator extends ContentGenerator<Flashcard> {
    @UserMessage("""
            Analyze the following text and identify the most important concepts, terms, or key ideas.
            Create a flashcard with a concise question, a clear answer, and a helpful hint.
            The language used must be simple and easy for a student to understand.
            Text: {{text}}
            """)
    @Override
    Flashcard generate(@V("text") String text);
}
