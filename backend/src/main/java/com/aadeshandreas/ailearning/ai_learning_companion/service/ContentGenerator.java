package com.aadeshandreas.ailearning.ai_learning_companion.service;

/**
 * A generic interface for AI content generators.
 * It takes a string of text as input and produces a specific type of content as output.
 *
 * @param <T> The type of the content to be generated
 */
public interface ContentGenerator<T> {

    /**
     * Generates content based on the provided text.
     * @param text The source text from the user.
     * @return The generated content of type T.
     */
    T generate(String text);
}