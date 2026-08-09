package com.smartlab.parser;

import java.nio.file.Path;
import java.util.List;

public class ProjectReaderTest {
    public static void main(String[] args) {
        ProjectReader reader = new ProjectReader();

        // Change this path to any folder on your computer that contains .java files
        // Example: your own SmartLabChecker project folder, to test recursively
        Path testFolder = Path.of("C:\\Users\\Acer\\IdeaProjects\\SmartLabChecker");

        List<Path> javaFiles = reader.getJavaFiles(testFolder);

        System.out.println("Found " + javaFiles.size() + " .java files:");
        for (Path file : javaFiles) {
            System.out.println(" - " + file.getFileName());
        }
    }
}