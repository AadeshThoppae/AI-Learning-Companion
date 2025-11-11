package com.aadeshandreas.ailearning.ai_learning_companion.service.coding;

import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.CodingQuestion;
import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.ExecutionResult;
import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.TestCase;
import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.TestResult;
import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.compilation.CodeCompiler;
import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.compilation.CompilationException;
import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.execution.TestCaseExecutor;
import com.aadeshandreas.ailearning.ai_learning_companion.service.coding.validation.CodeValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for compiling and executing user-submitted Java code in a sandboxed environment.
 * Provides secure code execution with timeout limits and security validation.
 * This class orchestrates the execution flow by delegating to specialized components:
 * - CodeValidator: Security validation
 * - CodeCompiler: In-memory compilation
 * - TestCaseExecutor: Test execution with strategy pattern
 */
@Service
public class CodeExecutor {

    private final CodeValidator codeValidator;
    private final CodeCompiler codeCompiler;
    private final TestCaseExecutor testCaseExecutor;

    public CodeExecutor(CodeValidator codeValidator, CodeCompiler codeCompiler, TestCaseExecutor testCaseExecutor) {
        this.codeValidator = codeValidator;
        this.codeCompiler = codeCompiler;
        this.testCaseExecutor = testCaseExecutor;
    }

    /**
     * Executes user code against test cases.
     *
     * @param userCode The complete Java code submitted by the user
     * @param question The coding question containing test cases
     * @param visibleOnly If true, only run visible test cases (for "Run Code" button)
     * @return ExecutionResult containing test results and metrics
     */
    public ExecutionResult executeCode(String userCode, CodingQuestion question, boolean visibleOnly) {
        ExecutionResult result = new ExecutionResult();
        List<TestResult> testResults = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        try {
            // Step 1: Validate code for security
            codeValidator.validateCode(userCode);

            // Step 2: Compile the code
            Class<?> compiledClass = codeCompiler.compileCode(userCode);

            // Step 3: Run test cases
            List<TestCase> testCases = visibleOnly
                    ? question.getTestCases().stream().filter(tc -> !tc.isHidden()).toList()
                    : question.getTestCases();

            for (TestCase testCase : testCases) {
                TestResult testResult = testCaseExecutor.executeTestCase(compiledClass, testCase, question);
                testResults.add(testResult);
            }

            // Step 4: Calculate results
            long passedCount = testResults.stream().filter(TestResult::isPassed).count();
            result.setSuccess(passedCount == testResults.size());
            result.setPassedTests((int) passedCount);
            result.setTotalTests(testResults.size());
            result.setResults(testResults);

        } catch (SecurityException e) {
            result.setSuccess(false);
            result.setError("Security violation: " + e.getMessage());
            result.setTotalTests(0);
            result.setPassedTests(0);
        } catch (CompilationException e) {
            result.setSuccess(false);
            result.setError("Compilation error: " + e.getMessage());
            result.setTotalTests(0);
            result.setPassedTests(0);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setError("Runtime error: " + e.getMessage());
            result.setTotalTests(testResults.size());
            result.setPassedTests((int) testResults.stream().filter(TestResult::isPassed).count());
            result.setResults(testResults);
        }

        long executionTime = System.currentTimeMillis() - startTime;
        result.setExecutionTime(executionTime);

        return result;
    }
}
