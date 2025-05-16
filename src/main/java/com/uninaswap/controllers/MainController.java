package com.uninaswap.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MainController {
    private String username;

    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private Button sellButton;
    @FXML
    private Button applyFilters;
    @FXML
    private Button resetFilters;
    @FXML
    private Button profileButton;
    @FXML
    private Label usernameLabel;

    @FXML
    private void initialize() {
        // Optionally set the label if username was set before initialize
        if (username != null) {
            usernameLabel.setText("Ciao " + username);
        }
    }

    public void setUsername(String username) {
        this.username = username;
        if (usernameLabel != null) {
            usernameLabel.setText("Ciao " + username);
        }
    }


    @FXML
    private void onSearchButtonClicked() {
        // TODO: Implement search logic
        System.out.println("Search button clicked");
    }

    @FXML
    private void onSellButtonClicked() {
        // TODO: Implement sell/proposals logic
        System.out.println("Sell/Proposals button clicked");
    }

    @FXML
    private void onApplyFiltersClicked() {
        // TODO: Implement apply filters logic
        System.out.println("Apply filters button clicked");
    }

    @FXML
    private void onResetFiltersClicked() {
        // TODO: Implement reset filters logic
        System.out.println("Reset filters button clicked");
    }

    @FXML
    private void onProfileButtonClicked() {
        // TODO: Implement profile logic
        System.out.println("Profile button clicked");
    }
}