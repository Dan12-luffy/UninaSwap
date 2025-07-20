package com.uninaswap.services;

import com.uninaswap.dao.UserDao;
import com.uninaswap.dao.UserDaoImpl;
import com.uninaswap.utility.Sha256;
import javafx.scene.control.Alert;


public class ProfileSecurityService {

    private static final ProfileSecurityService instance = new ProfileSecurityService();
    private final UserDao userDao;

    private ProfileSecurityService() {
        this.userDao = new  UserDaoImpl();
    }

    public static ProfileSecurityService getInstance() {
        return instance;
    }

    public boolean changePassword(String currentPassword, String newPassword){
        try{
            String hashedCurrentPassword = Sha256.hashPassword(currentPassword);
            if(!UserSession.getInstance().getCurrentUser().getPassword().equals(hashedCurrentPassword)){
                ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "La password attuale non è corretta");
                return false;
            }

            if(!isValidPassword(newPassword)){
                ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "La nuova password non è valida");
                return false;
            }
            String hashedNewPassword = Sha256.hashPassword(newPassword);
            boolean success = userDao.updatePassword(UserSession.getInstance().getCurrentUserId(), hashedNewPassword);

            if(success){
                ValidationService.getInstance().showAlert(Alert.AlertType.INFORMATION, "Successo", "Password cambiata con successo");
            }else{
                ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile cambiare la password");
            }
            return success;

        }catch(Exception e){
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Si è verificato un errore durante il cambio della password");
            return false;
        }

    }
    public boolean changeUsername(String newUsername){
        try{
            if(!isValidUsername(newUsername)){
                ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Il nuovo username non è valido");
                return false;
            }
            if(userDao.usernameAlreadyExists(newUsername)){
                ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Username già in uso");
                return false;
            }
            if(UserSession.getInstance().getCurrentUser().getUsername().equals(newUsername)){
                ValidationService.getInstance().showAlert(Alert.AlertType.INFORMATION, "Informazione", "Il nuovo username è lo stesso del precedente");
                return false;
            }

            boolean success = userDao.updateUsername(UserSession.getInstance().getCurrentUserId(), newUsername);
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

        if(username == null || username.isEmpty()) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Lo username non può essere vuoto");
            return false;
        }
        if(username.length() < 5 || username.length() > 20) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Lo username deve essere tra 5 e 20 caratteri");
            return false;
        }
        // Controlla che contenga solo lettere, numeri e underscore
        if(!username.matches("^[a-zA-Z0-9_]+$")) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Lo username può contenere solo lettere, numeri e underscore");
            return false;
        }
        return true;
    }

    public boolean isValidPassword(String password) {
        if(password == null || password.isEmpty()) {
            return false;
        }
        if(password.length() < 8 || password.length() > 20) {
            return false;
        }
        if(!password.matches(".*[a-z].*")) {
            return false;
        }
        if(!password.matches(".*[A-Z].*")) {
            return false;
        }
        if(!password.matches(".*\\d.*")) {
            return false;
        }
        if(!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            return false;
        }
        return true;
    }
}
