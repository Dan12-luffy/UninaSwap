package com.uninaswap.services;

import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class ValidationService {
    private static final ValidationService instance = new ValidationService();

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
            ValidationService.getInstance().showRegistrationFieldsEmptyError();
            return false;
        }
        if(!isValidUsername(usernameField.getText())){
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Attenzione si accettano solo lettere,numeri e underscore");
            return false;
        }

        if(!isStrongPassword(passwordField.getText())){
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "La password deve contenere almeno 8 caratteri, una lettera maiuscola e un numero ");
            return false;
        }
        return isValidPassword(passwordField, confirmPasswordField);
    }

    private boolean isStrongPassword(String password){
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
        return password.matches(passwordRegex);

    }

    private boolean isValidUsername(String username){
        String usernameRegex = "^[a-zA-Z0-9_-]{3,16}$";
        return username.matches(usernameRegex);
    }

    private  boolean isValidPassword(PasswordField passwordField, PasswordField confirmPasswordField) {
        if(!passwordField.getText().equals(confirmPasswordField.getText())){
            ValidationService.getInstance().showPasswordMismatchError();
            return false;
        }
        return true;
    }

    public void showLoginSuccess(String username) {
        showAlert(Alert.AlertType.INFORMATION,"Login Effettuato", "Benvenuto, " + username + "!");
    }
    public void showLoginError() {
        showAlert(Alert.AlertType.ERROR,"Login Fallito", "Username o password errati.");
    }

    public void showRegistrationFieldsEmptyError(){
        showAlert(Alert.AlertType.ERROR, "Errore", "Compila tutti i campi.");
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
    public void showInvalidPriceError() {
        showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore", "Il prezzo deve essere un numero valido.");
    }
    public void showNewInsertionError() {
        showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore", "L'inserimento della nuova inserzione non è andato a buon fine.");
    }
    public void showNewInsertionMissingCampsError() {
        showAlert(Alert.AlertType.ERROR, "Errore di validazione", "Titolo e descrizione sono campi obbligatori.");
    }
    public void showPriceFormatError() {
        showAlert(Alert.AlertType.ERROR,"Errore di validazione", "Il formato del prezzo non è valido.");
    }
    public void showInvalidPriceRangeError() {
        showAlert(Alert.AlertType.ERROR, "Errore", "Il prezzo minimo non deve essere maggiore del prezzo massimo");
    }

    public void showAlert(javafx.scene.control.Alert.AlertType alertType, String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void showNewInsertionSuccess() {
        showAlert(Alert.AlertType.INFORMATION, "Inserzione salvata", "L'inserzione è stata salvata con successo!");
    }
    public void showWrongOfferError() {
        showAlert(Alert.AlertType.ERROR, "Errore", "Non puoi fare un'offerta su una tua inserzione.");
    }
    public void showOfferProposalSuccess() {
        showAlert(Alert.AlertType.INFORMATION, "Offerta Inviata", "La tua offerta è stata inviata con successo!");
    }
    public void showCancelExchangeMessage() {
        showAlert(Alert.AlertType.INFORMATION, "Scambio Annullato", "Lo scambio è stato annullato con successo, tornerai alla pagina principale.");
    }
    public void showLoadAllItemsError() {
        showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile caricare gli oggetti.");
    }

    public void showFailedToSetStatsError() {
        showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile impostare le statistiche.");    
    }

    public void showInsertionAnnulledSuccess() {
        showAlert(Alert.AlertType.INFORMATION, "Successo", "Offerta annullata con successo.");
    }

    public void showSqlError(SQLException e) {
        showAlert(Alert.AlertType.ERROR, "Errore SQL", "Si è verificato un errore durante l'esecuzione della query: " + e.getMessage());
    }

    public void showUpdateFacltySuccess() {
        showAlert(Alert.AlertType.INFORMATION, "Successo", "Facoltà aggiornata con successo.");
    }
    public void showUpdateFacultyError() {
        showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile aggiornare la facoltà.");
    }
}