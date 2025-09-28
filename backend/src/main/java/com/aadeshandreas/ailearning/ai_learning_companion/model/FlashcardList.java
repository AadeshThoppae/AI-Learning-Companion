package com.aadeshandreas.ailearning.ai_learning_companion.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * A DTO (Data Transfer Object) that acts as a wrapper for a list of {@link Flashcard} objects.
 * <p>
 * This pattern is used to ensure the AI model returns a well-structured JSON object
 * (e.g., {"flashcards": [...]}) rather than a raw JSON array, which is more robust and extensible.
 */
@Setter
@Getter
public class FlashcardList {
    private List<Flashcard> flashcards;

    /**
     * Default no-argument constructor.
     * Required by frameworks like Jackson (for JSON deserialization) and JPA.
     */
    public FlashcardList() {}

    /**
     * Convenience constructor to create an instance with a pre-populated list of flashcards.
     *
     * @param list The list of flashcards to wrap.
     */
    public FlashcardList(List<Flashcard> list) {
        this.flashcards = list;
    }

}
