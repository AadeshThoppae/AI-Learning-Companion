package com.aadeshandreas.ailearning.ai_learning_companion.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Flashcard implements ApiResponse {
    private int id;
    private String question;
    private String answer;
    private String hint;
}
