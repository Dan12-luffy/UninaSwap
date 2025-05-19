package com.uninaswap.controllers;

import com.uninaswap.services.LoginService;
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

    private final LoginService loginService = new LoginService();

    @FXML
    public void initialize() {
        // Initialization logic here
    }

    @FXML
    private void onLoginButtonClicked(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (!ValidationService.getInstance().areCredentialsValid(username, password)) {
            return;
        }
        if (loginService.authenticateUser(username, password)) {
            ValidationService.getInstance().showLoginSuccess(username);
            NavigationService.getInstance().navigateToMainView(event);
        } else {
            ValidationService.getInstance().showLoginError();
        }
    }

    @FXML
    private void navigateToRegister(ActionEvent event){
            NavigationService.getInstance().navigateToRegisterView(event);
    }
}