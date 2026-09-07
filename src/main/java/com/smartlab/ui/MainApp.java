package com.smartlab.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainApp extends Application {


    @Override
    public void start(Stage stage) {


        // Open Week 1 Layout
        Week1Page week1 = new Week1Page(stage);


        // Create Scene
        Scene scene = new Scene(
                week1.getView(),
                900,
                600
        );


        // Load CSS
        scene.getStylesheets()
                .add(
                        getClass()
                                .getResource("/style.css")
                                .toExternalForm()
                );


        // Window settings
        stage.setTitle(
                "Smart Lab Examiner"
        );


        stage.setScene(scene);


        stage.show();

    }



    public static void main(String[] args) {

        launch(args);

    }

}