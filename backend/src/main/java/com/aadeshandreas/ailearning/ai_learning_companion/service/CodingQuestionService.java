package com.aadeshandreas.ailearning.ai_learning_companion.service;

import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.CodingQuestion;
import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.CodingTopic;
import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.CodingTopicList;
import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.ExecutionResult;
import com.aadeshandreas.ailearning.ai_learning_companion.repository.DocumentRepository;
import com.aadeshandreas.ailearning.ai_learning_companion.repository.coding.CodingQuestionRepository;
import com.aadeshandreas.ailearning.ai_learning_companion.repository.coding.CodingTopicRepository;
import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.CodeExecutor;
import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.CodingQuestionGenerator;
import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.CodingTopicExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CodingQuestionService {
    private final CodingTopicRepository codingTopicRepository;
    private final DocumentRepository documentRepository;
    private final CodingQuestionRepository codingQuestionRepository;
    private final CodingTopicExtractor codingTopicExtractor;
    private final CodingQuestionGenerator codingQuestionGenerator;
    private final CodeExecutor codeExecutor;

    @Autowired
    public CodingQuestionService(
            CodingTopicRepository codingTopicRepository,
            DocumentRepository documentRepository,
            CodingQuestionRepository codingQuestionRepository,
            CodingTopicExtractor codingTopicExtractor,
            CodingQuestionGenerator codingQuestionGenerator,
            CodeExecutor codeExecutor
    ) {
        this.codingTopicRepository = codingTopicRepository;
        this.documentRepository = documentRepository;
        this.codingQuestionRepository = codingQuestionRepository;
        this.codingTopicExtractor = codingTopicExtractor;
        this.codingQuestionGenerator = codingQuestionGenerator;
        this.codeExecutor = codeExecutor;
    }

    /**
     * Extracts 5 programming topics from the currently stored document, using a cache.
     * @return The extracted or cached CodingTopicList object.
     */
    public CodingTopicList extractCodingTopics() {
        if (codingTopicRepository.getCodingTopics() != null) {
            return codingTopicRepository.getCodingTopics();
        }

        String documentText = documentRepository.getDocumentText();
        CodingTopicList topics = codingTopicExtractor.generate(documentText);
        codingTopicRepository.setCodingTopics(topics);
        return topics;
    }

    /**
     * Generates a coding question for the selected topic and difficulty level.
     * @param topicId The ID of the selected topic
     * @param difficulty The desired difficulty level (EASY, MEDIUM, HARD)
     * @return The generated CodingQuestion object
     */
    public CodingQuestion generateCodingQuestion(int topicId, String difficulty) {
        // Get the cached topics
        CodingTopicList topicList = codingTopicRepository.getCodingTopics();
        if (topicList == null || topicList.getTopics() == null) {
            throw new IllegalStateException("No topics found. Please call /coding-topics first.");
        }

        // Find the selected topic
        CodingTopic selectedTopic = null;
        for (CodingTopic codingTopic : topicList.getTopics()) {
            if (codingTopic.getId() == topicId) {
                selectedTopic = codingTopic;
            }
        }

        if (selectedTopic == null) {
            throw new IllegalArgumentException("Topic with ID " + topicId + " not found");
        }
        
        // Get document context
        String documentText = documentRepository.getDocumentText();

        // Generate the question
        CodingQuestion question = codingQuestionGenerator.generateQuestion(
                selectedTopic.getTitle(),
                selectedTopic.getDescription(),
                difficulty,
                documentText
        );

        // Set the topicId in the question
        question.setTopicId(topicId);

        // Cache the question
        codingQuestionRepository.save(question);

        return question;
    }

    /**
     * Retrieves a previously generated coding question by its ID.
     * @param questionId The UUID of the question
     * @return The CodingQuestion object, or null if not found
     */
    public CodingQuestion getCodingQuestion(String questionId) {
        return codingQuestionRepository.findById(questionId);
    }

    /**
     * Submits user code for validation against all test cases.
     * @param questionId The UUID of the question being solved
     * @param userCode The user's complete Java solution
     * @return ExecutionResult with all test results
     */
    public ExecutionResult submitCodingQuestion(String questionId, String userCode) {
        CodingQuestion question = codingQuestionRepository.findById(questionId);
        if (question == null) {
            throw new IllegalArgumentException("Question with ID " + questionId + " not found");
        }

        return codeExecutor.executeCode(userCode, question, false);
    }

    /**
     * Tests user code against only visible test cases (for practice).
     * @param questionId The UUID of the question being solved
     * @param userCode The user's complete Java solution
     * @return ExecutionResult with visible test results only
     */
    public ExecutionResult testCodingQuestion(String questionId, String userCode) {
        CodingQuestion question = codingQuestionRepository.findById(questionId);
        if (question == null) {
            throw new IllegalArgumentException("Question with ID " + questionId + " not found");
        }

        return codeExecutor.executeCode(userCode, question, true);
    }
}
