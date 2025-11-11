package com.aadeshandreas.ailearning.ai_learning_companion.repository.content;

import com.aadeshandreas.ailearning.ai_learning_companion.model.content.Summary;
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
public class SummaryRepository {
    private Summary summary;
}
