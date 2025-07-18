package com.uninaswap.controllers;

import com.uninaswap.model.typeOffer;
import com.uninaswap.services.*;
import javafx.event.ActionEvent;
import com.uninaswap.model.Insertion;
import com.uninaswap.model.InsertionStatus;
import com.uninaswap.model.Offer;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import java.io.File;
import java.time.LocalDate;

public class SaleController {
    private Insertion insertion;

    @FXML private Label productTitleLabel;
    @FXML private Label productDescriptionLabel;
    @FXML private Label productPriceLabel;
    @FXML private TextField offerAmountField;
    @FXML private Button submitOfferButton;
    @FXML private ImageView productImageView;
    @FXML private Label noImageLabel;
    @FXML private Label offerValidationLabel;
    @FXML private Label sellerNameLabel;

    private static final ValidationService validationService = ValidationService.getInstance();
    private static final InsertionService insertionService = InsertionService.getInstance();



    @FXML
    private void initialize() {
        offerAmountField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            if (newText.isEmpty()) {
                return change;
            }
            if (change.getText().equals(",")) {
                change.setText(".");
            }
            if (newText.matches("\\d*(\\.\\d{0,2})?")) {
                return change;
            }
            return null;
        }));

        offerAmountField.setPromptText("0.00");
    }

    @FXML
    private void handleSubmitOffer(ActionEvent event) {
        if (insertion == null) {
            validationService.showAlert(Alert.AlertType.ERROR, "Errore", "Nessun prodotto selezionato.");
            return;
        }

        String offerAmountStr = offerAmountField.getText().replace("€", "").trim();
        if (offerAmountStr.isEmpty()) {
            validationService.showAlert(Alert.AlertType.ERROR, "Errore", "L'importo dell'offerta non può essere vuoto.");
            return;
        }

        double offerAmount = Double.parseDouble(offerAmountField.getText().trim());
        if (offerAmount <= 0) {
            validationService.showAlert(Alert.AlertType.ERROR, "Errore", "L'importo deve essere positivo.");
            return;
        }

        if (insertion.getPrice() != null && offerAmount >= insertion.getPrice().doubleValue()) {
            validationService.showAlert(Alert.AlertType.ERROR, "Errore", "Per offerte pari o superiori al prezzo di listino, usa 'Acquista Ora'.");
            return;
        }

        int currentUserId = UserSession.getInstance().getCurrentUser().getId();
        if (insertion.getUserId() == currentUserId) {
            validationService.showAlert(Alert.AlertType.ERROR, "Errore", "Non puoi fare offerte sui tuoi annunci.");
            return;
        }

        Offer offer = new Offer(
                insertion.getInsertionID(),
                currentUserId,
                offerAmount,
                null,
                typeOffer.SALE_OFFER,
                InsertionStatus.PENDING,
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

    public void setListing(Insertion insertion) {
        this.insertion = insertion;
        if (insertion != null) {
            productTitleLabel.setText(insertion.getTitle());
            productDescriptionLabel.setText(insertion.getDescription());
            productPriceLabel.setText("€" + insertion.getPrice());
            sellerNameLabel.setText(insertionService.getSellerFullName(insertion.getUserId()));

            setProductImage();
        }
    }

    public void setProductImage() {
        try {
            File imageFile = new File(insertion.getImageUrl());
            this.productImageView.setImage(new Image(imageFile.toURI().toString()));
            productImageView.setVisible(true);
            noImageLabel.setVisible(false);
        } catch (Exception e) {
            // Se non riesce a caricare l'immagine, mostra il placeholder
            productImageView.setVisible(false);
            noImageLabel.setVisible(true);
        }
    }
}