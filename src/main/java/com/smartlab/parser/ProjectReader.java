package com.smartlab.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ProjectReader {

    /**
     * Recursively scans the given folder and returns all .java files found,
     * including files inside subfolders.
     */
    public List<Path> getJavaFiles(Path rootFolder) {
        List<Path> javaFiles = new ArrayList<>();

        if (rootFolder == null || !Files.isDirectory(rootFolder)) {
            return javaFiles;
        }

        try (Stream<Path> paths = Files.walk(rootFolder)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(javaFiles::add);
        } catch (IOException e) {
            System.err.println("Error reading folder: " + e.getMessage());
        }

        return javaFiles;
    }

    /**
     * Convenience overload if you have a java.io.File instead of a Path.
     */
    public List<Path> getJavaFiles(File folder) {
        if (folder == null) {
            return new ArrayList<>();
        }
        return getJavaFiles(folder.toPath());
    }
}