package com.uninaswap.services;

import com.uninaswap.dao.UserDao;
import com.uninaswap.dao.UserDaoImpl;
import com.uninaswap.model.User;
import com.uninaswap.utility.Sha256;
import javafx.scene.control.Alert;


import java.sql.SQLException;


public class ProfileSecurityService {

    private static final ProfileSecurityService instance = new ProfileSecurityService();
    private final UserDao userDao;


    private ProfileSecurityService() {
        this.userDao = new  UserDaoImpl();
    }

    public static ProfileSecurityService getInstance() {
        return instance;
    }

    public boolean changePassword(int userId, String currentPassword, String newPassword){

        try{
            User user = UserSession.getInstance().getCurrentUser();
            if(user == null){
                ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "utente non trovato");
                return false;
            }
            String hashedCurrentPassword = Sha256.hashPassword(currentPassword);
            if(!user.getPassword().equals(hashedCurrentPassword)){
                ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "La password attuale non è corretta");
                return false;
            }
            if(!isValidPassword(newPassword)){
                ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "La nuova password non è valida");
                return false;
            }
            String hashedNewPassword = Sha256.hashPassword(newPassword);
            boolean success = userDao.updatePassword(userId, hashedNewPassword);

            if(success){
                ValidationService.getInstance().showAlert(Alert.AlertType.INFORMATION, "Successo", "Password cambiata con successo");
            }else{
                ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile cambiare la password");
            }
            return success;

        }catch(Exception e){
            e.printStackTrace();
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Si è verificato un errore durante il cambio della password");
            return false;
        }

    }
    public boolean changeUsername(int userId, String newUsername){
        try{
            User user = UserSession.getInstance().getCurrentUser();
            if(user == null){
                ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Utente non trovato");
                return false;
            }
            if(!isValidUsername(newUsername)){
                ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Il nuovo username non è valido");
                return false;
            }
            if(userDao.usernameExists(newUsername)){
                ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Username già in uso");
                return false;
            }
            User currentUser = userDao.getUserFromID(userId);
            if(currentUser != null && currentUser.getUsername().equals(newUsername)){
                ValidationService.getInstance().showAlert(Alert.AlertType.INFORMATION, "Informazione", "Il nuovo username è lo stesso del precedente");
                return false;
            }

            boolean success = userDao.updateUsername(userId, newUsername);
            if(success) {
                ValidationService.getInstance().showAlert(Alert.AlertType.INFORMATION, "Successo", "Username cambiato con successo");
            }else{
                ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile cambiare lo username");
            }
            return success;
        }catch(Exception e){
            e.printStackTrace();
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Si è verificato un errore durante il cambio dello username");
            return false;
        }

    }

    public boolean isValidUsername(String username){

        if(username == null || username.isEmpty()) return false;
        if(username.length() < 5 || username.length() > 20) return false;
        if(!username.matches("^[a-zA-Z0-9_]+$")) return false; // Controlla che contenga solo lettere, numeri e underscore
        return true;
    }

    public boolean isValidPassword(String password) {
        if(password == null || password.isEmpty()) {
            System.out.println("La password non può essere vuota");
            return false;
        }
        if(password.length() < 8 || password.length() > 20) {
            System.out.println("La password deve essere tra 8 e 20 caratteri");
            return false;
        }
        if(!password.matches(".*[a-z].*")) {
            System.out.println("La password deve contenere almeno una lettera minuscola");
            return false;
        }
        if(!password.matches(".*[A-Z].*")) {
            System.out.println("La password deve contenere almeno una lettera maiuscola");
            return false;
        }
        if(!password.matches(".*\\d.*")) {
            System.out.println("La password deve contenere almeno un numero");
            return false;
        }
        if(!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            System.out.println("La password deve contenere almeno un carattere speciale");
            return false;
        }
        return true;
    }


}
