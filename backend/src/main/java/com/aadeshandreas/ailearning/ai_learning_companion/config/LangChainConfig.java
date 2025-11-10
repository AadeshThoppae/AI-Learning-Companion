package com.aadeshandreas.ailearning.ai_learning_companion.config;

import com.aadeshandreas.ailearning.ai_learning_companion.service.*;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring {@code @Configuration} class responsible for setting up and configuring
 * the primary LangChain4j {@link ChatModel} bean for the application.
 */
@Configuration
public class LangChainConfig {

    private final String apiKey;
    private final String modelName;

    /**
     * Constructs the configuration and injects required properties from the
     * application's external configuration file (e.g., application.properties).
     *
     * @param apiKey    The API key for the Google Gemini service.
     * @param modelName The specific Gemini model to be used (e.g., "gemini-pro").
     */
    public LangChainConfig(
            @Value("${langchain4j.google-ai-gemini.chat-model.api-key}") String apiKey,
            @Value("${langchain4j.google-ai-gemini.chat-model.model-name}") String modelName
    ) {
        this.apiKey = apiKey;
        this.modelName = modelName;
    }

    /**
     * Defines the primary {@link ChatModel} bean for the application, configured to
     * use Google's Gemini model.
     * <p>
     * This bean can then be injected into any service that needs to interact with the AI.
     *
     * @return A fully configured instance of {@code ChatModel}.
     */
    @Bean
    public ChatModel gemini() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .build();
    }

    /**
     * Creates the {@link Summarizer} AI Service bean.
     * LangChain4j will create a dynamic implementation of the Summarizer interface.
     *
     * @param chatModel The configured {@link ChatModel} bean to use for the AI calls.
     * @return A ready-to-use instance of the Summarizer service.
     */
    @Bean
    public Summarizer summarizer(ChatModel chatModel) {
        return AiServices.create(Summarizer.class, chatModel);
    }

    /**
     * Creates the {@link FlashcardGenerator} AI Service bean.
     * LangChain4j will create a dynamic implementation of the FlashcardGenerator interface.
     *
     * @param chatModel The configured {@link ChatModel} bean to use for the AI calls.
     * @return A ready-to-use instance of the FlashcardGenerator service.
     */
    @Bean
    public FlashcardGenerator flashcardGenerator(ChatModel chatModel) {
        return AiServices.create(FlashcardGenerator.class, chatModel);
    }
    /**
     * Creates the {@link com.aadeshandreas.ailearning.ai_learning_companion.service.QuizGenerator} AI Service bean.
     * LangChain4j will create a dynamic implementation of the QuizGenerator interface.
     *
     * @param chatModel The configured {@link ChatModel} bean to use for the AI calls.
     * @return A ready-to-use instance of the QuizGenerator service.
     */
    @Bean
    public QuizGenerator quizGenerator(ChatModel chatModel) {
        return AiServices.create(QuizGenerator.class, chatModel);
    }

    /**
     * Creates the {@link com.aadeshandreas.ailearning.ai_learning_companion.service.InterviewGenerator} AI Service bean.
     * LangChain4j will create a dynamic implementation of the InterviewGenerator interface.
     *
     * @param chatModel The configured {@link ChatModel} bean to use for the AI calls.
     * @return A ready-to-use instance of the QuizGenerator service.
     */
    @Bean
    public InterviewGenerator interviewGenerator(ChatModel chatModel) {
        return AiServices.create(InterviewGenerator.class, chatModel);
    }

    /**
     * Creates the {@link com.aadeshandreas.ailearning.ai_learning_companion.service.InterviewAnswerGrader} AI Service bean.
     * LangChain4j will create a dynamic implementation of the InterviewAnswerGrader interface.
     *
     * @param chatModel The configured {@link ChatModel} bean to use for the AI calls.
     * @return A ready-to-use instance of the QuizGenerator service.
     */
    @Bean
    public InterviewAnswerGrader interviewAnswerGrader(ChatModel chatModel) {
        return AiServices.create(InterviewAnswerGrader.class, chatModel);
    }

}
