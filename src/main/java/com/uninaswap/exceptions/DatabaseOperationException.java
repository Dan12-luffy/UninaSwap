package com.uninaswap.exceptions;

public class DatabaseOperationException extends RuntimeException {

    private final String operation;

    public DatabaseOperationException(String operation, Exception cause) {
        super("Errore durante l'operazione '" + operation + "': " + cause.getMessage(), cause);
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }
}