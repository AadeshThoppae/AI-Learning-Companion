package com.aadeshandreas.ailearning.ai_learning_companion.service.coding.compilation;

import org.springframework.stereotype.Component;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Compiles Java code in-memory using the Java Compiler API.
 */
@Component
public class InMemoryCodeCompiler implements CodeCompiler {

    @Override
    public Class<?> compileCode(String code) throws CompilationException {
        try {
            // Extract class name from code
            String className = ClassNameExtractor.extractClassName(code);
            if (className == null) {
                throw new CompilationException("Could not find public class declaration");
            }

            // Get system Java compiler
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                throw new CompilationException("Java compiler not available. Ensure you're running on JDK, not JRE.");
            }

            // Create in-memory file manager
            InMemoryFileManager fileManager = new InMemoryFileManager(
                    compiler.getStandardFileManager(null, null, null)
            );

            // Create in-memory source file
            JavaFileObject sourceFile = new InMemoryJavaFile(className, code);

            // Compile
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            JavaCompiler.CompilationTask task = compiler.getTask(
                    null,
                    fileManager,
                    diagnostics,
                    null,
                    null,
                    Collections.singletonList(sourceFile)
            );

            boolean success = task.call();
            if (!success) {
                StringBuilder errorMsg = new StringBuilder();
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    errorMsg.append("Line ").append(diagnostic.getLineNumber())
                            .append(": ").append(diagnostic.getMessage(null))
                            .append("\n");
                }
                throw new CompilationException(errorMsg.toString());
            }

            // Load the compiled class (and any nested classes)
            Map<String, byte[]> allClassBytes = fileManager.getAllClassBytes();
            InMemoryClassLoader classLoader = new InMemoryClassLoader(allClassBytes);
            return classLoader.loadClass(className);

        } catch (Exception e) {
            if (e instanceof CompilationException) {
                throw (CompilationException) e;
            }
            throw new CompilationException("Compilation failed: " + e.getMessage());
        }
    }

    /**
     * In-memory representation of a Java source file.
     */
    private static class InMemoryJavaFile extends SimpleJavaFileObject {
        private final String code;

        protected InMemoryJavaFile(String className, String code) {
            super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }

    /**
     * In-memory file manager for compiled bytecode.
     */
    private static class InMemoryFileManager extends ForwardingJavaFileManager<JavaFileManager> {
        private final Map<String, ByteArrayOutputStream> classBytes = new HashMap<>();

        protected InMemoryFileManager(JavaFileManager fileManager) {
            super(fileManager);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            classBytes.put(className, baos);
            return new SimpleJavaFileObject(URI.create("bytes:///" + className), kind) {
                @Override
                public OutputStream openOutputStream() {
                    return baos;
                }
            };
        }

        public Map<String, byte[]> getAllClassBytes() {
            Map<String, byte[]> result = new HashMap<>();
            for (Map.Entry<String, ByteArrayOutputStream> entry : classBytes.entrySet()) {
                result.put(entry.getKey(), entry.getValue().toByteArray());
            }
            return result;
        }
    }

    /**
     * ClassLoader for loading classes from byte arrays.
     */
    private static class InMemoryClassLoader extends ClassLoader {
        private final Map<String, byte[]> classBytes;

        public InMemoryClassLoader(Map<String, byte[]> classBytes) {
            this.classBytes = classBytes;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] bytes = classBytes.get(name);
            if (bytes != null) {
                return defineClass(name, bytes, 0, bytes.length);
            }
            return super.findClass(name);
        }
    }
}
