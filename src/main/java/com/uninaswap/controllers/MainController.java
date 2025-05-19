package com.uninaswap.controllers;

import com.uninaswap.model.User;
import com.uninaswap.services.NavigationService;
import com.uninaswap.services.UserSession;
import com.uninaswap.services.ValidationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainController {

    @FXML
    private Button notificationButton;

    @FXML
    private Button profileButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button wishlistButton;

    @FXML
    private Button messagesButton;

    @FXML
    private Button sellButton;

    @FXML
    private ToggleButton allCategoryButton;

    @FXML
    private ToggleButton booksCategoryButton;

    @FXML
    private ToggleButton electronicsCategoryButton;

    @FXML
    private ToggleButton clothingCategoryButton;

    @FXML
    private ToggleButton notesCategoryButton;

    @FXML
    private ToggleButton toolsCategoryButton;

    @FXML
    private ToggleButton otherCategoryButton;

    @FXML
    private Button resetFilters;

    @FXML
    private Slider priceSlider;

    @FXML
    private TextField minPriceField;

    @FXML
    private TextField maxPriceField;

    @FXML
    private Button applyPriceButton;

    @FXML
    private Button applyFilters;

    @FXML
    private Label usernameLabel;

    @FXML
    private Button userItemsButton;

    @FXML
    private Button logoutButton;

    @FXML
    private ComboBox<String> sortComboBox;

    @FXML
    private ToggleButton listViewButton;

    @FXML
    private ToggleButton gridViewButton;

    @FXML
    private Button loadMoreButton;

    @FXML
    private ImageView logoImage;

    @FXML
    private void initialize() {
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            usernameLabel.setText("Ciao " + currentUser.getUsername());
        }
    }

    @FXML
    private void onNotificationButtonClicked() {
        // TODO: Implement notification logic
        System.out.println("Notification button clicked");
    }

    @FXML
    private void onProfileButtonClicked(ActionEvent event) {
        NavigationService.getInstance().navigateToOfferHistoryView(event);
    }

    @FXML
    private void onSearchButtonClicked() {
        // TODO: Implement search logic
        System.out.println("Search button clicked");
    }

    @FXML
    private void onWishlistButtonClicked() {
        // TODO: Implement wishlist logic
        System.out.println("Wishlist button clicked");
    }

    @FXML
    private void onMessagesButtonClicked() {
        // TODO: Implement messages logic
        System.out.println("Messages button clicked");
    }

    @FXML
    private void onSellButtonClicked() {
        // TODO: Implement sell logic
        System.out.println("Sell button clicked");
    }

    @FXML
    private void onCategoryButtonClicked(ActionEvent event) {
        // TODO: Implement category selection logic
        System.out.println("Category button clicked: " + ((ToggleButton) event.getSource()).getText());
    }

    @FXML
    private void onResetFiltersClicked() {
        // TODO: Implement reset filters logic
        System.out.println("Reset filters button clicked");
    }

    @FXML
    private void onApplyPriceButtonClicked() {
        // TODO: Implement price filter logic
        System.out.println("Apply price button clicked");
    }

    @FXML
    private void onApplyFiltersClicked() {
        // TODO: Implement apply filters logic
        System.out.println("Apply filters button clicked");
    }

    @FXML
    private void onUserItemsButtonClicked() {
        // TODO: Implement user items logic
        System.out.println("User items button clicked");
    }

    @FXML
    private void onLogoutButtonClicked(ActionEvent event) {
        UserSession.getInstance().logout();
        ValidationService.getInstance().showLogoutSuccess();
        try {
            NavigationService.getInstance().navigateToLoginView(event);
        } catch (Exception e) {
            ValidationService.getInstance().showFailedToOpenLoginPageError();
        }
    }

    @FXML
    private void onSortComboBoxChanged() {
        // TODO: Implement sorting logic
        System.out.println("Sort combo box changed: " + sortComboBox.getValue());
    }

    @FXML
    private void onViewToggleButtonClicked(ActionEvent event) {
        // TODO: Implement view toggle logic
        System.out.println("View toggle button clicked: " + ((ToggleButton) event.getSource()).getText());
    }

    @FXML
    private void onLoadMoreButtonClicked() {
        // TODO: Implement load more logic
        System.out.println("Load more button clicked");
    }
}