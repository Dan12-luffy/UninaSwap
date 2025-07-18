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
import java.util.Objects;

public class GiftController {
    private Insertion insertion;

    @FXML private TextArea motivationTextArea;
    @FXML private ImageView productImageView;
    @FXML private Label productDescriptionLabel;
    @FXML private Label productTitleLabel;
    @FXML private Label ownerNameLabel;
    @FXML private Label characterCountLabel;
    @FXML private Button sendGiftOfferButton;
    @FXML private Label ownerInitialsLabel;


    private static final InsertionService insertionService = InsertionService.getInstance();
    private static final OfferService offerService = OfferService.getInstance();

    @FXML public void initialize() {
        if (this.motivationTextArea != null) {
            this.motivationTextArea.textProperty().addListener((_, _, newValue) -> {
                this.updateCharacterCount();
                this.sendGiftOfferButton.setDisable(newValue.isEmpty());
            });
        }
    }

    @FXML
    private void sendGiftOffer(ActionEvent event) {
        String message = this.motivationTextArea.getText();
        if (message.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Il campo motivazione non può essere vuoto.", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        try {
            Offer offer = new Offer(this.insertion.getInsertionID(), UserSession.getInstance().getCurrentUserId(), 0, message, typeOffer.GIFT_OFFER, InsertionStatus.PENDING, LocalDate.now());
            offerService.createOffer(offer);
            NavigationService.getInstance().navigateToMainView(event);
        }
        catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Errore durante l'invio dell'offerta: " + ex.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    @FXML
    private void cancelGiftOffer(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Sei sicuro di voler annullare l'offerta?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                NavigationService.getInstance().navigateToMainView(event);
            }
        });
    }

    @FXML
    private void goBack(ActionEvent event) {
        NavigationService.getInstance().navigateToMainView(event);
    }

    @FXML
    private void updateCharacterCount() {
        if (this.motivationTextArea != null && this.characterCountLabel != null) {
            int currentLength = this.motivationTextArea.getText().length();
            int maxLength = 500;
            int remaining = maxLength - currentLength;

            this.characterCountLabel.setText(remaining + "/" + maxLength);

            if (remaining < 50) {
                this.characterCountLabel.setStyle("-fx-text-fill: red;");
            } else if (remaining < 100) {
                this.characterCountLabel.setStyle("-fx-text-fill: orange;");
            } else {
                this.characterCountLabel.setStyle("-fx-text-fill: green;");
            }

            if (currentLength > maxLength) {
                this.motivationTextArea.setText(this.motivationTextArea.getText().substring(0, maxLength));
                this.motivationTextArea.positionCaret(maxLength);
            }
        }
    }
    public void setInsertion(Insertion insertion) {
        this.insertion = insertion;
        if (insertion != null) {
            this.productTitleLabel.setText(insertion.getTitle());
            this.productDescriptionLabel.setText(insertion.getDescription());
            this.ownerNameLabel.setText(insertionService.getSellerFullName(insertion.getUserId()));
            this.ownerInitialsLabel.setText(insertionService.getSellerInitials(insertion.getUserId()));
            this.setProductImage();
        }
    }
    public void setProductImage() {
        String defaultImagePath = "/com/uninaswap/images/default_image.png";
        try {
            File imageFile = new File(this.insertion.getImageUrl());
            this.productImageView.setImage(new Image(imageFile.toURI().toString()));
        } catch (Exception e) {
            System.out.println("Impossibile caricare l'immagine: " + e.getMessage());
            this.productImageView.setImage(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream(defaultImagePath))));
        }
    }
}
