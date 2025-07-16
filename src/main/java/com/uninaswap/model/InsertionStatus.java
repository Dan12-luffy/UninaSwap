package com.uninaswap.model;

public enum InsertionStatus {

    AVAILABLE("AVAILABLE"),
    PENDING("PENDING"),
    ACCEPTED("ACCEPTED"),
    REJECTED("REJECTED"),
    SOLD("SOLD"),
    DISMISSED("DISMISSED");

    private final String status;

    InsertionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return switch (status) {
            case "AVAILABLE" -> "Disponibile";
            case "PENDING" -> "In attesa di approvazione";
            case "ACCEPTED" -> "Accettato";
            case "REJECTED" -> "Rifiutato";
            case "SOLD" -> "Venduto";
            case "DISMISSED" -> "Offerta accantonata";
            default -> "Stato sconosciuto";
        };
    }
}
