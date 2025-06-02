package com.uninaswap.controllers;

import com.uninaswap.model.User;
import com.uninaswap.services.NavigationService;
import com.uninaswap.services.UserService;
import com.uninaswap.services.ValidationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink registerButton;

    private final UserService userService = UserService.getInstance();

    @FXML
    public void initialize() {
        // Initialization logic here
    }

    @FXML
    private void onLoginButtonClicked(ActionEvent event) {
        String username = this.usernameField.getText();
        String password = this.passwordField.getText();

        if (!ValidationService.getInstance().areCredentialsValid(username, password)) {
            return;
        }
        userService.authenticateUser(username, password);
        NavigationService.getInstance().navigateToMainView(event);
    }

    @FXML
    private void navigateToRegister(ActionEvent event){
        NavigationService.getInstance().navigateToRegisterView(event);
    }
}

