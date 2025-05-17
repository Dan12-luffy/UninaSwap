package com.uninaswap.controllers;

import com.uninaswap.services.AuthenticationService;
import com.uninaswap.services.NavigationService;
import com.uninaswap.services.ValidationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;


public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Hyperlink registerButton;


    @FXML
    public void initialize() {
        // Initialization logic here
    }


    @FXML
    private void onLoginButtonClicked(ActionEvent event) { // TODO fare in modo che si apra la main interface relativa all'utente, aggiornare NavigationService
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (!ValidationService.getInstance().areCredentialsValid(username, password)){
            return;
        }
        if (AuthenticationService.getInstance().authenticateUser(username, password)) {
            ValidationService.getInstance().showLoginSuccess(username);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/mainInterface.fxml"));
                Parent root = loader.load();
                MainController mainController = loader.getController(); // This should NOT be null
                mainController.setUsername(username);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
                ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile aprire la schermata principale.");
            }
        } else {
            ValidationService.getInstance().showLoginError();
        }
    }

    @FXML
    private void navigateToRegister(ActionEvent event){
            NavigationService.getInstance().navigateToRegisterView(event);
    }
}