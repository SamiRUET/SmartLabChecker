package com.smartlab.parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.IOException;
import java.nio.file.Path;

public class JavaParserService {

    /**
     * Reads a .java file Path and parses it into an AST CompilationUnit.
     */
    public CompilationUnit parse(Path filePath) throws IOException {
        return StaticJavaParser.parse(filePath);
    }
}