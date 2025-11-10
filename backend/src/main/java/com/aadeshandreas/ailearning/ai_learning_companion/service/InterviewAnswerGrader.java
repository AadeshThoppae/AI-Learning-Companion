package com.aadeshandreas.ailearning.ai_learning_companion.service;

import com.aadeshandreas.ailearning.ai_learning_companion.model.Interview;
import com.aadeshandreas.ailearning.ai_learning_companion.model.InterviewResponse;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface InterviewAnswerGrader extends ContentGenerator<InterviewResponse >{
    /**
     * Grades a user's interview answer by comparing it to the perfect answer and the original
     * question context. This method will be automatically implemented by LangChain4j
     *
     * @param question the interview question that was asked
     * @param answer the model answer demonstrating complete understanding
     * @param userAnswer The user's submitted answer to be graded
     * @return A {@link InterviewResponse} containing score, feedback, and suggestions
     */
    @UserMessage("""
            You are an expert interviewer evaluating a candidate's answer.
            
            Question: {{answer}}
            
            Perfect Answer(for reference): {{answer}}
            
            Please evaluate the candidate's answer and provide:
            1. A score out of 10
            2. Detailed feedback on their answer
            3. Specific strengths in their response
            4. Concrete suggestions for improvement
            
            Become encouraging but honest. Consider: 
            - Accuracy and completeness of concepts
            - Specific strengths in their response
            - Use of relevant examples or details
            - Overall understanding demonstrated
           
            Even if the answer doesn't match the perfect answer word for word,
            give credit for demonstrating understanding in their own words.
            """)
    InterviewResponse gradeAnswer(@V("question") String question,
                                  @V("answer") String answer,
                                  @V("userAnswer") String userAnswer);
}
