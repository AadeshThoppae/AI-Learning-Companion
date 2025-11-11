package com.aadeshandreas.ailearning.ai_learning_companion.service.coding;

import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.CodingQuestion;
import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.Difficulty;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * AI service interface for generating LeetCode-style coding questions.
 * Implemented automatically by LangChain4j at runtime.
 */
public interface CodingQuestionGenerator {

    /**
     * Generates a complete coding question for the specified topic.
     * This method will be implemented by LangChain4j, which sends the prompt
     * to the AI model and parses the response into a {@link CodingQuestion}.
     *
     * @param topicTitle The title of the programming topic
     * @param topicDescription Description of what the topic involves
     * @param difficulty The desired difficulty level (EASY, MEDIUM, or HARD)
     * @param documentContext The original document text for additional context
     * @return A {@link CodingQuestion} with problem description, examples, and test cases
     */
    @UserMessage("""
            Generate a LeetCode-style coding question in Java for the following topic:

            Topic Title: {{topicTitle}}
            Topic Description: {{topicDescription}}
            Difficulty Level: {{difficulty}}

            Summary (for context):
            {{documentContext}}

            Create a complete coding problem with the following components:

            1. Title: A clear, descriptive title for the problem (3-6 words)

            2. Description: A detailed problem description that explains:
               - What the user needs to implement
               - Any important concepts or constraints
               - Use clear, beginner-friendly language

            3. Examples: Provide 2-3 input/output examples, each with:
               - input: The input data (as a string representation)
               - output: The expected output (as a string representation)
               - explanation: A brief explanation of why this is the correct output

            4. Method Signature: The exact Java method signature the user should implement
               - Use standard Java types (int, String, int[], List<Integer>, etc.)
               - Method should be public and static for easy testing
               - Example: "public static boolean isValid(String s)"

            <starterCode>
            5. Starter Code: CRITICAL - TEMPLATE ONLY - DO NOT IMPLEMENT THE SOLUTION
               - Include method signature with empty body or simple return statement
               - Add TODO comments as hints
               - The actual solution logic must be MISSING - students will implement it

               WRONG (has complete solution):
                 public class Solution {
                     public static boolean isPalindrome(String s) {
                         return s.equals(new StringBuilder(s).reverse().toString());
                     }
                 }

               RIGHT (incomplete template):
                 public class Solution {
                     public static boolean isPalindrome(String s) {
                         // TODO: Implement your solution here
                         return false;
                     }
                 }
            </starterCode>

            6. Test Cases: Create exactly 5 test cases:
               - First 2 test cases: Set hidden = false (these are shown to the user as examples)
               - Last 3 test cases: Set hidden = true (these are for validation only)
               - Cover edge cases: empty input, single element, large input, boundary conditions
               - Each test case should have:
                 * id: 1, 2, 3, 4, 5
                 * input: Input parameters as a SIMPLE COMMA-SEPARATED STRING
                 * expectedOutput: The expected return value as a string
                 * hidden: false for first 2, true for last 3

            CRITICAL INPUT FORMAT RULES - Use comma-separated values only:
            CORRECT: "5" | "\"hello\"" | "[1,2,3]" | "2, [100,50]" | "[1,2,3], 1, 2"
            WRONG: {"numWorkers":2} | paramName=value | JSON format

            Examples by parameter type:
            - Single int/String/array: "5" or "\"hello\"" or "[1,2,3]"
            - Multiple params: "2, [100,50]" or "[1,2,3], 1, 2" or "\"test\", 5"
            - List<Integer>: "[100, 50]" (bracket notation, will be converted)
            - String[][]: "[[\"a\",\"b\"],[\"c\",\"d\"]]"

            7. Hints: Provide 2-3 helpful hints (as a single string, one hint per line)

            8. Constraints: Include:
               - timeComplexity: The expected time complexity (e.g., "O(n)", "O(n log n)")
               - spaceComplexity: The expected space complexity (e.g., "O(1)", "O(n)")
               - rules: A list of 2-3 constraint statements about input size and properties
                 (e.g., "1 <= n <= 10^4", "All values are unique")

            Important guidelines:
            - The question should test understanding of the specified topic at the given difficulty level
            - For EASY: Focus on basic implementation with straightforward logic
            - For MEDIUM: Require algorithmic thinking or multiple steps
            - For HARD: Involve complex algorithms, optimization, or advanced data structures
            - Ensure the problem is solvable and well-defined
            - Make test cases realistic and cover different scenarios
            - The method signature should match what's used in the starter code
            - ALWAYS follow the input format rules above - this is critical for automated testing

            <forbidden>
            LIMITATIONS - YOU MUST STRICTLY AVOID:
            FORBIDDEN: Custom test harnesses or operation sequences as inputs
            FORBIDDEN: Input formats like: Operations: ["insert", "delete"], Arguments: [[1], [2]]
            FORBIDDEN: Problems requiring step-by-step operation sequences
            FORBIDDEN: Simulating specific thread interleavings or timing-dependent behavior

            ALLOWED for concurrency: Simple direct parameters like:
               - CountDownLatch(int count) -> simple constructor test
               - increment() with no parameters -> simple method test
               - Thread-safe counter classes with getValue() methods
               - Blocking queue operations with simple put/take

            CRITICAL: Test inputs MUST be simple comma-separated values only.
            Examples of VALID inputs:
            - "5, 10" (two integers)
            - "[1, 2, 3], 5" (array and integer)
            - "\"hello\", true" (string and boolean)

            Examples of INVALID inputs (NEVER generate these):
            - Operations: ["enqueue", "dequeue"], Arguments: [[1], []]
            - ["INSERT", "GET"], [[key, value], [key]]
            - Any JSON-like nested structure with operation sequences
            </forbidden>

            VALIDATION - Verify before submitting:
            - Use ONLY standard Java API methods (no hallucinated method names)
            - Starter code must be INCOMPLETE (no solution logic, only TODO comments)
            - Starter code compiles without errors
            - Test inputs are comma-separated values (not JSON or custom DSL)
            - Problem is testable with simple parameter-based inputs
            - NO operation sequence patterns whatsoever

            <outputFormat>
            OUTPUT FORMAT - CRITICAL:
            Return ONLY the raw JSON object. Do NOT wrap it in markdown code blocks.
            Do NOT use ```json or ``` markers. Return pure JSON starting with { and ending with }.
            </outputFormat>

            Generate the complete coding question now.
            """)
    CodingQuestion generateQuestion(
            @V("topicTitle") String topicTitle,
            @V("topicDescription") String topicDescription,
            @V("difficulty") Difficulty difficulty,
            @V("documentContext") String documentContext
    );
}