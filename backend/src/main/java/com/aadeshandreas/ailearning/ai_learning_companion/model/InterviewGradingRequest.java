package com.aadeshandreas.ailearning.ai_learning_companion.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class InterviewGradingRequest {
    private Integer questionId;
    private String userAnswer;
}
