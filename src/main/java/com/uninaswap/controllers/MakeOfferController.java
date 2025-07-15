package com.uninaswap.controllers;

import com.uninaswap.model.typeOffer;
import javafx.event.ActionEvent;
import com.uninaswap.model.Insertion;
import com.uninaswap.model.InsertionStatus;
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
    private Insertion insertion;

    @FXML private Label productTitleLabel;
    @FXML private Label productDescriptionLabel;
    @FXML private Label productPriceLabel;
    @FXML private TextField offerAmountField;
    @FXML private Button submitOfferButton;
    @FXML private ImageView productImageView;
    @FXML private Label noImageLabel;
    @FXML private Label offerValidationLabel;

    private static final ValidationService validationService = ValidationService.getInstance();

    public void setListing(Insertion insertion) {
        this.insertion = insertion;
        if (insertion != null) {
            productTitleLabel.setText(insertion.getTitle());
            productDescriptionLabel.setText(insertion.getDescription());
            productPriceLabel.setText("€" + insertion.getPrice());

            setProductImage(insertion.getImageUrl());
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

    //TODO separa la logica di validazione in un metodo a parte
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
}