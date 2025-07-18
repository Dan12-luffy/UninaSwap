package com.uninaswap.controllers;

import com.uninaswap.model.Faculty;
import com.uninaswap.services.NavigationService;
import com.uninaswap.services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
        int maxLength = 30;
        this.nameField.setTextFormatter(createTextFormatter(maxLength));
        this.surnameField.setTextFormatter(createTextFormatter(maxLength));
        this.passwordField.setTextFormatter(createTextFormatter(maxLength));
        this.confirmPasswordField.setTextFormatter(createTextFormatter(maxLength));
        this.usernameField.setTextFormatter(createTextFormatter(maxLength));
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

    private TextFormatter<String> createTextFormatter(int maxLength) {
        return new TextFormatter<>(change -> {
            if (change.getControlNewText().length() <= maxLength) {
                return change;
            }
            return null;
        });
    }
}
