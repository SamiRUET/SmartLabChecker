package com.smartlab.model;

public record PlagiarismMatch(
        String studentA,
        String studentB,
        double similarity,
        String timeA,
        String timeB) {
}