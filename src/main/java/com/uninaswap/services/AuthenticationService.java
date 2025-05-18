package com.uninaswap.services;

import com.uninaswap.dao.UserDao;
import com.uninaswap.dao.UserDaoImpl;
import com.uninaswap.utility.DatabaseUtil;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static com.uninaswap.utility.Sha256.hashPassword;

public class AuthenticationService {
    private static final AuthenticationService instance = new AuthenticationService();
    private final UserDao userDao = new UserDaoImpl();

    private AuthenticationService() {
        // Singleton
    }
    public static AuthenticationService getInstance() {
        return instance;
    }

    public boolean authenticateUser(String username, String password) {
        return userDao.authenticate(username, hashPassword(password)) != null;
    }

    public boolean registerUser(String name, String surname, String faculty, String username, String password) {
        String sql = "INSERT INTO users (name, surname, faculty, username, password) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             stmt.setString(1, name);
             stmt.setString(2, surname);
             stmt.setString(3, faculty);
             stmt.setString(4, username);
             stmt.setString(5, hashPassword(password));
             int rowInserted = stmt.executeUpdate();
             return rowInserted > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isStrongPassword(String password){
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
        return password.matches(passwordRegex);

    }

    public boolean isValidUsername(String username){
        String usernameRegex = "^[a-zA-Z0-9_-]{3,16}$";
        return username.matches(usernameRegex);
    }

    public boolean isValidPassword(PasswordField passwordField, PasswordField confirmPasswordField) {
        if(!passwordField.getText().equals(confirmPasswordField.getText())){
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Le Password non coincidono.");
            return false;
        }
        return true;
    }

}