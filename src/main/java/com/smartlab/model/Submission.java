package com.smartlab.model;

import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Set;

public record Submission(String name, Path path, FileTime time, Set<String> tokens) {
    public String getFormattedTime() {
        if (time == null) return "Unknown";
        return time.toString().substring(0, 19).replace("T", " ");
    }
}