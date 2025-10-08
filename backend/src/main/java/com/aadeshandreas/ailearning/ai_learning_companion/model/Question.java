package com.aadeshandreas.ailearning.ai_learning_companion.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Question {
    private int id;
    private String question;
    private List<AnswerOption> options;
    private int answerId;

}
