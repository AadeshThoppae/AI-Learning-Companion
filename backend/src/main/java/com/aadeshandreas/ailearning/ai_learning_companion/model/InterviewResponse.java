package com.aadeshandreas.ailearning.ai_learning_companion.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterviewResponse {
    private String userAnswer;
    private int score;
    private String feedback;
    private String suggestions;
    private String strengths;
}
