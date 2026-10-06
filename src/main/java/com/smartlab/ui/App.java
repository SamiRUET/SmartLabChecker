package com.smartlab.ui;

import com.smartlab.model.PlagiarismMatch;
import com.smartlab.model.ReferenceMatch;
import com.smartlab.parser.LabPlagiarismDetector;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class App extends Application {

    // State Variables
    private List<File> selectedStudentFiles;
    private File selectedReferenceFile;
    private ObservableList<PlagiarismMatch> allMatches = FXCollections.observableArrayList();
    private ObservableList<ReferenceMatch> refMatches = FXCollections.observableArrayList();

    // UI Components (Global for updating)
    private Label lblSelectedStudents;
    private Label lblSelectedReference;
    private TextField txtThreshold;
    private TextField txtSearch;

    // Stats
    private Label lblTotalStudents, lblComparisons, lblFlagged, lblHighestMatch;

    // Tables
    private TableView<PlagiarismMatch> tableAll, tableSuspicious;
    private TableView<ReferenceMatch> tableReference;
    private FilteredList<PlagiarismMatch> filteredMatches;

    // Chart
    private BarChart< String, Number> top3Chart; // Horizontal bar chart

    @Override
    public void start(Stage stage) {
        stage.setTitle("SmartLab - Code Plagiarism Analyzer");

        // Main Layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f6f9; -fx-font-family: 'Segoe UI', sans-serif;");

        // Header
        VBox header = new VBox();
        header.setPadding(new Insets(20, 20, 10, 20));
        Label title = new Label("SMART LAB");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #2c3e50;");
        Label subtitle = new Label("Code Plagiarism Analyzer");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        subtitle.setStyle("-fx-text-fill: #7f8c8d;");
        header.getChildren().addAll(title, subtitle);
        root.setTop(header);

        // Left Panel (Controls)
        root.setLeft(buildControlPanel(stage));

        // Center Panel (Tabs & Dashboard)
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabPane.getTabs().addAll(
                buildDashboardTab(),
                buildComparisonsTab(),
                buildSuspiciousTab(),
                buildReferenceTab()
        );
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1100, 700);
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildControlPanel(Stage stage) {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setPrefWidth(280);
        panel.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 1 0 0;");

        Label lblSection = new Label("Control Panel");
        lblSection.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        // Student Files Selection
        Button btnSelectStudents = new Button("📄 Select Student Files");
        btnSelectStudents.setMaxWidth(Double.MAX_VALUE);
        lblSelectedStudents = new Label("0 files selected");
        lblSelectedStudents.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

        btnSelectStudents.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java Files", "*.java"));
            List<File> files = chooser.showOpenMultipleDialog(stage);
            if (files != null && !files.isEmpty()) {
                selectedStudentFiles = files;
                lblSelectedStudents.setText(files.size() + " files selected");
            }
        });

        // Reference Solution Selection
        Button btnSelectReference = new Button("⭐ Select Reference (Optional)");
        btnSelectReference.setMaxWidth(Double.MAX_VALUE);
        lblSelectedReference = new Label("None selected");
        lblSelectedReference.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

        btnSelectReference.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java Files", "*.java"));
            File file = chooser.showOpenDialog(stage);
            if (file != null) {
                selectedReferenceFile = file;
                lblSelectedReference.setText(file.getName());
            }
        });

        // Threshold Input
        Label lblThresh = new Label("Plagiarism Threshold (%):");
        txtThreshold = new TextField("80");
        txtThreshold.setMaxWidth(80);

        // Action Buttons
        Button btnScan = new Button("▶ START ANALYSIS");
        btnScan.setMaxWidth(Double.MAX_VALUE);
        btnScan.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px;");
        btnScan.setOnAction(e -> runAnalysis());

        Button btnReset = new Button("⟲ CLEAR / RESET");
        btnReset.setMaxWidth(Double.MAX_VALUE);
        btnReset.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px;");
        btnReset.setOnAction(e -> resetApplication());

        panel.getChildren().addAll(
                lblSection,
                new Separator(),
                btnSelectStudents, lblSelectedStudents,
                btnSelectReference, lblSelectedReference,
                new Separator(),
                lblThresh, txtThreshold,
                new Separator(),
                btnScan, btnReset
        );
        return panel;
    }

    private Tab buildDashboardTab() {
        Tab tab = new Tab("📊 Dashboard");
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));

        // Statistics Cards
        HBox statsBox = new HBox(15);
        statsBox.setAlignment(Pos.CENTER_LEFT);

        lblTotalStudents = new Label("0");
        lblComparisons = new Label("0");
        lblFlagged = new Label("0");
        lblHighestMatch = new Label("0.0%");

        statsBox.getChildren().addAll(
                createStatCard("Students", lblTotalStudents, "#3498db"),
                createStatCard("Comparisons", lblComparisons, "#9b59b6"),
                createStatCard("Flagged", lblFlagged, "#e74c3c"),
                createStatCard("Highest Match", lblHighestMatch, "#f39c12")
        );

        // Chart Setup (Horizontal Bar Chart)
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Student Comparison");

        NumberAxis yAxis = new NumberAxis(0, 100, 10);
        yAxis.setLabel("Similarity (%)");

        top3Chart = new BarChart<>(xAxis, yAxis);


        top3Chart.setTitle("Top 5 Suspicious Similarities");
        top3Chart.setLegendVisible(false);
        top3Chart.setAnimated(false);

        top3Chart.setCategoryGap(10);

        layout.getChildren().addAll(statsBox, top3Chart);
        tab.setContent(layout);
        return tab;
    }

    private Tab buildComparisonsTab() {
        Tab tab = new Tab("🔍 All Comparisons");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        // Search Bar
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.getChildren().add(new Label("Search Student: "));
        txtSearch = new TextField();
        txtSearch.setPromptText("Enter name...");
        txtSearch.textProperty().addListener((obs, old, newVal) -> applyFilter(newVal));
        filterBox.getChildren().add(txtSearch);

        // Table
        tableAll = new TableView<>();
        tableAll.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        filteredMatches = new FilteredList<>(allMatches, p -> true);
        tableAll.setItems(filteredMatches);

        TableColumn<PlagiarismMatch, String> colA = new TableColumn<>("Student A");
        colA.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().studentA()));

        TableColumn<PlagiarismMatch, String> colB = new TableColumn<>("Student B");
        colB.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().studentB()));

        TableColumn<PlagiarismMatch, String> colSim = new TableColumn<>("Similarity");
        colSim.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.format("%.2f%%", data.getValue().similarity())));
        // Custom sort to sort numerically instead of string alphabetical
        colSim.setComparator((s1, s2) -> Double.valueOf(s1.replace("%", "")).compareTo(Double.valueOf(s2.replace("%", ""))));

        TableColumn<PlagiarismMatch, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                data.getValue().similarity() >= getThreshold() ? "Requires Review" : "Normal"
        ));

        tableAll.getColumns().addAll(colA, colB, colSim, colStatus);

        layout.getChildren().addAll(filterBox, tableAll);
        tab.setContent(layout);
        return tab;
    }

    private Tab buildSuspiciousTab() {
        Tab tab = new Tab("🚨 Disqualified / Review");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        tableSuspicious = new TableView<>();
        tableSuspicious.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<PlagiarismMatch, String> colSuspect = new TableColumn<>("Student (Suspect)");
        colSuspect.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                // Heuristic: Newer file time might indicate the copier
                data.getValue().timeA().compareTo(data.getValue().timeB()) > 0 ?
                        data.getValue().studentA() : data.getValue().studentB()
        ));

        TableColumn<PlagiarismMatch, String> colCompared = new TableColumn<>("Compared With");
        colCompared.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                data.getValue().timeA().compareTo(data.getValue().timeB()) > 0 ?
                        data.getValue().studentB() : data.getValue().studentA()
        ));

        TableColumn<PlagiarismMatch, String> colSim = new TableColumn<>("Similarity");
        colSim.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.format("%.2f%%", data.getValue().similarity())));

        tableSuspicious.getColumns().addAll(colSuspect, colCompared, colSim);

        layout.getChildren().addAll(new Label("Submissions crossing the similarity threshold:"), tableSuspicious);
        tab.setContent(layout);
        return tab;
    }

    private Tab buildReferenceTab() {
        Tab tab = new Tab("⭐ Reference Analysis");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        tableReference = new TableView<>(refMatches);
        tableReference.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ReferenceMatch, String> colStudent = new TableColumn<>("Student");
        colStudent.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().student()));

        TableColumn<ReferenceMatch, String> colSim = new TableColumn<>("Similarity to Solution");
        colSim.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.format("%.2f%%", data.getValue().similarity())));

        TableColumn<ReferenceMatch, String> colStatus = new TableColumn<>("Note");
        colStatus.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                "Baseline match (Not inherently plagiarism)"
        ));

        tableReference.getColumns().addAll(colStudent, colSim, colStatus);

        layout.getChildren().addAll(new Label("Similarity to official reference solution (if provided):"), tableReference);
        tab.setContent(layout);
        return tab;
    }

    private VBox createStatCard(String title, Label valueLabel, String color) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setPrefWidth(180);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3); " +
                "-fx-border-color: " + color + "; -fx-border-width: 4 0 0 0; -fx-border-radius: 8;");

        Label lblTitle = new Label(title);
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        lblTitle.setStyle("-fx-text-fill: #7f8c8d;");

        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        valueLabel.setStyle("-fx-text-fill: #2c3e50;");

        card.getChildren().addAll(lblTitle, valueLabel);
        return card;
    }

    // --- LOGIC ---

    private void runAnalysis() {
        if (selectedStudentFiles == null || selectedStudentFiles.size() < 2) {
            showAlert(Alert.AlertType.WARNING, "Not enough files", "Please select at least TWO student Java files to compare.");
            return;
        }

        double threshold = getThreshold();
        if (threshold < 0 || threshold > 100) {
            showAlert(Alert.AlertType.ERROR, "Invalid Threshold", "Threshold must be a number between 0 and 100.");
            return;
        }

        // Convert java.io.File to java.nio.file.Path
        List<Path> studentPaths = new java.util.ArrayList<>(selectedStudentFiles.stream().map(File::toPath).toList());

        // Safeguard: Remove reference solution from student pool if accidentally included
        if (selectedReferenceFile != null) {
            studentPaths.removeIf(p -> p.equals(selectedReferenceFile.toPath()));
        }

        // Run Engine
        List<PlagiarismMatch> results = LabPlagiarismDetector.scanStudents(studentPaths);
        allMatches.setAll(results);

        // Run Reference Scan if provided
        if (selectedReferenceFile != null) {
            List<ReferenceMatch> refRes = LabPlagiarismDetector.scanAgainstReference(studentPaths, selectedReferenceFile.toPath());
            refMatches.setAll(refRes);
        } else {
            refMatches.clear();
        }

        updateDashboard(threshold, studentPaths.size());
    }

    private void updateDashboard(double threshold, int totalStudents) {
        // Find flagged items
        List<PlagiarismMatch> flagged = allMatches.stream()
                .filter(m -> m.similarity() >= threshold)
                .toList();

        // Update Suspicious Table
        tableSuspicious.setItems(FXCollections.observableArrayList(flagged));

        // Refresh All Table to update "Status" column colors/text based on new threshold
        tableAll.refresh();

        // Update Stat Cards
        lblTotalStudents.setText(String.valueOf(totalStudents));
        lblComparisons.setText(String.valueOf(allMatches.size()));
        lblFlagged.setText(String.valueOf(flagged.size()));

        if (!allMatches.isEmpty()) {
            lblHighestMatch.setText(String.format("%.1f%%", allMatches.get(0).similarity()));
        } else {
            lblHighestMatch.setText("0.0%");
        }

        // Update Top 3 Chart
        top3Chart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 0; i < Math.min(5, allMatches.size()); i++) {
            PlagiarismMatch m = allMatches.get(i);
            String label = m.studentA() + " ↔ " + m.studentB();
            series.getData().add(new XYChart.Data<>(label, m.similarity()));
        }
        top3Chart.getData().add(series);
        top3Chart.applyCss();

        top3Chart.lookupAll(".chart-bar").forEach(bar -> {
            bar.setStyle("-fx-background-radius: 0;");
            bar.setScaleX(0.45);
        });
    }

    private void applyFilter(String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            filteredMatches.setPredicate(p -> true);
        } else {
            String lowerCaseFilter = filterText.toLowerCase();
            filteredMatches.setPredicate(p ->
                    p.studentA().toLowerCase().contains(lowerCaseFilter) ||
                            p.studentB().toLowerCase().contains(lowerCaseFilter)
            );
        }
    }

    private void resetApplication() {
        selectedStudentFiles = null;
        selectedReferenceFile = null;
        lblSelectedStudents.setText("0 files selected");
        lblSelectedReference.setText("None selected");
        txtThreshold.setText("70");
        txtSearch.clear();

        allMatches.clear();
        refMatches.clear();
        tableSuspicious.getItems().clear();
        top3Chart.getData().clear();

        lblTotalStudents.setText("0");
        lblComparisons.setText("0");
        lblFlagged.setText("0");
        lblHighestMatch.setText("0.0%");
    }

    private double getThreshold() {
        try {
            return Double.parseDouble(txtThreshold.getText().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}