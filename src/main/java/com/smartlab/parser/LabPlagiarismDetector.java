package com.smartlab.parser;

import com.smartlab.model.PlagiarismMatch;
import com.smartlab.model.ReferenceMatch;
import com.smartlab.model.Submission;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.*;

public class LabPlagiarismDetector {

    /**
     * Scans specifically selected student files and compares them against each other.
     */
    public static List<PlagiarismMatch> scanStudents(List<Path> files) {
        List<PlagiarismMatch> matches = new ArrayList<>();
        List<Submission> submissions = loadSubmissions(files);

        for (int i = 0; i < submissions.size(); i++) {
            for (int j = i + 1; j < submissions.size(); j++) {
                Submission subA = submissions.get(i);
                Submission subB = submissions.get(j);

                double sim = computeSimilarity(subA.tokens(), subB.tokens());
                matches.add(new PlagiarismMatch(
                        subA.name(),
                        subB.name(),
                        sim,
                        subA.getFormattedTime(),
                        subB.getFormattedTime()
                ));
            }
        }

        // Sort from highest to lowest similarity
        matches.sort((a, b) -> Double.compare(b.similarity(), a.similarity()));
        return matches;
    }

    /**
     * Compares every student submission against a single official reference solution.
     */
    public static List<ReferenceMatch> scanAgainstReference(List<Path> studentFiles, Path referenceFile) {
        List<ReferenceMatch> matches = new ArrayList<>();
        List<Submission> submissions = loadSubmissions(studentFiles);

        Submission refSub = loadSingleSubmission(referenceFile);
        if (refSub == null) return matches;

        for (Submission sub : submissions) {
            double sim = computeSimilarity(sub.tokens(), refSub.tokens());
            matches.add(new ReferenceMatch(sub.name(), sim));
        }

        // Sort from highest similarity to lowest
        matches.sort((a, b) -> Double.compare(b.similarity(), a.similarity()));
        return matches;
    }

    //takes all code from the students
    private static List<Submission> loadSubmissions(List<Path> files) {
        List<Submission> submissions = new ArrayList<>();
        for (Path file : files) {
            Submission sub = loadSingleSubmission(file);
            if (sub != null) {
                submissions.add(sub);
            }
        }
        return submissions;
    }

    //takes the reference code
    private static Submission loadSingleSubmission(Path file) {
        try {
            String code = Files.readString(file);
            FileTime time = Files.getLastModifiedTime(file);
            String name = file.getFileName().toString().replace(".java", "");
            return new Submission(name, file, time, extractTokens(code));
        } catch (IOException e) {
            System.err.println("Failed to read file: " + file.getFileName());
            return null;
        }
    }

    /**
     * removes side comment and converts all letters to lowercase
     */
    private static Set<String> extractTokens(String code) {
        // Remove line comments and block comments
        String clean = code.replaceAll("//.*|(?s)/\\*.*?\\*/", "")
                // Remove string literals (prevents strings from inflating matches)
                .replaceAll("\".*?\"", "")
                .replaceAll("\\s+", " ")
                .toLowerCase();

        Set<String> tokens = new HashSet<>(Arrays.asList(clean.split("\\W+")));
        tokens.remove("");
        return tokens;
    }

    //makes a set and compares two with intersection and union
    private static double computeSimilarity(Set<String> a, Set<String> b) {
        if (a.isEmpty() || b.isEmpty()) return 0.0;
        Set<String> inter = new HashSet<>(a);
        inter.retainAll(b);
        Set<String> union = new HashSet<>(a);
        union.addAll(b);
        return ((double) inter.size() / union.size()) * 100.0;
    }
}