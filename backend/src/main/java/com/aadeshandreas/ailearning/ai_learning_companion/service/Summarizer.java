package com.aadeshandreas.ailearning.ai_learning_companion.service;

import com.aadeshandreas.ailearning.ai_learning_companion.model.Summary;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface Summarizer {
    @UserMessage("""
            Please create a clear and structured summary of the following text. The summary should:
            - Use simple language that is easy for students to understand.
            - Break the information into short sections or bullet points.
            - Highlight the most important concepts, definitions, and examples.
            - Include key takeaways or lessons at the end.
            
            Text: {{text}}
            """)
    Summary summarize(@V("text") String text);

}
