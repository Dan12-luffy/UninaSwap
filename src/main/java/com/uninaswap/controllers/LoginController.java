package com.uninaswap.controllers;

import com.google.common.hash.Hashing;
import com.uninaswap.utility.DatabaseUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static com.uninaswap.utility.Alert.showAlert;
import static com.uninaswap.utility.Sha256.hashPassword;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Hyperlink registerButton;

    @FXML
    public void initialize() {
        // Initialization logic here
    }

    private boolean authenticateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             stmt.setString(1, username);
             stmt.setString(2, hashPassword(password));
             try (ResultSet rs = stmt.executeQuery()) {
                 return rs.next();
             }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    private void onLoginButtonClicked(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Login Fallito", "Username e password non possono essere vuoti.");
            return;
        }
        if (authenticateUser(username, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Login Effettuato", "Benvenuto, " + username + "!");
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/mainInterface.fxml"));
                Parent root = loader.load();
                MainController mainController = loader.getController(); // This should NOT be null
                mainController.setUsername(username);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile aprire la schermata principale.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Fallito", "Username o password errati.");
        }
    }


    @FXML
    private void navigateToRegister(ActionEvent actionEvent){

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/RegisterInterface.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();
        }catch (Exception e){
            showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile accedere alla pagina di registrazione.");
        }
    }
}