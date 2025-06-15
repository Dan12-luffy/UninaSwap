package com.uninaswap.controllers;

import com.kitfox.svg.A;
import com.uninaswap.model.Listing;
import com.uninaswap.model.ListingStatus;
import com.uninaswap.model.Offer;
import com.uninaswap.model.User;
import com.uninaswap.services.ListingService;
import com.uninaswap.services.NavigationService;
import com.uninaswap.services.OfferService;
import com.uninaswap.services.ValidationService;
import com.uninaswap.services.UserSession;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.time.LocalDate;

public class PurchaseConfirmationController {
    @FXML
    private Label titleLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label sellerLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private ImageView productImageView;


    private Listing listing;
    private ValidationService validationService = ValidationService.getInstance();
    private OfferService offerService = OfferService.getInstance();


    public void setListing(Listing listing) {
        this.listing = listing;
        populateData();
    }

    private void populateData(){
        if(listing != null){
            titleLabel.setText(listing.getTitle());
            priceLabel.setText(String.format("€%.2f", listing.getPrice()));
            try{
                String sellerName = ListingService.getInstance().getSellerFullName(listing.getUserId());
                sellerLabel.setText("Venditore : " + sellerName);
            }catch(Exception e){
                sellerLabel.setText("Venditore : informazioni non disponibili");

            }
            descriptionLabel.setText(listing.getDescription());
            if(listing.getImageUrl() != null && !listing.getImageUrl().isEmpty()){
                try{
                    Image image = new Image(listing.getImageUrl());
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

        if(listing.getUserId() == currentUser.getId()){
            throw new  IllegalArgumentException("Non puoi acquistare il tuo stesso prodotto.");
        }
    }
    @FXML
    private void onConfirmPurchase(ActionEvent event) {
        if(listing == null){
            validationService.showAlert(Alert.AlertType.ERROR, "Errore", "Nessun prodotto selezionato.");
            return;
        }
        try{
            User currentUser = UserSession.getInstance().getCurrentUser();
            verifyUser(currentUser);

            listing.setStatus(ListingStatus.SOLD);
            ListingService.getInstance().updateListing(listing);

            validationService.showAlert(Alert.AlertType.INFORMATION," Acquisto effettuato ", "Acquisto completato con successo ! il prodotto è ora tuo " );
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
