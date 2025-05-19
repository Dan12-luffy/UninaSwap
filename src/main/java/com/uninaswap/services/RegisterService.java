package com.uninaswap.services;


import com.uninaswap.dao.UserDao;
import com.uninaswap.dao.UserDaoImpl;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import static com.uninaswap.utility.Sha256.hashPassword;

public class RegisterService {

    private final UserDao userDao;

    public RegisterService(){
        this.userDao = new UserDaoImpl();
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
