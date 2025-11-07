package com.aadeshandreas.ailearning.ai_learning_companion.service;

import com.aadeshandreas.ailearning.ai_learning_companion.model.content.FlashcardList;
import com.aadeshandreas.ailearning.ai_learning_companion.model.content.Quiz;
import com.aadeshandreas.ailearning.ai_learning_companion.model.content.Summary;
import com.aadeshandreas.ailearning.ai_learning_companion.repository.DocumentRepository;
import com.aadeshandreas.ailearning.ai_learning_companion.repository.content.FlashcardRepository;
import com.aadeshandreas.ailearning.ai_learning_companion.repository.content.QuizRepository;
import com.aadeshandreas.ailearning.ai_learning_companion.repository.content.SummaryRepository;
import com.aadeshandreas.ailearning.ai_learning_companion.service.generation.FlashcardGenerator;
import com.aadeshandreas.ailearning.ai_learning_companion.service.generation.QuizGenerator;
import com.aadeshandreas.ailearning.ai_learning_companion.service.generation.Summarizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContentGenerationService {

    private final SummaryRepository summaryRepository;
    private final DocumentRepository documentRepository;
    private final FlashcardRepository flashcardRepository;
    private final QuizRepository quizRepository;
    private final Summarizer summarizer;
    private final FlashcardGenerator flashcardGenerator;
    private final QuizGenerator quizGenerator;

    @Autowired
    public ContentGenerationService(
            SummaryRepository summaryRepository,
            DocumentRepository documentRepository,
            FlashcardRepository flashcardRepository,
            QuizRepository quizRepository,
            Summarizer summarizer,
            FlashcardGenerator flashcardGenerator,
            QuizGenerator quizGenerator
    ) {
        this.summaryRepository = summaryRepository;
        this.documentRepository = documentRepository;
        this.flashcardRepository = flashcardRepository;
        this.quizRepository = quizRepository;
        this.summarizer = summarizer;
        this.flashcardGenerator = flashcardGenerator;
        this.quizGenerator = quizGenerator;
    }

    /**
     * Generates a summary from the currently stored document, using a cache to avoid repeat AI calls.
     * @return The generated or cached Summary object.
     */
    public Summary generateSummary() {
        if (summaryRepository.getSummary() != null) {
            return summaryRepository.getSummary();
        }

        String documentText = documentRepository.getDocumentText();
        Summary summary = summarizer.generate(documentText);
        summaryRepository.setSummary(summary);
        return summary;
    }

    /**
     * Generates flashcards from the currently stored document, using a cache.
     * @return The generated or cached FlashcardList object.
     */
    public FlashcardList generateFlashcards() {
        if (flashcardRepository.getFlashcardList() != null) {
            return flashcardRepository.getFlashcardList();
        }

        String documentText = documentRepository.getDocumentText();
        FlashcardList flashcardList = flashcardGenerator.generate(documentText);
        flashcardRepository.setFlashcardList(flashcardList);
        return flashcardList;
    }

    public Quiz generateQuiz(){
        if (quizRepository.getQuiz() != null){
            return quizRepository.getQuiz();
        }

        String docText = documentRepository.getDocumentText();
        Quiz quiz = quizGenerator.generate(docText);
        quizRepository.setQuiz(quiz);
        return quiz;
    }
}
