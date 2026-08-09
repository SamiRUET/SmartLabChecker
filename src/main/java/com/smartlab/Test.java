package com.smartlab;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Test {
    public static void printHierarchy(Path rootFolder){
        if(!Files.exists(rootFolder)){
            throw new IllegalArgumentException("Target folder does not exist");
        }
        try(Stream<Path> stream=Files.walk(rootFolder)){
            stream.forEach(path->{
                int depth= path.getNameCount()-rootFolder.getNameCount();
                String space= "   ".repeat(depth);
                String prefix= Files.isDirectory(path)?"[DIR]":"[FILE]";
                System.out.println(space + prefix + path.getFileName());
            });
        }
        catch (IOException e) {
            System.err.println("Error reading directory tree: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        Path root=Path.of("src");
        printHierarchy(root);
    }
}

