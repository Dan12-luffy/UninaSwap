package com.uninaswap.controllers;

import com.uninaswap.model.Insertion;
import com.uninaswap.services.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;

public class ProductDetailsController {

    @FXML private Label productTitleLabel;
    @FXML private ImageView productImageView;
    @FXML private TextArea descriptionTextArea;
    @FXML private Label categoryLabel;
    @FXML private Label sellerLabel;
    @FXML private Label priceLabel;
    @FXML private Label sellerInitialsLabel;
    @FXML private Button actionButton;
    @FXML private Button offerButton;
    @FXML private Label availabilityLabel;
    @FXML private Label dateLabel;
    @FXML private VBox actionButtonsVbox;

    private Insertion insertion;

    private final InsertionService insertionService = InsertionService.getInstance();
    private final UserService userService = UserService.getInstance();

    @FXML
    public void initialize() {
    }

    @FXML
    private void onBackButtonClicked(ActionEvent event) {
        NavigationService.getInstance().navigateToMainView(event);
    }

    @FXML
    private void onBuyButtonClicked(ActionEvent event) {
        try {
            if (insertion != null) {
                NavigationService.getInstance().navigateToPurchaseConfirmationView(event, insertion);
            }
        } catch (Exception e) {
            ValidationService.getInstance().showFailedToOpenPageError();
        }
    }

    @FXML
    private void onOfferButtonClicked(ActionEvent event) {
        try {
            if (insertion != null) {
                NavigationService.getInstance().navigateToMakeOfferView(event, insertion);
            }
        } catch (Exception e) {
            ValidationService.getInstance().showFailedToOpenPageError();
        }
    }

    public void loadProductDetails(Insertion insertion) {
        if (insertion == null) {
            return;
        }
        this.insertion = insertion;
        this.productTitleLabel.setText(insertion.getTitle());
        this.descriptionTextArea.setText(insertion.getDescription());
        this.categoryLabel.setText(insertion.getCategory());
        this.availabilityLabel.setText(insertion.getStatus().toString());
        this.dateLabel.setText(insertion.getPublishDate().toString());
        this.sellerInitialsLabel.setText(insertionService.getSellerInitials(insertion.getUserId()));

        if (insertion.getPrice() != null) {
            this.priceLabel.setText("€" + insertion.getPrice().toString());
        } else {
            this.priceLabel.setText("Prezzo non disponibile");
        }
        calculateDaysDifferenceAndSetDateLabel(insertion, this.dateLabel);
        this.sellerLabel.setText("Utente: " + userService.getUserFullName(insertion.getUserId()));
        String defaultImagePath = "/com/uninaswap/images/default_image.png";

        try {
            File imageFile = new File(insertion.getImageUrl());
            this.productImageView.setImage(new Image(imageFile.toURI().toString()));
        } catch (Exception e) {
            System.out.println("Impossibile caricare l'immagine: " + e.getMessage());
            this.productImageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(defaultImagePath))));
        }


        if (insertion.getType() != null && !insertion.getUserId().equals(UserSession.getInstance().getCurrentUserId())) {
            switch (insertion.getType()) {
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
        } else {
            actionButton.setVisible(false);
            offerButton.setVisible(false);
            actionButtonsVbox.setVisible(false);
        }
    }

    private void calculateDaysDifferenceAndSetDateLabel(Insertion insertion, Label dateLabel) {
        long daysDifference = Math.abs(Date.valueOf(LocalDate.now()).toLocalDate().toEpochDay() -
                insertion.getPublishDate().toEpochDay());
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
        if (this.insertion != null) {
            NavigationService.getInstance().navigateToExchangeView(event, this.insertion);
        }
    }

    private void onGiftButtonClicked(ActionEvent event) {
        if (insertion != null) {
            NavigationService.getInstance().navigateToGiftView(event, this.insertion);
        }
    }
}
