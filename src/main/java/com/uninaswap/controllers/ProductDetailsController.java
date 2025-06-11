package com.uninaswap.controllers;

import com.uninaswap.dao.UserDaoImpl;
import com.uninaswap.model.Listing;
import com.uninaswap.services.FavouriteService;
import com.uninaswap.services.NavigationService;
import com.uninaswap.services.ValidationService;
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
    @FXML private Button favoriteButton;
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
      /* try {
           if (listing != null) {
               NavigationService.getInstance().navigateToPurchaseConfirmationView(event, listing);
           }
       }catch(Exception e){
           ValidationService.getInstance().showFailedToOpenPageError();
       }*/
    }

    @FXML
    private void onOfferButtonClicked(ActionEvent event) {
        try {
            if (listing != null) {
                NavigationService.getInstance().navigateToMakeOfferView(event, listing);
            }
        }catch(Exception e){
            ValidationService.getInstance().showFailedToOpenPageError();
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

        this.productTitleLabel.setText(listing.getTitle());
        this.descriptionTextArea.setText(listing.getDescription());
        this.categoryLabel.setText(listing.getCategory());
        this.availabilityLabel.setText(listing.getStatus().toString());
        this.dateLabel.setText(listing.getPublishDate().toString());

        if (listing.getPrice() != null) {
            this.priceLabel.setText("€" + listing.getPrice().toString());
        } else {
            this.priceLabel.setText("Prezzo non disponibile");
        }
        calculateDaysDifferenceAndSetDateLabel(listing, this.dateLabel);
        this.sellerLabel.setText("Utente: " + new UserDaoImpl().findFullNameFromID(listing.getUserId()));
        String defaultImagePath = "/com/uninaswap/images/default_image.png";

        try {
            File imageFile = new File(listing.getImageUrl());
            this.productImageView.setImage(new Image(imageFile.toURI().toString()));
        } catch (Exception e) {
            System.out.println("Impossibile caricare l'immagine: " + e.getMessage());
            this.productImageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(defaultImagePath))));
        }


        if (listing.getType() != null) {
            switch (listing.getType()) {
                case EXCHANGE:
                    this.actionButton.setText("Proponi scambio");
                    this.actionButton.setOnAction(this::onExchangeButtonClicked);
                    this.actionButton.setTranslateX(-150);
                    this.offerButton.setVisible(false);

                    break;
                case GIFT:
                    this.actionButton.setText("Richiedi regalo");
                    this.actionButton.setOnAction(this::onGiftButtonClicked);
                    this.actionButton.setTranslateX(-150);
                    this.offerButton.setVisible(false);
                    this.priceLabel.setText("Gratis");
                    break;
                default:
                    this.actionButton.setText("Acquista ora");
                    this.offerButton.setVisible(true);
            }
        }
        updateFavoriteButton();
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
        if (this.listing != null) {
            NavigationService.getInstance().navigateToExchangeView(event, this.listing);
        }
    }
    private void onGiftButtonClicked(ActionEvent event) {
        if (listing != null) {
            // Logica per l'acquisto del prodotto
            System.out.println("Regalo prodotto: " + listing.getListingId());
        }
    }
    @FXML
    private void toggleFavorite() {
        try {
            FavouriteService.getInstance().toggleFavorite(listing.getListingId());
            updateFavoriteButton();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void updateFavoriteButton() {
        boolean isFavorite = FavouriteService.getInstance().isFavorite(listing.getListingId());
        if(isFavorite){
            favoriteButton.getStyleClass().add("favorite");
            favoriteButton.setText("♥ Nei preferiti");
        }else{
            favoriteButton.getStyleClass().remove("favorite"); // Here's the error
            favoriteButton.setText("Aggiungi ai preferiti");
        }
    }
}