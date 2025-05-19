package com.uninaswap.services;

import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;

public class ValidationService {
    private static final ValidationService instance = new ValidationService();
    private final RegisterService registerService = new RegisterService();
    private ValidationService() {
        // Singleton
    }

    public static ValidationService getInstance() {
        return instance;
    }

    public boolean areCredentialsValid(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            showEmptyUsernameError();
            return false;
        }
        if (password == null || password.isEmpty()) {
            showEmptyPasswordError();
            return false;
        }
        return true;
    }
    public boolean validateInputFromRegistration(@NotNull TextField nameField, TextField surnameField, TextField usernameField, PasswordField passwordField, PasswordField confirmPasswordField){ //TODO forse si potrebbero implementare delle eccezioni
        if(nameField.getText().trim().isEmpty() ||
                surnameField.getText().trim().isEmpty() ||
                usernameField.getText().trim().isEmpty()||
                passwordField.getText().isEmpty() ||
                confirmPasswordField.getText().isEmpty()){
            ValidationService.instance.showRegistrationFieldsEmptyError();
            return false;
        }
        if(!registerService.isValidUsername(usernameField.getText())){
            showAlert(Alert.AlertType.ERROR, "Errore", "Attenzione si accettano solo lettere,numeri e underscore");
            return false;
        }

        if(!registerService.isStrongPassword(passwordField.getText())){
            showAlert(Alert.AlertType.ERROR, "Errore", "La password deve contenere almeno 8 caratteri, una lettera maiuscola e un numero ");
            return false;
        }
        return true;
    }

    public void showLoginSuccess(String username) {
        showAlert(javafx.scene.control.Alert.AlertType.INFORMATION,"Login Effettuato", "Benvenuto, " + username + "!");
    }
    public void showLoginError() {
        showAlert(javafx.scene.control.Alert.AlertType.ERROR,"Login Fallito", "Username o password errati.");
    }
    public void showRegistrationFieldsEmptyError(){
        showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore", "Compila tutti i campi.");
    }
    public void showRegistrationError() {
        showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Registrazione Fallita", "Impossibile completare la registrazione.");
    }
    public void showRegistrationSuccess() {
        showAlert(javafx.scene.control.Alert.AlertType.INFORMATION, "Registrazione Effettuata", "Registrazione avvenuta con successo.");
    }
    public void showFailedToOpenPageError() {
        showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore", "Impossibile aprire la pagina.");
    }
    public void showEmptyUsernameError() {
        showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Dati Mancanti", "Il campo username non può essere vuoto.");
    }
    public void showEmptyPasswordError() {
        showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Dati Mancanti", "Il campo password non può essere vuoto.");
    }
    public void showUsernameAlreadyExistsError() {
        showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore", "Nome utente già esistente.");
    }
    public void showPasswordMismatchError() {
        showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore", "Le password non coincidono.");
    }
    public void showLogoutSuccess() {
        showAlert(javafx.scene.control.Alert.AlertType.INFORMATION, "Logout", "Logout effettuato con successo.");
    }
    public void showFailedToOpenLoginPageError() {
        showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore", "Impossibile aprire la pagina di login.");
    }

    public void showAlert(javafx.scene.control.Alert.AlertType alertType, String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}