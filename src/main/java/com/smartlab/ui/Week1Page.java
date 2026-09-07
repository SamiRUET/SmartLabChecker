package com.smartlab.ui;


import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;


public class Week1Page {


    private BorderPane layout;


    public Week1Page(Stage primaryStage) {

        // Title
        Label title =
                new Label("Smart Lab Examiner");

        title.setId("title");



        // Open Project Button

        Button openProjectButton =
                new Button("Open Project");

        openProjectButton.setId("openButton");



        // Project Information
        Label projectLabel =
                new Label("Selected Project: None");

        projectLabel.setId("project");



        Label countLabel =
                new Label("Total Java Files: 0");

        countLabel.setId("count");

        // Java File List

        ListView<String> fileList =
                new ListView<>();


        fileList.setPrefHeight(350);



        // Open File Button

        Button openFileButton =
                new Button("Open File");


        openFileButton.setId("openFileButton");



        // Initially disabled

        openFileButton.setDisable(true);




        // Enable after selecting java file

        fileList.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable, oldValue, newValue) -> {


                            if(newValue != null){

                                openFileButton
                                        .setDisable(false);

                            }
                            else{

                                openFileButton
                                        .setDisable(true);

                            }


                        }
                );




        // Open Project Action



        openProjectButton.setOnAction(e -> {


            DirectoryChooser chooser =
                    new DirectoryChooser();


            chooser.setTitle(
                    "Select Java Project Folder"
            );



            File selectedFolder =
                    chooser.showDialog(primaryStage);




            if(selectedFolder != null){


                projectLabel.setText(
                        "Selected Project: "
                                + selectedFolder.getName()
                );



                fileList.getItems()
                        .clear();




                findJavaFiles(
                        selectedFolder,
                        fileList
                );




                countLabel.setText(
                        "Total Java Files: "
                                + fileList.getItems().size()
                );



            }


        });




        // Open File Action


        openFileButton.setOnAction(e -> {


            String selectedFile =
                    fileList.getSelectionModel()
                            .getSelectedItem();



            if(selectedFile != null){



                Week2Dashboard dashboard =
                        new Week2Dashboard();



                javafx.scene.Scene scene =
                        new javafx.scene.Scene(
                                dashboard.getView(),
                                900,
                                600
                        );



                scene.getStylesheets()
                        .add(
                                getClass()
                                        .getResource("/style.css")
                                        .toExternalForm()
                        );



                primaryStage.setScene(scene);


                primaryStage.show();


            }


        });





        // Exit Button


        Button exitButton =
                new Button("Exit");


        exitButton.setId("exitButton");



        exitButton.setOnAction(e -> {

            primaryStage.close();

        });






        // Top Layout


        VBox topBox =
                new VBox(15);


        topBox.setAlignment(
                Pos.CENTER
        );


        topBox.setPadding(
                new Insets(25)
        );



        topBox.getChildren()
                .addAll(
                        title,
                        openProjectButton,
                        projectLabel,
                        countLabel
                );






        // Bottom Layout



        HBox bottomBox =
                new HBox();



        bottomBox.setPadding(
                new Insets(15)
        );



        Region spacer =
                new Region();



        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );



        bottomBox.getChildren()
                .addAll(
                        openFileButton,
                        spacer,
                        exitButton
                );







        // Main Layout



        layout =
                new BorderPane();



        layout.setTop(topBox);


        layout.setCenter(fileList);


        layout.setBottom(bottomBox);



        layout.setPadding(
                new Insets(10)
        );


    }




    // Recursive Java File Search



    private void findJavaFiles(
            File folder,
            ListView<String> list){



        File[] files =
                folder.listFiles();



        if(files == null)
            return;




        for(File file : files){


            if(file.isDirectory()){


                findJavaFiles(
                        file,
                        list
                );


            }
            else if(
                    file.getName()
                            .endsWith(".java")
            ){


                list.getItems()
                        .add(
                                "✓ "
                                        + file.getName()
                        );


            }


        }


    }







    public BorderPane getView(){

        return layout;

    }


}