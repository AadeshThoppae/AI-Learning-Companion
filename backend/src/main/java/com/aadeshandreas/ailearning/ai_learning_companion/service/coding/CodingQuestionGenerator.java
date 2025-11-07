package com.aadeshandreas.ailearning.ai_learning_companion.service.coding;

import com.aadeshandreas.ailearning.ai_learning_companion.model.coding.CodingQuestion;
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

            Document Context (for additional reference):
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

            5. Starter Code: Template code with TODO comments where the user should write their solution
               - Include the method signature
               - Add helpful TODO comments
               - Example:
                 public class Solution {
                     public static boolean isValid(String s) {
                         // TODO: Implement your solution here
                         return false;
                     }
                 }

            6. Test Cases: Create exactly 5 test cases:
               - First 2 test cases: Set hidden = false (these are shown to the user as examples)
               - Last 3 test cases: Set hidden = true (these are for validation only)
               - Cover edge cases: empty input, single element, large input, boundary conditions
               - Each test case should have:
                 * id: 1, 2, 3, 4, 5
                 * input: Serialized input as a string (e.g., "[1,2,3]" or "5" or ""hello"")
                 * expectedOutput: The expected return value as a string
                 * hidden: false for first 2, true for last 3

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

            Generate the complete coding question now.
            """)
    CodingQuestion generateQuestion(
            @V("topicTitle") String topicTitle,
            @V("topicDescription") String topicDescription,
            @V("difficulty") String difficulty,
            @V("documentContext") String documentContext
    );
}