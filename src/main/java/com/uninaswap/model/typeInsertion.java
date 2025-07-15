package com.uninaswap.model;

public enum typeInsertion {
        EXCHANGE("scambio"),
        GIFT("regalo"),
        SALE("vendita"),
        UNDEFINED("Non definito");

        private final String type;

        typeInsertion(String type) {
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

