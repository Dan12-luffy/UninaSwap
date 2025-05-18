package com.uninaswap.logic;

import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage){
        try{

            Image logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/uninaswap/images/uninaswap_logo.png")));
            primaryStage.getIcons().add(logo);
            primaryStage.show();
            primaryStage.centerOnScreen();
            primaryStage.setResizable(false);
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/uninaswap/gui/loginInterface.fxml")));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("UninaSwap");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}