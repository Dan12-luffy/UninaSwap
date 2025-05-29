package com.uninaswap.controllers;

import com.uninaswap.dao.UserDaoImpl;
import com.uninaswap.model.Listing;
import com.uninaswap.services.NavigationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;

public class ProductDetailsController {

    @FXML private Label productTitleLabel;
    @FXML private ImageView productImageView;
    @FXML private TextArea descriptionTextArea;
    @FXML private Label categoryLabel;
    @FXML private Label conditionLabel;
    @FXML private Label sellerLabel;
    @FXML private Label priceLabel;
    @FXML private Button actionButton;
    @FXML private Button offerButton;
    @FXML private Button contactButton;
    @FXML private Button backButton;
    @FXML private Label conditionBadge;
    @FXML private Label typeBadge;
    @FXML private Label availabilityLabel;
    @FXML private Label dateLabel;

    private Listing listing;


    @FXML
    public void initialize() {

        // TODO aggiungere a listings le condizioni conditionBadge.setText("N/A");
    }

    @FXML
    private void onBackButtonClicked(ActionEvent event) {
        NavigationService.getInstance().navigateToMainView(event);
    }

    @FXML
    private void onBuyButtonClicked(ActionEvent event) {
        if (listing != null) {
            System.out.println("Acquisto prodotto: " + listing.getListingId());
        }
    }

    @FXML
    private void onOfferButtonClicked(ActionEvent event) {
        if (listing != null) {
            System.out.println("Proposta per: " + listing.getListingId());
        }
    }

    @FXML
    private void onContactButtonClicked(ActionEvent event) {
        if (listing != null) {
            System.out.println("Contatta venditore: " + listing.getUserId());
        }
    }
    public void loadProductDetails(Listing listing) {
        if (listing == null) {
            return;
        }
        this.listing = listing;

        productTitleLabel.setText(listing.getTitle());
        descriptionTextArea.setText(listing.getDescription());
        categoryLabel.setText(listing.getCategory());
        availabilityLabel.setText(listing.getStatus().toString());
        dateLabel.setText(listing.getPublishDate().toString());

        if (listing.getPrice() != null) {
            priceLabel.setText("€" + listing.getPrice().toString());
        } else {
            priceLabel.setText("Prezzo non disponibile");
        }
        calculateDaysDifferenceAndSetDateLabel(listing, dateLabel);
        sellerLabel.setText("Utente: " + new UserDaoImpl().fullNameFromID(listing.getUserId()));
        String defaultImagePath = "/com/uninaswap/images/default_image.png";
        try {
            File imageFile = new File(listing.getImageUrl());
            productImageView.setImage(new Image(imageFile.toURI().toString()));
        } catch (Exception e) {
            System.out.println("Impossibile caricare l'immagine: " + e.getMessage());
            productImageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(defaultImagePath))));
        }


        if (listing.getType() != null) {
            switch (listing.getType()) {
                case EXCHANGE:
                    actionButton.setText("Proponi scambio");
                    actionButton.setOnAction(this::onExchangeButtonClicked);
                    actionButton.setTranslateX(-150);
                    offerButton.setVisible(false);

                    break;
                case GIFT:
                    actionButton.setText("Richiedi regalo");
                    actionButton.setOnAction(this::onGiftButtonClicked);
                    actionButton.setTranslateX(-150);
                    offerButton.setVisible(false);
                    priceLabel.setText("Gratis");
                    break;
                default:
                    actionButton.setText("Acquista ora");
                    offerButton.setVisible(true);
            }
        }
    }
    private void calculateDaysDifferenceAndSetDateLabel(Listing listing, Label dateLabel) {
        long daysDifference = Math.abs(Date.valueOf(LocalDate.now()).toLocalDate().toEpochDay() -
                listing.getPublishDate().toEpochDay());
        String timeText;
        if (daysDifference == 0) {
            timeText = "oggi";
        } else if (daysDifference == 1) {
            timeText = "1 giorno fa";
        } else {
            timeText = daysDifference + " giorni fa";
        }
        dateLabel.setText("Pubblicato " + timeText);
    }
    private void onExchangeButtonClicked(ActionEvent event) {
        if (listing != null) {
            System.out.println("Proposta di scambio per: " + listing.getListingId());
            // Logica per la proposta di scambio
        }
    }
    private void onGiftButtonClicked(ActionEvent event) {
        if (listing != null) {
            // Logica per l'acquisto del prodotto
            System.out.println("Regalo prodotto: " + listing.getListingId());
        }
    }
}