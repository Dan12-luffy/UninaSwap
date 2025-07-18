package com.uninaswap.controllers;

import com.uninaswap.model.Faculty;
import com.uninaswap.model.User;
import com.uninaswap.services.NavigationService;
import com.uninaswap.services.UserService;
import com.uninaswap.services.ValidationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField surnameField;
    @FXML private ComboBox<String> facultyComboBox;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    private final UserService userService = UserService.getInstance();

    @FXML
    public void initialize(){
        for (Faculty faculty : Faculty.values()) {
            this.facultyComboBox.getItems().add(faculty.getFacultyName());
        }
    }

    @FXML
    public void onRegisterButtonClicked(ActionEvent actionEvent) {
        if(userService.processRegistrationForm(nameField, surnameField, facultyComboBox, usernameField, passwordField, confirmPasswordField))
            navigateToLogin(actionEvent);
    }

    @FXML
    private void navigateToLogin(ActionEvent event) {
        NavigationService.getInstance().navigateToLoginView(event);
    }
}
