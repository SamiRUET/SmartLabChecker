package com.smartlab.parser;

import com.github.javaparser.ast.CompilationUnit;
import java.nio.file.Path;
import java.util.List;

public class ProjectReaderTest {
    public static void main(String[] args) {
        ProjectReader reader = new ProjectReader();
        JavaParserService parserService = new JavaParserService();

        Path testFolder = Path.of("C:\\Users\\Acer\\IdeaProjects\\SmartLabChecker");
        List<Path> javaFiles = reader.getJavaFiles(testFolder);

        System.out.println("Found " + javaFiles.size() + " .java files.\n");

        if (!javaFiles.isEmpty()) {
            // Pick the first .java file found
            Path targetFile = javaFiles.get(0);
            System.out.println("Parsing target file: " + targetFile.getFileName());

            try {
                // Parse it into an AST CompilationUnit
                CompilationUnit cu = parserService.parse(targetFile);

                // Inspect basic AST information
                System.out.println("--- AST Information ---");
                System.out.println("Package: " + cu.getPackageDeclaration().map(p -> p.getNameAsString()).orElse("default package"));
                System.out.println("Imports count: " + cu.getImports().size());
                System.out.println("Primary Class: " + cu.getPrimaryTypeName().orElse("none"));

            } catch (Exception e) {
                System.err.println("Failed to parse AST: " + e.getMessage());
            }
        }
    }
}