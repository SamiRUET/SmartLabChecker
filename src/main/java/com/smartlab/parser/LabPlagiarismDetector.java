package com.smartlab.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.stream.Stream;

public class LabPlagiarismDetector {

    public record PlagiarismMatch(String suspect, String original, double similarity, String suspectTime, String originalTime) {}

    record Submission(String name, Path path, FileTime time, Set<String> tokens) {}

    public static List<PlagiarismMatch> scanFolder(Path labFolder, double thresholdPercent) {
        List<PlagiarismMatch> matches = new ArrayList<>();
        List<Submission> submissions = new ArrayList<>();

        // 1. Walk directory and collect all .java student files
        try (Stream<Path> stream = Files.walk(labFolder)) {
            List<Path> files = stream.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .toList();

            for (Path file : files) {
                String code = Files.readString(file);
                FileTime time = Files.getLastModifiedTime(file);
                String name = file.getFileName().toString().replace(".java", "");
                submissions.add(new Submission(name, file, time, extractTokens(code)));
            }
        } catch (IOException e) {
            System.err.println("Folder read error: " + e.getMessage());
            return matches;
        }

        // 2. Compare every student against each other (N x N)
        for (int i = 0; i < submissions.size(); i++) {
            for (int j = i + 1; j < submissions.size(); j++) {
                Submission subA = submissions.get(i);
                Submission subB = submissions.get(j);

                double sim = computeSimilarity(subA.tokens(), subB.tokens());
                if (sim >= thresholdPercent) {
                    // Older timestamp = original author; Newer timestamp = copier
                    Submission orig = subA.time().compareTo(subB.time()) <= 0 ? subA : subB;
                    Submission copy = (orig == subA) ? subB : subA;

                    matches.add(new PlagiarismMatch(
                            copy.name(),
                            orig.name(),
                            sim,
                            formatTime(copy.time()),
                            formatTime(orig.time())
                    ));
                }
            }
        }
        return matches;
    }

    private static Set<String> extractTokens(String code) {
        String clean = code.replaceAll("//.*|(?s)/\\*.*?\\*/", "")
                .replaceAll("\\s+", " ")
                .toLowerCase();
        Set<String> tokens = new HashSet<>(Arrays.asList(clean.split("\\W+")));
        tokens.remove("");
        return tokens;
    }

    private static double computeSimilarity(Set<String> a, Set<String> b) {
        if (a.isEmpty() || b.isEmpty()) return 0.0;
        Set<String> inter = new HashSet<>(a);
        inter.retainAll(b);
        Set<String> union = new HashSet<>(a);
        union.addAll(b);
        return ((double) inter.size() / union.size()) * 100.0;
    }

    private static String formatTime(FileTime time) {
        return time.toString().substring(0, 19).replace("T", " ");
    }
}