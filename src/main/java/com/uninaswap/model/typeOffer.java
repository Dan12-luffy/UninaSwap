package com.uninaswap.model;

public enum typeOffer {
    EXCHANGE_OFFER("EXCHANGE_OFFER"),
    GIFT_OFFER("GIFT_OFFER"),
    SALE_OFFER("SALE_OFFER"),
    UNDEFINED("Non definito");

    private final String type;

    typeOffer(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }
}
