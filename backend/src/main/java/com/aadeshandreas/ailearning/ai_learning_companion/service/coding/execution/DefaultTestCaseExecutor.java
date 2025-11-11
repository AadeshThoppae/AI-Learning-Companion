package com.aadeshandreas.ailearning.ai_learning_companion.service.coding.execution;

import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.CodingQuestion;
import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.TestCase;
import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.TestResult;
import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.execution.strategy.ExecutionStrategy;
import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.execution.strategy.ExecutionStrategyFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Default implementation of TestCaseExecutor that uses strategy pattern
 * to select the appropriate execution approach.
 */
@Component
public class DefaultTestCaseExecutor implements TestCaseExecutor {

    private final ExecutionStrategyFactory strategyFactory;

    public DefaultTestCaseExecutor(ExecutionStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    @Override
    public TestResult executeTestCase(Class<?> compiledClass, TestCase testCase, CodingQuestion question) {
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            // Select the appropriate strategy
            ExecutionStrategy strategy = strategyFactory.selectStrategy(compiledClass, testCase);

            // Execute using the selected strategy
            return strategy.execute(compiledClass, testCase, question, executor);
        }
    }
}
