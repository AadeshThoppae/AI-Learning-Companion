package com.aadeshandreas.ailearning.ai_learning_companion.service.generation;

import com.aadeshandreas.ailearning.ai_learning_companion.model.content.Summary;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * An AI Service interface for creating structured summaries of text using LangChain4j.
 * This interface is designed to be implemented automatically by the AiServices factory.
 */
public interface Summarizer extends ContentGenerator<Summary> {
    /**
     * Generates a structured summary from the provided text.
     * The behavior of the summary is defined by the prompt in the @UserMessage annotation.
     *
     * @param text The input text to be summarized.
     * @return A {@link Summary} object containing the structured summary.
     */
    @UserMessage("""
            Please create a clear and structured summary of the following text. The summary should:
            - Use simple language that is easy for students to understand.
            - Break the information into short sections or bullet points.
            - Highlight the most important concepts, definitions, and examples.
            - Include key takeaways or lessons at the end.
            
            Text: {{text}}
            """)
    @Override
    Summary generate(@V("text") String text);

}
