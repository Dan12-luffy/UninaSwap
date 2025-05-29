package com.uninaswap.controllers;

import com.uninaswap.dao.ListingDaoImpl;
import com.uninaswap.model.Listing;
import com.uninaswap.model.typeListing;
import com.uninaswap.services.NavigationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ExchangeController implements Initializable {

    @FXML private Button helpButton;
    @FXML private ImageView logoImage;
    @FXML private Button backButton;
    @FXML private VBox yourProductsContainer;
    @FXML private Label selectedCountLabel;
    @FXML private Label totalValueLabel;
    @FXML private ImageView desiredProductImageView;
    @FXML private Label desiredProductNameLabel;
    @FXML private Label desiredProductDescriptionLabel;
    @FXML private Label desiredProductPriceLabel;
    @FXML private Label desiredProductOwnerLabel;
    @FXML private Label yourTotalValueLabel;
    @FXML private Label desiredValueLabel;
    @FXML private Label differenceLabel;
    @FXML private VBox differenceMessageContainer;
    @FXML private Label differenceMessageLabel;
    @FXML private Button cancelExchangeButton;
    @FXML private Button previewExchangeButton;
    @FXML private Button confirmExchangeButton;

    private Listing desiredProduct;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadUserListings();
    }

    @FXML
    private void goBack(ActionEvent event) {
        NavigationService.getInstance().navigateToMainView(event);
    }

    @FXML
    private void cancelExchange() {
        // Cancel the exchange process
    }

    @FXML
    private void confirmExchange() {
        // Confirm and submit the exchange proposal
    }

    private void loadDesiredProduct() {
        // Load the product the user wants to obtain
    }

    private void updateCalculations() {
        // Update the exchange calculations based on selected products
    }

    private void updateDifferenceMessage() {
        // Update the message explaining the exchange difference
    }

    private void toggleConfirmButton() {
        // Enable/disable the confirm button based on selection status
    }
    private void loadUserListings() {
        try {
            ListingDaoImpl listingDao = new ListingDaoImpl();
            List<Listing> listings = listingDao.findMyAviableInsertions();

            // Clear existing content
            this.yourProductsContainer.getChildren().clear();

            if (listings.isEmpty()) {
                Label emptyLabel = new Label("Nessun annuncio trovato");
                this.yourProductsContainer.getChildren().add(emptyLabel);
            } else {
                for (Listing listing : listings) {
                    this.yourProductsContainer.getChildren().add(createListingCard(listing));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Errore nel caricamento degli annunci");
            yourProductsContainer.getChildren().add(errorLabel);
        }
    }
    private HBox createListingCard(Listing listing) {
        HBox card = new HBox(15);
        card.setPrefWidth(this.yourProductsContainer.getPrefWidth() - 20);
        card.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: white;");
        card.setPrefHeight(100);

        // Add hover effect
        card.setOnMouseEntered(e -> card.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: #f8f8f8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: white;"));

        ImageView imageView = new ImageView();
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 0); -fx-background-radius: 3;");

        String defaultImagePath = "/com/uninaswap/images/default_image.png";
        try {
            File imageFile = new File(listing.getImageUrl());
            imageView.setImage(new Image(imageFile.toURI().toString()));
        } catch (Exception e) {
            imageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(defaultImagePath))));
        }

        VBox textContent = new VBox(5);
        textContent.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        HBox.setHgrow(textContent, javafx.scene.layout.Priority.ALWAYS);

        Label titleLabel = new Label(listing.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        titleLabel.setWrapText(true);

        // Price and type
        //String priceText = listing.getType().equals(typeListing.GIFT) ? String.format("€%.2f", listing.getPrice()) : listing.getType().toString();
        /*String priceText;
        if (listing.getType().equals(typeListing.GIFT)) {
            priceText = typeListing.GIFT.toString();
        }
        else if (listing.getType().equals(typeListing.EXCHANGE)) {
            priceText = typeListing.EXCHANGE.toString();
        } else {
            priceText = String.format("€%.2f", listing.getPrice());
        }*/

        //Label priceLabel = new Label(priceText);
        //priceLabel.setStyle("-fx-font-size: 14px;");

        HBox infoBox = new HBox(10);
        Label statusLabel = new Label(listing.getStatus().toString());
        statusLabel.setStyle("-fx-font-size: 12px; -fx-background-color: #f0f0f0; -fx-padding: 2 5; -fx-background-radius: 3;");

        Label dateLabel = new Label(listing.getPublishDate() != null ? listing.getPublishDate().toString() : "");
        dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");

        infoBox.getChildren().addAll(statusLabel, dateLabel);
        textContent.getChildren().addAll(titleLabel, infoBox);

        VBox actionButtons = new VBox(8);
        actionButtons.setAlignment(javafx.geometry.Pos.CENTER);
        actionButtons.setPrefWidth(80);

        /*Button editButton = new Button("Modifica");
        editButton.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #444; -fx-background-radius: 3;");
        editButton.setPrefWidth(75);
        editButton.setOnAction(event -> {
            event.consume(); editListing(listing);
        });*/

        /*Button deleteButton = new Button("Elimina");
        deleteButton.setStyle("-fx-background-color: #ffecec; -fx-text-fill: #d32f2f; -fx-background-radius: 3;");
        deleteButton.setPrefWidth(75);
        deleteButton.setOnAction(event -> {
            event.consume();
            deleteListing(listing);
        });*/

        //actionButtons.getChildren().addAll(editButton, deleteButton);

        /*card.getChildren().addAll(imageView, textContent, actionButtons);
        card.setOnMouseClicked(event -> showItemDetails(event, listing));*/

        return card;
    }

    public void setListing(Listing listing) {
        if (listing == null) {
            return;
        }
        this.desiredProduct = listing;
        this.desiredProductNameLabel.setText(listing.getTitle());
        this.desiredProductDescriptionLabel.setText(listing.getDescription());
        this.desiredProductPriceLabel.setText("€" + listing.getPrice().toString());
        this.desiredProductOwnerLabel.setText("Utente: " + listing.getUserId());

        String defaultImagePath = "/com/uninaswap/images/default_image.png";
        try {
            File imageFile = new File(listing.getImageUrl());
            this.desiredProductImageView.setImage(new Image(imageFile.toURI().toString()));
        } catch (Exception e) {
            System.out.println("Impossibile caricare l'immagine: " + e.getMessage());
            this.desiredProductImageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(defaultImagePath))));
        }
    }
}