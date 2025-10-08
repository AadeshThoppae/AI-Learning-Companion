package com.aadeshandreas.ailearning.ai_learning_companion.repository;

import com.aadeshandreas.ailearning.ai_learning_companion.model.Quiz;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
@Setter
@Getter
@NoArgsConstructor
public class QuizRepository {
    private Quiz quiz;
}
