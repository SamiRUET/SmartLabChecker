package com.smartlab.ui;


import javafx.beans.property.SimpleStringProperty;


public class ASTRow {


    private final SimpleStringProperty type;

    private final SimpleStringProperty name;




    public ASTRow(
            String type,
            String name
    ){

        this.type =
                new SimpleStringProperty(type);


        this.name =
                new SimpleStringProperty(name);

    }




    public SimpleStringProperty typeProperty(){

        return type;

    }



    public SimpleStringProperty nameProperty(){

        return name;

    }

}