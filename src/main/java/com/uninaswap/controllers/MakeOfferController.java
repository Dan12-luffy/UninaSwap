package com.uninaswap.controllers;

import javafx.event.ActionEvent;
import com.uninaswap.model.Listing;
import com.uninaswap.model.ListingStatus;
import com.uninaswap.model.Offer;
import com.uninaswap.services.NavigationService;
import com.uninaswap.services.OfferService;
import com.uninaswap.services.UserSession;
import com.uninaswap.services.ValidationService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.time.LocalDate;

public class MakeOfferController {
    private Listing listing;

    @FXML
    private Label productTitleLabel;
    @FXML
    private Label productDescriptionLabel;
    @FXML
    private Label productPriceLabel;
    @FXML
    private TextField offerAmountField;
    @FXML
    private Button submitOfferButton;
    @FXML
    private ImageView productImageView;
    @FXML
    private Label noImageLabel;
    @FXML
    private Label offerValidationLabel;

    private static final ValidationService validationService = ValidationService.getInstance();

    public void setListing(Listing listing) {
        this.listing = listing;
        if (listing != null) {
            productTitleLabel.setText(listing.getTitle());
            productDescriptionLabel.setText(listing.getDescription());
            productPriceLabel.setText("€" + listing.getPrice());


            setProductImage(listing.getImageUrl());
        }
    }

    public void setProductImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                Image image = new Image(imagePath);
                productImageView.setImage(image);
                productImageView.setVisible(true);
                noImageLabel.setVisible(false);
            } catch (Exception e) {
                // Se non riesce a caricare l'immagine, mostra il placeholder
                productImageView.setVisible(false);
                noImageLabel.setVisible(true);
            }
        } else {
            productImageView.setVisible(false);
            noImageLabel.setVisible(true);
        }
    }

    @FXML
    private void initialize() {
        // Inizializzazione se necessaria
    }

    @FXML
    private void handleSubmitOffer(ActionEvent event) {
        if (listing == null) {
            validationService.showAlert(Alert.AlertType.ERROR, "Errore", "Nessun prodotto selezionato.");
            return;
        }

        String offerAmountStr = offerAmountField.getText().replace("€", "").trim();
        if (offerAmountStr.isEmpty()) {
            validationService.showAlert(Alert.AlertType.ERROR, "Errore", "L'importo dell'offerta non può essere vuoto.");
            return;
        }

        double offerAmount;
        try {
            offerAmount = Double.parseDouble(offerAmountStr.replace(",", "."));
        } catch (NumberFormatException e) {
            validationService.showAlert(Alert.AlertType.ERROR, "Errore", "Inserisci un importo valido.");
            return;
        }

        if (offerAmount <= 0) {
            validationService.showAlert(Alert.AlertType.ERROR, "Errore", "L'importo deve essere positivo.");
            return;
        }

        if (listing.getPrice() != null && offerAmount >= listing.getPrice().doubleValue()) {
            validationService.showAlert(Alert.AlertType.ERROR, "Errore", "Per offerte pari o superiori al prezzo di listino, usa 'Acquista Ora'.");
            return;
        }

        int currentUserId = UserSession.getInstance().getCurrentUser().getId();
        if (listing.getUserId() == currentUserId) {
            validationService.showAlert(Alert.AlertType.ERROR, "Errore", "Non puoi fare offerte sui tuoi annunci.");
            return;
        }

        Offer offer = new Offer(
                listing.getListingId(),
                currentUserId,
                offerAmount,
                "Offerta per " + listing.getTitle(),
                ListingStatus.PENDING,
                LocalDate.now()
        );

        try {
            int offerId = OfferService.getInstance().createOffer(offer);
            if (offerId > 0) {
                validationService.showAlert(Alert.AlertType.INFORMATION, "Offerta inviata", "La tua offerta è stata inviata con successo al venditore!");
                NavigationService.getInstance().navigateToMainView(event);
            } else {
                validationService.showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile inviare l'offerta. Riprova più tardi.");
            }
        } catch (Exception e) {
            validationService.showAlert(Alert.AlertType.ERROR, "Errore", "Si è verificato un errore: " + e.getMessage());
        }
    }

    @FXML
    private void onExitButtonClicked(ActionEvent event) {
        NavigationService.getInstance().navigateToMainView(event);
    }
}