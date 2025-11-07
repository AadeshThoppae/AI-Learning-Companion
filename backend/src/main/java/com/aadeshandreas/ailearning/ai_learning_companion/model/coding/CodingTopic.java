package com.aadeshandreas.ailearning.ai_learning_companion.model.coding;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Represents a programming topic extracted from the document that can be used
 * to generate coding practice questions.
 */
@Getter
@Setter
public class CodingTopic {
    private int id;
    private String title;           // e.g., "Binary Search Trees"
    private String description;     // e.g., "Implement and traverse BST structures"
    private Difficulty difficulty;      // EASY, MEDIUM, HARD
    private List<String> keywords;  // ["BST", "recursion", "tree traversal"]
}