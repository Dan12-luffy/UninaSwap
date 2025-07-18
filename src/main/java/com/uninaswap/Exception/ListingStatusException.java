package com.uninaswap.Exception;

import com.uninaswap.model.InsertionStatus;

public class ListingStatusException extends Exception {

    private InsertionStatus currentStatus;
    private InsertionStatus requiredStatus;

    public ListingStatusException(String message) {
        super(message);
    }

    public ListingStatusException(InsertionStatus currentStatus, InsertionStatus requiredStatus) {
        super(String.format("Operazione non valida: l'inserzione è in stato %s, ma dovrebbe essere %s",
                currentStatus, requiredStatus));
        this.currentStatus = currentStatus;
        this.requiredStatus = requiredStatus;
    }

    public ListingStatusException(InsertionStatus invalidStatus, String operation) {
        super(String.format("Impossibile eseguire '%s' su un'inserzione in stato %s",
                operation, invalidStatus));
        this.currentStatus = invalidStatus;
    }

    public InsertionStatus getCurrentStatus() {
        return currentStatus;
    }

    public InsertionStatus getRequiredStatus() {
        return requiredStatus;
    }
}