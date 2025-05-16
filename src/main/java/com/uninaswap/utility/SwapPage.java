package com.uninaswap.utility;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SwapPage {
    public static FXMLLoader swapPage(ActionEvent event, String page) throws IOException {
        FXMLLoader loader = new FXMLLoader(SwapPage.class.getResource(page));
        Parent root = loader.load();
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        return loader;
    }
}