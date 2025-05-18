package com.uninaswap.services;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class NavigationService {
    private static final NavigationService instance = new NavigationService();

    private NavigationService(){
    }
    public static NavigationService getInstance() {
        return instance;
    }

    public void navigateToLoginView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/loginInterface.fxml"));
            Parent root = loader.load();
            setScene(event, root);

        } catch (IOException e) {
            ValidationService.getInstance().showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore",
                    "Impossibile aprire la schermata di login.");
        }
    }

    public void navigateToRegisterView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/registerInterface.fxml"));
            Parent root = loader.load();
            setScene(event, root);
        } catch (IOException e) {
            ValidationService.getInstance().showFailedToOpenPageError();
        }
    }

    public void navigateToMainView(ActionEvent event, String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/mainInterface.fxml"));
            Parent root = loader.load();
            setScene(event, root);

        } catch (IOException e) {
            ValidationService.getInstance().showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore",
                    "Impossibile aprire la schermata principale.");
        }
    }
    private void setScene(ActionEvent event, Parent root) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.show();
    }
}