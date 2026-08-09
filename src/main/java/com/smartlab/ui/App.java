package com.smartlab.ui;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.DirectoryChooser;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.ListView;
import java.io.File;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;

public class App extends Application{

    public void start(Stage primaryStage){
        //title
        primaryStage.setTitle("My JavaFX Project");

        //label
        Label title = new Label("JavaFX Project Explorer");
        title.setId("title");

        Label folder = new Label("Select Folder:");
        folder.setId("folder");

        //Listview
        ListView<String> fileList = new ListView<>();
        fileList.getItems().add("Main.java");
        fileList.getItems().add("Student.java");
        fileList.getItems().add("Teacher.java");

        fileList.getSelectionModel().clearSelection();


        //Button
        Button openButton = new Button("Open Folder");
        openButton.setId("openButton");
        openButton.setDisable(true);
        fileList.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, newValue) -> {
                    openButton.setDisable(newValue == null);
                }
        );

        openButton.setOnAction(e -> {

            DirectoryChooser directoryChooser = new DirectoryChooser();

            directoryChooser.setTitle("Choose a Folder");

            File selectedFolder =
                    directoryChooser.showDialog(primaryStage);

            if (selectedFolder != null) {
                openButton.getStyleClass().add("selected");

                folder.setText(
                        "Selected Folder: " + selectedFolder.getAbsolutePath()
                );


                fileList.getItems().clear();


                File[] files = selectedFolder.listFiles();

                if (files != null) {

                    for (File file : files) {

                        if (file.isFile()) {

                            fileList.getItems().add(
                                    file.getName()
                            );
                        }
                    }
                }
            }
        });

        //exit
        Button exitButton = new Button("Exit");
        exitButton.setId("exitButton");

        exitButton.setOnAction(e -> {
            primaryStage.close();
        });

        HBox buttonBox = new HBox();
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        buttonBox.getChildren().addAll(
                openButton,
                spacer,
                exitButton
        );

        VBox topBox = new VBox(10);
        topBox.getChildren().addAll(
                title,
                folder
        );

        BorderPane layout = new BorderPane();
        layout.setTop(topBox);
        layout.setCenter(fileList);
        layout.setBottom(buttonBox);

        Scene scene = new Scene(layout, 600, 450);


        //CSS
        scene.getStylesheets().add(
                getClass()
                        .getResource("style.css")
                        .toExternalForm()
        );

        primaryStage.setScene(scene);
        primaryStage.show();


    }
    public static void main(String[] args){
        launch(args);
    }
}


