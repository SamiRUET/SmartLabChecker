package com.smartlab.ui;


import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;



public class Week2Dashboard {


    private BorderPane layout;



    public Week2Dashboard(){



        // =========================
        // Title
        // =========================


        Label title =
                new Label(
                        "Smart Lab Examiner - Dashboard"
                );


        title.setId("title");


        title.setMaxWidth(
                Double.MAX_VALUE
        );


        title.setAlignment(
                Pos.CENTER
        );






        // =========================
        // Selected File
        // =========================


        Label selectedFile =
                new Label(
                        "Selected file : Student.java"
                );


        selectedFile.setId("project");


        selectedFile.setMaxWidth(
                Double.MAX_VALUE
        );


        selectedFile.setAlignment(
                Pos.CENTER_LEFT
        );







        // =========================
        // Metrics
        // =========================


        Label classes =
                new Label(
                        "Classes: 1"
                );


        Label methods =
                new Label(
                        "Methods: 4"
                );


        Label variables =
                new Label(
                        "Variables: 6"
                );


        Label imports =
                new Label(
                        "Imports: 2"
                );



        classes.setId("count");
        methods.setId("count");
        variables.setId("count");
        imports.setId("count");





        HBox metricsBox =
                new HBox(40);



        metricsBox.setAlignment(
                Pos.CENTER_LEFT
        );


        metricsBox.setPadding(
                new Insets(
                        10,
                        0,
                        10,
                        0
                )
        );



        metricsBox.getChildren()
                .addAll(
                        classes,
                        methods,
                        variables,
                        imports
                );









        // =========================
        // AST Table
        // =========================


        TableView<ASTRow> table =
                new TableView<>();



        TableColumn<ASTRow,String> typeColumn =
                new TableColumn<>(
                        "Type"
                );


        typeColumn.setCellValueFactory(
                data ->
                        data.getValue()
                                .typeProperty()
        );




        TableColumn<ASTRow,String> nameColumn =
                new TableColumn<>(
                        "Name"
                );


        nameColumn.setCellValueFactory(
                data ->
                        data.getValue()
                                .nameProperty()
        );



        table.getColumns()
                .addAll(
                        typeColumn,
                        nameColumn
                );



        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );



        table.setPrefHeight(
                350
        );

        // Dummy AST Data


        table.getItems()
                .addAll(

                        new ASTRow(
                                "Class",
                                "Student"
                        ),

                        new ASTRow(
                                "Method",
                                "getName"
                        ),

                        new ASTRow(
                                "Method",
                                "calculateGrade"
                        ),

                        new ASTRow(
                                "Variable",
                                "studentId"
                        ),

                        new ASTRow(
                                "Import",
                                "java.util.List"
                        )

                );









        // =========================
        // Top Layout
        // =========================


        VBox top =
                new VBox();



        top.setPadding(
                new Insets(
                        25,
                        10,
                        10,
                        10
                )
        );



        // title spacing

        VBox.setMargin(
                title,
                new Insets(
                        0,
                        0,
                        25,
                        0
                )
        );



        // selected file spacing

        VBox.setMargin(
                selectedFile,
                new Insets(
                        20,
                        0,
                        5,
                        0
                )
        );



        top.getChildren()
                .addAll(
                        title,
                        selectedFile,
                        metricsBox
                );









        // =========================
        // Main Layout
        // =========================


        layout =
                new BorderPane();



        layout.setTop(
                top
        );


        layout.setCenter(
                table
        );


        layout.setPadding(
                new Insets(10)
        );



    }





    public BorderPane getView(){

        return layout;

    }


}