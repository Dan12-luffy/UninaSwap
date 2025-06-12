package com.uninaswap.controllers;

import com.uninaswap.dao.OfferDao;
import com.uninaswap.dao.OfferDaoImpl;
import com.uninaswap.model.*;
import com.uninaswap.services.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;

public class NotificationsController implements Initializable {

    @FXML private Button homeButton;
    @FXML private ImageView logoImage;
    @FXML private Button profileButton;
    @FXML private Label notificationsCountLabel;
    @FXML private ScrollPane notificationsScrollPane;
    @FXML private VBox notificationsContainer;
    @FXML private Circle unreadIndicator1;
    @FXML private Button declineOffer1;
    @FXML private Button acceptOffer1;
    @FXML private Button counterOffer1;
    @FXML private Circle unreadIndicator2;
    @FXML private Button declineOffer2;
    @FXML private Button acceptOffer2;
    @FXML private Button counterOffer2;
    @FXML private Circle unreadIndicator3;
    @FXML private Button declineSwap1;
    @FXML private Button acceptSwap1;
    @FXML private VBox noMoreNotifications;

    private final OfferDao offerDao = new OfferDaoImpl();
    private final ListingService listingService = ListingService.getInstance();
    private final UserService userService = UserService.getInstance();
    //TODO, implementare le notifiche e il dettaglio dell'offerta
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadUserOffers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadUserOffers() throws SQLException {
        notificationsContainer.getChildren().clear();
        List<Offer> offers = offerDao.findOffersToCurrentUser();

        notificationsCountLabel.setText(offers.size() + " nuove offerte");
        for (Offer offer : offers) {
            VBox offerCard = createOfferCard(offer);
            notificationsContainer.getChildren().add(offerCard);
        }
    }
    //Questo metodo non l'ho fatto io, l'ha fatto claude perchè sono un cane
    private VBox createOfferCard(Offer offer) throws SQLException {
        VBox card = new VBox();
        card.setStyle("-fx-effect: none !important; -fx-background-insets: 0; -fx-border-color: #e0e0e0; -fx-border-width: 1.5; -fx-border-radius: 4; -fx-padding: 12;");

        // Create the content layout
        HBox content = new HBox();
        content.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        content.setSpacing(12);

        VBox offerContent = new VBox();
        offerContent.setSpacing(4);
        HBox.setHgrow(offerContent, javafx.scene.layout.Priority.ALWAYS);

        Listing listing = listingService.getListingByID(offer.getListingID());
        User offerUser = userService.getUserById(offer.getUserID());
        List<OfferedItem> offeredItems = OfferedItemsService.getInstance().findOfferedItemsByOfferId(offer.getOfferID());//.findOfferedItemsByOfferIdAndListingId(offer.getOfferID(), listing.getListingId());

        HBox header = new HBox();
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        header.setSpacing(8);

        Label notificationType = new Label("NUOVA OFFERTA");
        notificationType.getStyleClass().add("notification-type");

        Label notificationTime = new Label(getTimeAgo(offer.getOfferDate()));
        notificationTime.getStyleClass().add("notification-time");

        header.getChildren().addAll(notificationType, notificationTime);

        String title = "Hai ricevuto un'offerta per '" + listing.getTitle() + "'";
        Label notificationTitle = new Label(title);
        notificationTitle.getStyleClass().add("notification-title");

        StringBuilder description = new StringBuilder((offerUser != null ? offerUser.getUsername() : "Un utente") +
                " ha offerto " + listing.getTitle() + " dal valore: " + formatAmount(offer.getAmount()) +
                ". Valore del prodotto: " + formatAmount(listing.getPrice().doubleValue()) + " ");

        //TODO aggiustqre il for che mostra la lista degli oggetti offerti
        for (OfferedItem offeredItem : offeredItems) {
            Listing offeredListing = listingService.getListingByID(offeredItem.getListingId());
            if (offeredListing != null) {
                description.append("\nOggetti offerti:");
                description.append("\n- ").append(offeredListing.getTitle());
            }
        }

        Label notificationDescription = new Label(description.toString());
        notificationDescription.getStyleClass().add("notification-description");

        HBox actions = new HBox();
        actions.setSpacing(8);
        actions.getStyleClass().add("notification-actions");

        Label offerAmount = new Label("Offerta: " + formatAmount(offer.getAmount()));
        offerAmount.getStyleClass().add("offer-amount");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button declineButton = new Button("Rifiuta");
        declineButton.getStyleClass().add("decline-button");
        int offerId = offer.getOfferID();
        declineButton.setOnAction(e -> handleDeclineOffer(e,offerId));

        Button acceptButton = new Button("Accetta");
        acceptButton.getStyleClass().add("accept-button");
        acceptButton.setOnAction(e -> handleAcceptOffer(e,offerId));

        Button counterButton = new Button("Controfferta");
        counterButton.getStyleClass().add("counter-button");
        counterButton.setOnAction(e -> handleCounterOffer(offerId));

        actions.getChildren().addAll(offerAmount, spacer, declineButton, acceptButton, counterButton);

        offerContent.getChildren().addAll(header, notificationTitle, notificationDescription, actions);

        content.getChildren().add(offerContent);
        card.getChildren().add(content);

        return card;
    }
    //Sempre generato fa claude
    private String getTimeAgo(LocalDate date) {
        long days = ChronoUnit.DAYS.between(date, LocalDate.now());

        if (days == 0) return "Oggi";
        if (days == 1) return "Ieri";
        if (days < 7) return days + " giorni fa";

        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
    //Claude anche qui
    private String formatAmount(double amount) {
        DecimalFormat df = new DecimalFormat("#,##0.00€");
        return df.format(amount);
    }

    @FXML
    void onHomeButtonClicked(ActionEvent event) {
        NavigationService.getInstance().navigateToMainView(event);
    }

    @FXML
    void onProfileButtonClicked() {
        System.out.println("Profile button clicked");
    }

    @FXML
    void onAcceptOfferClicked() {

    }

    private void handleAcceptOffer(ActionEvent event,int offerId) {
        try {
            offerDao.updateOfferStatus(offerId, ListingStatus.ACCEPTED);
            loadUserOffers();
            ValidationService.getInstance().showAlert(Alert.AlertType.INFORMATION, "Offerta accettata", "L'offerta è stata accettata con successo.");
            NavigationService.getInstance().navigateToMainView(event);
        } catch (Exception e) {
            e.printStackTrace();
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile accettare l'offerta: " + e.getMessage());
        }
    }

    @FXML
    void onDeclineOfferClicked() {
        // Called by static buttons in FXML, not used in dynamic generation
    }

    private void handleDeclineOffer(ActionEvent event,int offerId) {
        try {
            offerDao.updateOfferStatus(offerId, ListingStatus.REJECTED);
            loadUserOffers();
            ValidationService.getInstance().showAlert(Alert.AlertType.INFORMATION, "Offerta rifiutata", "L'offerta è stata rifiiutata con successo.");
            NavigationService.getInstance().navigateToMainView(event);
        } catch (Exception e) {
            e.printStackTrace();
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile accettare l'offerta: " + e.getMessage());
        }
    }

    @FXML
    void onCounterOfferClicked() {
        System.out.println("Counter button clicked");
    }

    private void handleCounterOffer(int offerId) {
        System.out.println("Funzionalità di controfferta in sviluppo");
    }

    @FXML
    void onAcceptSwapClicked() {
        // Logic for handling swaps
    }

    @FXML
    void onDeclineSwapClicked() {
        // Logic for handling swaps
    }
}