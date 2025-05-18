package com.uninaswap.controllers;

import com.uninaswap.model.User;
import com.uninaswap.services.NavigationService;
import com.uninaswap.services.UserSession;
import com.uninaswap.services.ValidationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
    private Button logoutButton;
    @FXML
    private Label usernameLabel;

    @FXML
    private void initialize() {
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            usernameLabel.setText("Ciao " + currentUser.getUsername());
        }
    }


    @FXML
    private void onSearchButtonClicked() {
        // TODO: Implement search logic
        System.out.println("Search button clicked");
    }
    @FXML
    private void onLogoutButtonClicked(ActionEvent event) {
        UserSession.getInstance().logout();
        ValidationService.getInstance().showLogoutSuccess();
        try {
            NavigationService.getInstance().navigateToLoginView(event);
        }catch (Exception e) {
            ValidationService.getInstance().showFailedToOpenLoginPageError();
        }
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