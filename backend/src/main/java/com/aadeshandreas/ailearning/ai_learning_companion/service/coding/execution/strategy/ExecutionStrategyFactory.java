package com.aadeshandreas.ailearning.ai_learning_companion.service.coding.execution.strategy;

import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.TestCase;
import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.comparison.OutputComparator;
import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.formatting.OutputFormatter;
import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.parsing.DefaultInputParser;
import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.parsing.InputParser;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for selecting the appropriate execution strategy based on test case characteristics.
 */
@Component
public class ExecutionStrategyFactory {

    private final List<ExecutionStrategy> strategies;

    public ExecutionStrategyFactory(InputParser inputParser, OutputFormatter outputFormatter, OutputComparator outputComparator) {
        this.strategies = new ArrayList<>();

        // Order matters - more specific strategies should come first
        // SingleMethodExecutionStrategy is the fallback, so it should be last

        // Cast InputParser to DefaultInputParser for strategies that need parseOperationArguments
        DefaultInputParser defaultInputParser = (DefaultInputParser) inputParser;

        strategies.add(new OperationBasedExecutionStrategy(defaultInputParser));
        strategies.add(new CounterSimulationExecutionStrategy());
        strategies.add(new SingleMethodExecutionStrategy(inputParser, outputFormatter, outputComparator));
    }

    /**
     * Selects the appropriate execution strategy for the given test case.
     *
     * @param compiledClass The compiled class
     * @param testCase The test case
     * @return The appropriate execution strategy
     */
    public ExecutionStrategy selectStrategy(Class<?> compiledClass, TestCase testCase) {
        for (ExecutionStrategy strategy : strategies) {
            if (strategy.canHandle(compiledClass, testCase)) {
                return strategy;
            }
        }

        // This should never happen as SingleMethodExecutionStrategy always returns true
        throw new IllegalStateException("No execution strategy found for test case");
    }
}
