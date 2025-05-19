package com.uninaswap.services;

import javafx.scene.control.Alert;
import com.uninaswap.dao.UserDao;
import com.uninaswap.dao.UserDaoImpl;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;

import static com.uninaswap.utility.Sha256.hashPassword;

public class RegisterService {

    private final UserDao userDao;

    public RegisterService(){
        this.userDao = new UserDaoImpl();
    }

    private boolean registerUser(String name, String surname, String faculty, String username, String password) {
        if(userDao.usernameExists(username)) {
            ValidationService.getInstance().showUsernameAlreadyExistsError();
            return false;
        }
        return userDao.create(name, surname, faculty, username, hashPassword(password));
    }

    public boolean processRegistrationForm(TextField nameField, TextField surnameField, ComboBox<String> facultyComboBox, TextField usernameField, PasswordField passwordField, PasswordField confirmPasswordField) {
        if (!validateInputFromRegistration(nameField, surnameField, usernameField, passwordField, confirmPasswordField)) {
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

    private  boolean isValidPassword(PasswordField passwordField, PasswordField confirmPasswordField) {
        if(!passwordField.getText().equals(confirmPasswordField.getText())){
            ValidationService.getInstance().showPasswordMismatchError();
            return false;
        }
        return true;
    }

}
