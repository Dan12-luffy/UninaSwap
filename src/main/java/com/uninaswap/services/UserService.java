package com.uninaswap.services;

import com.uninaswap.dao.UserDao;
import com.uninaswap.dao.UserDaoImpl;
import com.uninaswap.model.User;
import com.uninaswap.utility.Sha256;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

import static com.uninaswap.utility.Sha256.hashPassword;

public class UserService {
    private static final UserService instance = new UserService();
    private final UserDao userDao;

    private UserService() {
        this.userDao = new UserDaoImpl();
    }

    public static UserService getInstance() {
        return instance;
    }

    public boolean registerUser(User user) {
        if (user.getUsername() == null ||  user.getPassword() == null) {
            return false;
        }

        if (userDao.usernameAlreadyExists(user.getUsername())) {
            return false;
        }

        user.setPassword(Sha256.hashPassword(user.getPassword()));

        userDao.insertUser(user);
        return true;
    }

    public void authenticateUser(String username, String password) {
        User user = userDao.findUserByUsername(username);
        if (user != null && authenticate(username, password)) {
            UserSession.getInstance().login(user);
            ValidationService.getInstance().showLoginSuccess(user.getUsername());
            return;
        }
        ValidationService.getInstance().showLoginError();
    }

    public boolean updateUsername(int userId, String newUsername) {
        try {
            userDao.updateUsername(userId, newUsername);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserById(int userId) {
        return userDao.findUserFromID(userId);
    }

    public String getUserFullName(int userId) {
        return userDao.findFullNameFromID(userId);
    }

    private boolean authenticate(String username, String password) {
        User user = userDao.authenticateUser(username, hashPassword(password));
        return user != null;
    }

    public boolean processRegistrationForm(@NotNull TextField nameField, @NotNull TextField surnameField, @NotNull ComboBox<String> facultyComboBox, @NotNull TextField usernameField, @NotNull PasswordField passwordField, @NotNull PasswordField confirmPasswordField) {
        if (!ValidationService.getInstance().validateInputFromRegistration(nameField, surnameField, usernameField,
                passwordField, confirmPasswordField)) {
            ValidationService.getInstance().showRegistrationError();
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
            return true;
        }
        return false;
    }

    private boolean registerUser(String name, String surname, String faculty, String username, String password) {
        if (userDao.usernameAlreadyExists(username)) {
            ValidationService.getInstance().showUsernameAlreadyExistsError();
            return false;
        }

        try {
            User user = new User();
            user.setFirst_name(name);
            user.setLast_name(surname);
            user.setFaculty(faculty);
            user.setUsername(username);
            user.setPassword(hashPassword(password));

            return userDao.insertUser(user);
        } catch (Exception e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore",
                    "Errore durante la registrazione: " + e.getMessage());
            return false;
        }
    }
}
