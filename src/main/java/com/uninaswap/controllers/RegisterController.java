package com.uninaswap.controllers;

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
    public void inizialize(){
        //Inizializzazione della registrazione
    }

    public void onRegisterButtonClicked(ActionEvent actionEvent){

        if(!validateInput()){
            return;
        }
        if(!validatePassword()){
            return;
        }

        boolean registrationSuccessful = registerUser();

        if(registrationSuccessful){
            navigateToLogin(actionEvent);
        }
    }

    private boolean validateInput(){
        if(nameField.getText().trim().isEmpty() ||
            surnameField.getText().trim().isEmpty() ||
            usernameField.getText().trim().isEmpty()||
            passwordField.getText().isEmpty() ||
            confirmPasswordField.getText().isEmpty()){

            showAlert(Alert.AlertType.ERROR, "Errore", "Compila tutti i campi.");
            return false;
        }

        if(!isValidUsername(usernameField.getText())){
            showAlert(Alert.AlertType.ERROR, "Errore", "Attenzione si accettano solo lettere,numeri e underscore");
            return false;
        }

        if(!isStrongPassword(passwordField.getText())){
            showAlert(Alert.AlertType.ERROR, "Errore", "La password deve contenere almeno 8 caratteri, una lettera maiuscola e un numero ");
            return false;
        }

        return true;

    }

    private boolean isStrongPassword(String password){

        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
        return password.matches(passwordRegex);

    }

    private boolean isValidUsername(String username){
        String usernameRegex = "^[a-zA-Z0-9_-]{3,16}$";
        return username.matches(usernameRegex);
    }

    private boolean validatePassword(){

        if(!passwordField.getText().equals(confirmPasswordField.getText())){
            showAlert(Alert.AlertType.ERROR, "Errore", "Le Password non coincidono.");
            return false;
        }
        return true;
    }


    @FXML
    private boolean registerUser(){

        String name = nameField.getText();
        String surname = surnameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        String sql = "INSERT INTO users (name, surname, username, password) VALUES (?, ?, ?, ?)";
        try{Connection connection = DatabaseUtil.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, surname);
            stmt.setString(3, username);
            stmt.setString(4, hashPassword(password));

            int rowInserted = stmt.executeUpdate();

            if(rowInserted > 0){
                showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "Registrazione dell' utente avvenuta con successo.");
                return true;
            }else{
                showAlert(Alert.AlertType.ERROR, "Errore", "Registrazione Fallita");
                return false;
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Errore Database");
            return false;
        }

    }
    @FXML
    private void navigateToLogin(ActionEvent actionEvent){

            try {
                // Carica la vista di login
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/loginInterface.fxml"));
                Parent loginRoot = loader.load();

                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

                stage.setScene(new Scene(loginRoot));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Errore","Impossibile aprire la pagina");
            }

    }
}
