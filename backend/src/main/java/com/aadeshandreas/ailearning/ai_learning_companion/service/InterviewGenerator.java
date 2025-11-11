package com.aadeshandreas.ailearning.ai_learning_companion.service;

import com.aadeshandreas.ailearning.ai_learning_companion.model.Interview;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface InterviewGenerator extends ContentGenerator<Interview> {
    /**
     * This method will be implemented by LangChain4j. When called, it will send the
     * user message (prompt) along with the provided text to the AI model and parse
     * the response into a {@link Interview} object.
     *
     * @param text The source text from which to generate interview questions
     * @return A {@link Interview} DTO containing the generated interview questions.
     */
    @UserMessage("""
            Given the following text, generate 3 interview style questions and answers based on the main concepts, terms and ideas.
            For each question, generate a perfect interview style answer that demonstrates complete understanding of the topic.
            The language should be at the same level as the given text.
            Text: {{text}}
            """)
    @Override
    Interview generate(@V("text") String text);


}
