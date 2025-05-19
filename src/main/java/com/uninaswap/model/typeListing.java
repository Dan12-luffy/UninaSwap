package com.uninaswap.model;

public enum typeListing {
        BUY("Buy"),
        EXCHANGE("Exchange"),
        GYFT("Gift"),
        SELL("Sell");

        private final String type;

        typeListing(String type) {
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

