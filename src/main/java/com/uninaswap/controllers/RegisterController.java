package com.uninaswap.controllers;

import com.uninaswap.model.Faculty;
import com.uninaswap.services.AuthenticationService;
import com.uninaswap.services.NavigationService;
import com.uninaswap.services.ValidationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import com.uninaswap.utility.DatabaseUtil;    // non vede questo
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private ComboBox<String> facultyComboBox;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Hyperlink loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private VBox passwordRequirements;

    @FXML
    private Label uppercaseCheck;

    @FXML
    private Label specialCharCheck;

    @FXML
    private Label numberCheck;

    @FXML
    private Label lengthCheck;

    @FXML
    public void initialize(){
        for (Faculty faculty : Faculty.values()) {
            facultyComboBox.getItems().add(faculty.getFacultyName());
        }
    }

    @FXML
    public void onRegisterButtonClicked(ActionEvent actionEvent) {
        boolean registrationSuccessful = AuthenticationService.getInstance().processRegistrationForm(nameField, surnameField, facultyComboBox, usernameField, passwordField, confirmPasswordField);

        if (registrationSuccessful) {
            NavigationService.getInstance().navigateToLoginView(actionEvent);
        }
    }
    @FXML
    private void navigateToLogin(ActionEvent event) {
        NavigationService.getInstance().navigateToLoginView(event);
    }
}
