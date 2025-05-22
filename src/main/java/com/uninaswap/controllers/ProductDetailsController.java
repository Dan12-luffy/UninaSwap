package com.uninaswap.controllers;

import com.uninaswap.dao.ListingDaoImpl;
import com.uninaswap.dao.UserDaoImpl;
import com.uninaswap.model.Listing;
import com.uninaswap.model.User;
import com.uninaswap.services.NavigationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.util.Objects;

public class ProductDetailsController {

    @FXML
    private Label productTitleLabel;
    @FXML
    private ImageView productImageView;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private Label categoryLabel;
    @FXML
    private Label conditionLabel;
    @FXML
    private Label sellerLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Button buyButton;
    @FXML
    private Button offerButton;
    @FXML
    private Button contactButton;
    @FXML
    private Button backButton;
    private Listing listing;

    @FXML
    public void initialize() {
        productTitleLabel.setText("Caricamento...");
        descriptionTextArea.setText("");
        priceLabel.setText("€0.00");
        conditionLabel.setText("N/A");
        categoryLabel.setText("N/A");
        sellerLabel.setText("N/A");
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

        if (listing.getPrice() != null) {
            priceLabel.setText("€" + listing.getPrice().toString());
        } else {
            priceLabel.setText("Prezzo non disponibile");
        }

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
                    buyButton.setText("Proponi scambio");
                    offerButton.setVisible(false);
                    break;
                case GIFT:
                    buyButton.setText("Richiedi regalo");
                    offerButton.setVisible(false);
                    priceLabel.setText("Gratis");
                    break;
                default:
                    buyButton.setText("Acquista ora");
                    offerButton.setVisible(true);
            }
        }
    }
}