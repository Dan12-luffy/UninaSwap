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


    private static void handleSQLException(SQLException e) {
        String message = e.getMessage();
        if (message.contains("insertion_status_check")) {
            showError("Lo stato del prodotto non può essere aggiornato. Verificare che sia ancora disponibile.");
        } else if (message.contains("validate_offer_content")) {
            showError("L'offerta non rispetta i requisiti per questo tipo di prodotto.");
        } else {
            showError("Errore di database: " + message);
        }
        logger.log(Level.SEVERE, "Database error during exchange", e);
    }
}

