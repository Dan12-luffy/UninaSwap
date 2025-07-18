package com.uninaswap.controllers;

import com.uninaswap.services.NavigationService;
import com.uninaswap.services.UserService;
import com.uninaswap.services.ValidationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private final UserService userService = UserService.getInstance();

    @FXML
    public void initialize() {}

    @FXML
    private void onLoginButtonClicked(ActionEvent event) {
        String username = this.usernameField.getText();
        String password = this.passwordField.getText();

        if (!ValidationService.getInstance().areCredentialsValid(username, password))
            return;
        if(!userService.authenticateUser(username, password))
            return;
        NavigationService.getInstance().navigateToMainView(event);
    }

    @FXML
    private void navigateToRegister(ActionEvent event){
        NavigationService.getInstance().navigateToRegisterView(event);
    }
}

