package com.uninaswap.exceptions;

import com.uninaswap.services.ValidationService;
import javafx.scene.control.Alert;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExchangeException extends Exception {
    private static final Logger logger = Logger.getLogger(ExchangeException.class.getName());

    public ExchangeException(String message) {
        super(message);
    }

    public ExchangeException(String message, Throwable cause) {
        super(message, cause);
        logger.log(Level.SEVERE, message, cause);
    }
    public void showError() {
        ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore per lo scambio", getMessage());
    }

    public static void showError(String message) {
        ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile completare lo scambio: " + message
        );
    }
}

