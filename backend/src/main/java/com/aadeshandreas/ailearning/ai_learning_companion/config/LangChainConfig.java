package com.aadeshandreas.ailearning.ai_learning_companion.config;

import com.aadeshandreas.ailearning.ai_learning_companion.service.*;
import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.CodingQuestionGenerator;
import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.CodingTopicExtractor;
import com.aadeshandreas.ailearning.ai_learning_companion.service.generation.FlashcardGenerator;
import com.aadeshandreas.ailearning.ai_learning_companion.service.generation.QuizGenerator;
import com.aadeshandreas.ailearning.ai_learning_companion.service.generation.Summarizer;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Spring {@code @Configuration} class responsible for setting up and configuring
 * the primary LangChain4j {@link ChatModel} bean for the application.
 */
@Configuration
public class LangChainConfig {

    private final String apiKey;
    private final String proModelName;
    private final String flashModelName;

    /**
     * Constructs the configuration and injects required properties from the
     * application's external configuration file (e.g., application.properties).
     *
     * @param apiKey         The API key for the Google Gemini service.
     * @param proModelName   The Gemini Pro model for complex tasks (e.g., "gemini-2.5-pro").
     * @param flashModelName The Gemini Flash model for faster tasks (e.g., "gemini-2.5-flash").
     */
    public LangChainConfig(
            @Value("${langchain4j.google-ai-gemini.chat-model.api-key}") String apiKey,
            @Value("${langchain4j.google-ai-gemini.chat-model.pro-model-name}") String proModelName,
            @Value("${langchain4j.google-ai-gemini.chat-model.flash-model-name}") String flashModelName
    ) {
        this.apiKey = apiKey;
        this.proModelName = proModelName;
        this.flashModelName = flashModelName;
    }

    /**
     * Defines the Gemini Pro {@link ChatModel} bean for complex tasks like code generation.
     * <p>
     * Uses gemini-2.5-pro model for better instruction following and complex reasoning.
     *
     * @return A fully configured instance of {@code ChatModel} using Gemini Pro.
     */
    @Bean
    public ChatModel geminiPro() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(proModelName)
                .timeout(Duration.ofSeconds(180)) // 3 minutes for complex code generation tasks
                .build();
    }

    /**
     * Defines the Gemini Flash {@link ChatModel} bean for faster, simpler tasks.
     * <p>
     * Uses gemini-2.5-flash model for quick summaries, flashcards, and quizzes.
     *
     * @return A fully configured instance of {@code ChatModel} using Gemini Flash.
     */
    @Bean
    public ChatModel geminiFlash() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(flashModelName)
                .timeout(Duration.ofSeconds(60)) // 1 minute for faster tasks
                .build();
    }

    /**
     * Creates the {@link Summarizer} AI Service bean using Gemini Flash.
     * LangChain4j will create a dynamic implementation of the Summarizer interface.
     *
     * @return A ready-to-use instance of the Summarizer service.
     */
    @Bean
    public Summarizer summarizer() {
        return AiServices.create(Summarizer.class, geminiFlash());
    }

    /**
     * Creates the {@link FlashcardGenerator} AI Service bean using Gemini Flash.
     * LangChain4j will create a dynamic implementation of the FlashcardGenerator interface.
     *
     * @return A ready-to-use instance of the FlashcardGenerator service.
     */
    @Bean
    public FlashcardGenerator flashcardGenerator() {
        return AiServices.create(FlashcardGenerator.class, geminiFlash());
    }

    /**
     * Creates the {@link QuizGenerator} AI Service bean using Gemini Flash.
     * LangChain4j will create a dynamic implementation of the QuizGenerator interface.
     *
     * @return A ready-to-use instance of the QuizGenerator service.
     */
    @Bean
    public QuizGenerator quizGenerator() {
        return AiServices.create(QuizGenerator.class, geminiFlash());
    }

    /**
     * Creates the {@link CodingTopicExtractor} AI Service bean using Gemini Flash.
     * LangChain4j will create a dynamic implementation of the CodingTopicExtractor interface.
     *
     * @return A ready-to-use instance of the CodingTopicExtractor service.
     */
    @Bean
    public CodingTopicExtractor codingTopicExtractor() {
        return AiServices.create(CodingTopicExtractor.class, geminiFlash());
    }

    /**
     * Creates the {@link CodingQuestionGenerator} AI Service bean using Gemini Pro.
     * LangChain4j will create a dynamic implementation of the CodingQuestionGenerator interface.
     * Uses the more powerful Pro model for better instruction following in code generation.
     *
     * @return A ready-to-use instance of the CodingQuestionGenerator service.
     */
    @Bean
    public CodingQuestionGenerator codingQuestionGenerator() {
        return AiServices.create(CodingQuestionGenerator.class, geminiPro());
    }

    /**
     * Creates the {@link com.aadeshandreas.ailearning.ai_learning_companion.service.InterviewGenerator} AI Service bean.
     * LangChain4j will create a dynamic implementation of the InterviewGenerator interface.
     *
     * @return A ready-to-use instance of the InterviewGenerator service.
     */
    @Bean
    public InterviewGenerator interviewGenerator() {
        return AiServices.create(InterviewGenerator.class, geminiFlash());
    }

    /**
     * Creates the {@link com.aadeshandreas.ailearning.ai_learning_companion.service.InterviewAnswerGrader} AI Service bean.
     * LangChain4j will create a dynamic implementation of the InterviewAnswerGrader interface.
     *
     * @return A ready-to-use instance of the InterviewAnswerGrader service.
     */
    @Bean
    public InterviewAnswerGrader interviewAnswerGrader() {
        return AiServices.create(InterviewAnswerGrader.class, geminiFlash());
    }

}
