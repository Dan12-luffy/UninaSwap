package com.uninaswap.services;

import com.uninaswap.dao.UserDao;
import com.uninaswap.dao.UserDaoImpl;

import com.uninaswap.model.Faculty;
import com.uninaswap.model.User;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.w3c.dom.Text;

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
        User user = userDao.authenticate(username, hashPassword(password));
        if (user != null) {
            UserSession.getInstance().login(user);
            return true;
        }
        return false;
    }

    public boolean registerUser(String name, String surname, String faculty, String username, String password) {
        if(userDao.usernameExists(username)) {
            ValidationService.getInstance().showUsernameAlreadyExistsError();
            return false;
        }
        return userDao.create(name, surname, faculty, username, hashPassword(password));
    }

    public boolean processRegistrationForm(TextField nameField, TextField surnameField, ComboBox<String> facultyComboBox, TextField usernameField, PasswordField passwordField, PasswordField confirmPasswordField) {
        if (!ValidationService.getInstance().validateInputFromRegistration(nameField, surnameField, usernameField, passwordField, confirmPasswordField)) {
            return false;
        }
        if (!isValidPassword(passwordField, confirmPasswordField)) {
            return false;
        }

        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String faculty = facultyComboBox.getValue();

        boolean success = registerUser(name, surname, faculty, username, password);

        if (success) {
            ValidationService.getInstance().showRegistrationSuccess();
        } else {
            ValidationService.getInstance().showRegistrationError();
        }

        return success;
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
            ValidationService.getInstance().showPasswordMismatchError();
            return false;
        }
        return true;
    }
}