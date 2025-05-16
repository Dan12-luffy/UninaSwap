// src/main/java/com/uninaswap/controllers/ExchangeController.java
package com.uninaswap.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class ExchangeController {

    @FXML private ImageView yourProductImageView;
    @FXML private ImageView wantedProductImageView;
    @FXML private Label yourProductTitleLabel;
    @FXML private Label youProductValueLabel;
    @FXML private Label yourProductPriceLabel;
    @FXML private Label wantedProductTitleLabel;
    @FXML private Label desiredProductValueLabel;
    @FXML private Label wantedProductPriceLabel;
    @FXML private Label moneyDifferenceValueLabel;
    @FXML private Label differenceAmountLabel;
    @FXML private Button backButton;
    @FXML private Button cancelTradeButton;
    @FXML private Button acceptTradeButton;

    @FXML
    public void initialize() {
        // Initialization logic here
    }

    @FXML
    private void onBackButtonClicked() {
        // Handle back button action
    }

    @FXML
    private void onCancelTradeButtonClicked() {
        // Handle cancel trade action
    }

    @FXML
    private void onAcceptTradeButtonClicked() {
        // Handle accept trade action
    }
}