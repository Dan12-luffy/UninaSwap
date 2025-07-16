package com.uninaswap.controllers;

import com.uninaswap.model.Insertion;
import com.uninaswap.model.InsertionStatus;
import com.uninaswap.model.User;
import com.uninaswap.services.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PurchaseConfirmationController {
    @FXML private Label titleLabel;
    @FXML private Label priceLabel;
    @FXML private Label sellerLabel;
    @FXML private Label descriptionLabel;
    @FXML private ImageView productImageView;

    private Insertion insertion;
    private ValidationService validationService = ValidationService.getInstance();
    private OfferService offerService = OfferService.getInstance();

    public void setListing(Insertion insertion) {
        this.insertion = insertion;
        populateData();
    }

    private void populateData(){
        if(insertion != null){
            titleLabel.setText(insertion.getTitle());
            priceLabel.setText(String.format("€%.2f", insertion.getPrice()));
            try{
                String sellerName = InsertionService.getInstance().getSellerFullName(insertion.getUserId());
                sellerLabel.setText("Venditore : " + sellerName);
            }catch(Exception e){
                sellerLabel.setText("Venditore : informazioni non disponibili");

            }
            descriptionLabel.setText(insertion.getDescription());
            if(insertion.getImageUrl() != null && !insertion.getImageUrl().isEmpty()){
                try{
                    Image image = new Image(insertion.getImageUrl());
                    productImageView.setImage(image);
                } catch (Exception e) {
                    productImageView.setVisible(false);
                }
            }else{
                productImageView.setVisible(false);
            }
        }
    }
    private void verifyUser(User currentUser) throws IllegalArgumentException{

        if(insertion.getUserId() == currentUser.getId()){
            throw new  IllegalArgumentException("Non puoi acquistare il tuo stesso prodotto.");
        }
    }
    @FXML
    private void onConfirmPurchase(ActionEvent event) {
        if(insertion == null){
            validationService.showAlert(Alert.AlertType.ERROR, "Errore", "Nessun prodotto selezionato.");
            return;
        }
        try{
            User currentUser = UserSession.getInstance().getCurrentUser();
            verifyUser(currentUser);

            int transactionId = TransactionService.getInstance().recordSale(insertion,null ,currentUser);

            if(transactionId > 0) {
                insertion.setStatus(InsertionStatus.SOLD);
                InsertionService.getInstance().updateInsertion(insertion);
                validationService.showAlert(Alert.AlertType.INFORMATION, "Acquisto effettuato",
                        "Acquisto completato con successo! Il prodotto è ora tuo.");
            } else {
                validationService.showAlert(Alert.AlertType.WARNING, "Acquisto effettuato",
                        "Prodotto acquistato, ma errore nella registrazione della transazione.");
            }
            NavigationService.getInstance().navigateToMainView(event);

        }catch(Exception e){
           validationService.showAlert(Alert.AlertType.ERROR, "Errore", "Si è verificato un errore durante l'elaborazione dell'acquisto: " + e.getMessage());
        }

    }

    @FXML
    private void onCancelPurchase(ActionEvent event) {
        NavigationService.getInstance().navigateToMainView(event);
    }





}
