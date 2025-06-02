package com.uninaswap.controllers;

import com.uninaswap.model.Listing;
import com.uninaswap.services.FavouriteService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class FavoritesViewController {
    @FXML private VBox favoritesContainer;
    @FXML private Label emptyLabel;

    private final FavouriteService favouriteService = FavouriteService.getInstance();

    @FXML
    public void initialize() {
        loadFavorites();
    }

    private void loadFavorites() {
        try {
            // Clear existing items
            favoritesContainer.getChildren().clear();

            // Get favorites for current user
            List<Listing> favorites = favouriteService.getUserFavorites();

            if (favorites == null || favorites.isEmpty()) {
                emptyLabel.setVisible(true);
            } else {
                emptyLabel.setVisible(false);

                // Add each favorite listing to the container
                for (Listing listing : favorites) {
                    favoritesContainer.getChildren().add(createFavoriteCard(listing));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading favorites: " + e.getMessage());
        }
    }

    private Node createFavoriteCard(Listing listing) {
        // Create a card for the favorite listing
        HBox card = new HBox(10);
        card.getStyleClass().add("listing-card");
        card.setPadding(new javafx.geometry.Insets(10));

        // Add listing details
        VBox detailsBox = new VBox(5);
        HBox.setHgrow(detailsBox, Priority.ALWAYS);

        Label titleLabel = new Label(listing.getTitle());
        titleLabel.getStyleClass().add("listing-title");

        Label priceLabel = new Label(String.format("%.2f €", listing.getPrice()));
        priceLabel.getStyleClass().add("listing-price");

        Label descriptionLabel = new Label(listing.getDescription());
        descriptionLabel.setWrapText(true);
        descriptionLabel.getStyleClass().add("listing-description");

        detailsBox.getChildren().addAll(titleLabel, priceLabel, descriptionLabel);

        // Create remove button
        Button removeButton = new Button("Rimuovi");
        removeButton.getStyleClass().add("delete-button");
        removeButton.setOnAction(e -> {
            FavouriteService.getInstance().removeFromFavorites(listing.getListingId());
            loadFavorites(); // Refresh after removing
        });

        card.getChildren().addAll(detailsBox, removeButton);
        return card;
    }

    @FXML
    private void goBack(ActionEvent event) {
        // Close the favorites window
        Stage stage = (Stage) favoritesContainer.getScene().getWindow();
        stage.close();
    }
}