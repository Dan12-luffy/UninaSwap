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


    private static final InsertionService insertionService = InsertionService.getInstance();

    @FXML
    private void sendGiftOffer() {
        String message = motivationTextArea.getText();
        // Valida e invia il messaggio al backend
    }

    @FXML
    private void cancelGiftOffer() {
        // Torna alla schermata precedente o chiudi il dialog
    }
    @FXML
    private void goBack(ActionEvent event) {
        NavigationService.getInstance().navigateToMainView(event);
    }

    @FXML
    private void updateCharacterCount() {
        if (motivationTextArea != null) {
            int currentLength = motivationTextArea.getText().length();
            int maxLength = 500;
        }
    }
    public void setListing(Insertion insertion) {
        this.insertion = insertion;
        if (insertion != null) {
            productTitleLabel.setText(insertion.getTitle());
            productDescriptionLabel.setText(insertion.getDescription());
            ownerNameLabel.setText(insertionService.getSellerFullName(insertion.getUserId()));
            setProductImage(insertion.getImageUrl());
        }
    }
    public void setProductImage(String imagePath) {
        String defaultImagePath = "/com/uninaswap/images/default_image.png";
        try {
            File imageFile = new File(insertion.getImageUrl());
            this.productImageView.setImage(new Image(imageFile.toURI().toString()));
        } catch (Exception e) {
            System.out.println("Impossibile caricare l'immagine: " + e.getMessage());
            this.productImageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(defaultImagePath))));
        }
    }
}
