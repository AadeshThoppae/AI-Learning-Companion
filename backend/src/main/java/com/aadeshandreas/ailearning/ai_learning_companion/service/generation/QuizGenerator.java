package com.aadeshandreas.ailearning.ai_learning_companion.service.generation;

import com.aadeshandreas.ailearning.ai_learning_companion.model.content.Quiz;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface QuizGenerator extends ContentGenerator<Quiz> {
    /**
     * This method will be implemented by LangChain4j. When called, it will send the
     * user message (prompt) along with the provided text to the AI model and parse
     * the response into a {@link Quiz} object.
     *
     * @param text The source text from which to generate quiz questions
     * @return A {@link Quiz} DTO containing the generated quiz questions.
     */
    @UserMessage("""
            Given the following text, generate 10 multiple choice questions and answers based on the main concepts, terms and ideas.
            For each question, create 4 answer choices with explanations as to why each option is the incorrect/correct choice.
            The language should be at the same level as the given text.
            Text: {{text}}
            """)
    @Override
    Quiz generate(@V("text") String text);
}
