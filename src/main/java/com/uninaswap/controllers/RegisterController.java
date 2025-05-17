package com.uninaswap.controllers;

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
import javafx.stage.Stage;
import static com.uninaswap.utility.Alert.showAlert;
import static com.uninaswap.utility.Sha256.hashPassword;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


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
    public void initialize(){
        facultyComboBox.getItems().addAll("Informatica", "Scienze", "Matematica", "Economia", "Arte", "Medicina", "Biologia", "Filosofia", "Geografia", "Psychologia", "Chimica", "Astronomia", "Turismo", "Linguistica", "Musica");
    }

    public void onRegisterButtonClicked(ActionEvent actionEvent) {
        if (!ValidationService.getInstance().validateInputFromRegistration(nameField, surnameField, usernameField, passwordField, confirmPasswordField)) {
            return;
        }
        if (!AuthenticationService.getInstance().isValidPassword(passwordField, confirmPasswordField)) {
            return;
        }
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        String faculty = facultyComboBox.getValue();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        boolean registrationSuccessful = AuthenticationService.getInstance().registerUser(name, surname, faculty, username, password);

        if (registrationSuccessful) {
            ValidationService.getInstance().showRegistrationSuccess();
            navigateToLogin(actionEvent);
        } else {
            ValidationService.getInstance().showRegistrationError();
        }
    }

    @FXML
    private void navigateToLogin(ActionEvent actionEvent){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/loginInterface.fxml"));
                Parent loginRoot = loader.load();

                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

                stage.setScene(new Scene(loginRoot));
                stage.show();
            } catch (IOException e) {
                ValidationService.getInstance().showFailedToOpenPageError();
            }
    }
}
