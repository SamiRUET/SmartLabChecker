package com.smartlab.ui;

import com.smartlab.parser.LabPlagiarismDetector;
import com.smartlab.parser.LabPlagiarismDetector.PlagiarismMatch;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Plagiarism Detector");

        Label lblTitle = new Label("Select Student Java Files to Compare:");
        Button btnSelectFiles = new Button("📄 Select Student Files (Multi-Select)");
        ListView<String> resultsList = new ListView<>();
        Label lblSummary = new Label("Status: Ready to scan.");

        btnSelectFiles.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select Student Files");
            // Shows only .java files in the dialog
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Java Files (*.java)", "*.java")
            );

            // Opens multi-file picker (Hold Ctrl or Shift to select multiple)
            List<File> selectedFiles = chooser.showOpenMultipleDialog(stage);

            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                lblSummary.setText("Scanning " + selectedFiles.size() + " files...");
                resultsList.getItems().clear();

                // If user selected files inside a folder, scan their parent folder
                File parentDir = selectedFiles.get(0).getParentFile();
                List<PlagiarismMatch> matches = LabPlagiarismDetector.scanFolder(parentDir.toPath(), 70.0);

                if (matches.isEmpty()) {
                    resultsList.getItems().add("✅ No matching submissions found (Threshold: 70%).");
                    lblSummary.setText("Status: Clean. No plagiarism detected.");
                } else {
                    for (PlagiarismMatch m : matches) {
                        resultsList.getItems().add(
                                "🚨 " + m.suspect() + " copied from " + m.original() +
                                        " | " + String.format("%.1f%%", m.similarity()) + " Match" +
                                        " | Saved at: " + m.suspectTime()
                        );
                    }
                    lblSummary.setText("Status: " + matches.size() + " match(es) detected!");
                }
            }
        });

        VBox layout = new VBox(12, lblTitle, btnSelectFiles, resultsList, lblSummary);
        layout.setPadding(new Insets(15));
        resultsList.setPrefHeight(320);

        stage.setScene(new Scene(layout, 680, 440));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}