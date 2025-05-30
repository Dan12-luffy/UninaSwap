package com.uninaswap.controllers;

import com.uninaswap.dao.ListingDaoImpl;
import com.uninaswap.dao.UserDaoImpl;
import com.uninaswap.model.Listing;
import com.uninaswap.model.typeListing;
import com.uninaswap.services.NavigationService;
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
import java.math.BigDecimal;
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


    private void updateCalculations(Listing listing, boolean isSelected) {
        String valueText = this.totalValueLabel.getText().replace("€", "");
        double totalValue = Double.parseDouble(valueText);

        String countText = this.selectedCountLabel.getText().replaceAll("[^0-9]", "");
        int count = Integer.parseInt(countText);

        if(isSelected) {
            totalValue += listing.getPrice().doubleValue();
            count += 1;
        } else {
            totalValue -= listing.getPrice().doubleValue();
            count -= 1;
        }

        selectedCountLabel.setText(String.valueOf(count));
        totalValueLabel.setText("€" + String.format("%.2f", totalValue));
        yourTotalValueLabel.setText("Il tuo valore totale: €" + String.format("%.2f", totalValue));

        double difference = this.desiredProduct.getPrice().doubleValue() - totalValue;
        differenceLabel.setText("Differenza: €" + String.format("%.2f", difference));

        updateDifferenceMessage();
        toggleConfirmButton();
    }
    private void updateDifferenceMessage() {
        // Update the message explaining the exchange difference
    }

    private void toggleConfirmButton() {
        // Enable/disable the confirm button based on selection status
    }

    private HBox createListingCard(Listing listing) {
        HBox card = new HBox(15);
        card.setPrefWidth(this.yourProductsContainer.getPrefWidth() - 20);
        card.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: white;");
        card.setPrefHeight(100);

        card.setUserData(false);

        card.setOnMouseEntered(e -> {
            if (!((boolean) card.getUserData())) {
                card.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: #f8f8f8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
            }
        });
        card.setOnMouseExited(e -> {
            if (!((boolean) card.getUserData())) {
                card.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: white;");
            }
        });

        // Product image
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

        String priceText;
        if (listing.getType() == typeListing.GIFT) {
            priceText = "Gratis";
        } else if (listing.getType() == typeListing.EXCHANGE) {
            priceText = "Scambio";
        } else {
            priceText = String.format("€%.2f", listing.getPrice());
        }

        Label priceLabel = new Label(priceText);
        priceLabel.setStyle("-fx-font-size: 14px;");

        textContent.getChildren().addAll(titleLabel, priceLabel);

        card.setOnMouseClicked(event -> {
            boolean currentState = (boolean)card.getUserData();
            boolean newState = !currentState;
            card.setUserData(newState);

            if (newState) {
                card.setStyle("-fx-padding: 10; -fx-border-color: #4CAF50; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: #f1f8e9;");
            } else {
                card.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: white;");
            }
            updateCalculations(listing,newState);
        });

        VBox actionBox = new VBox(5);
        actionBox.setAlignment(javafx.geometry.Pos.CENTER);

        card.getChildren().addAll(imageView, textContent, actionBox);
        return card;
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

    public void loadDesiredProduct(Listing listing) {
        if (listing == null) {
            return;
        }
        this.desiredProduct = listing;
        this.desiredProductNameLabel.setText(listing.getTitle());
        this.desiredProductDescriptionLabel.setText(listing.getDescription());
        this.desiredProductPriceLabel.setText("€" + listing.getPrice().toString());
        this.desiredValueLabel.setText("€" + listing.getPrice().toString());
        this.desiredProductOwnerLabel.setText("Utente: " + new UserDaoImpl().fullNameFromID(listing.getUserId()));

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