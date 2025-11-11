package com.aadeshandreas.ailearning.ai_learning_companion.service.coding.compilation;

/**
 * Interface for compiling Java code in-memory.
 */
public interface CodeCompiler {

    /**
     * Compiles Java source code and returns the compiled class.
     *
     * @param code The Java source code to compile
     * @return The compiled Class object
     * @throws CompilationException if compilation fails
     */
    Class<?> compileCode(String code) throws CompilationException;
}
