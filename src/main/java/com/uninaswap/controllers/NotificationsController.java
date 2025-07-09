package com.uninaswap.controllers;

import com.uninaswap.dao.OfferDao;
import com.uninaswap.dao.OfferDaoImpl;
import com.uninaswap.model.*;
import com.uninaswap.services.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
    private final OfferService offerService = OfferService.getInstance();
    private final ListingService listingService = ListingService.getInstance();
    private final UserService userService = UserService.getInstance();
    private final TransactionService transactionService = TransactionService.getInstance();

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
        List<Offer> rejectedOffers = offerDao.findRejectedOffersForCurrentUser();

        int totalNotifications = offers.size() + rejectedOffers.size();
        notificationsCountLabel.setText(totalNotifications + " notifiche");

        for (Offer offer : offers) {
            VBox offerCard = createOfferCard(offer);
            notificationsContainer.getChildren().add(offerCard);
        }

        for (Offer rejectedOffer : rejectedOffers) {
            VBox rejectedOfferCard = createRejectedOfferCard(rejectedOffer);
            notificationsContainer.getChildren().add(rejectedOfferCard);
        }
    }

    private VBox createOfferCard(Offer offer) throws SQLException {
        VBox card = new VBox();
        card.setStyle("-fx-effect: none !important; -fx-background-insets: 0; -fx-border-color: #e0e0e0; -fx-border-width: 1.5; -fx-border-radius: 4; -fx-padding: 12;");

        HBox content = new HBox();
        content.setAlignment(Pos.CENTER_LEFT);
        content.setSpacing(16);

        VBox offerContent = new VBox();
        offerContent.setSpacing(8);
        HBox.setHgrow(offerContent, Priority.ALWAYS);

        Listing listing = listingService.getListingByID(offer.getListingID());
        User offerUser = userService.getUserById(offer.getUserID());
        List<OfferedItem> offeredItems = OfferedItemsService.getInstance()
                .findOfferedItemsByOfferId(offer.getOfferID());

        // Usa il type del listing per determinare il tipo di notifica
        String notificationType = getNotificationTypeFromListing(listing.getType());

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(10);
        header.setPadding(new Insets(0, 0, 4, 0));

        Label notificationTypeLabel = new Label(notificationType);
        notificationTypeLabel.getStyleClass().add("notification-type");

        Label notificationTime = new Label(getTimeAgo(offer.getOfferDate()));
        notificationTime.getStyleClass().add("notification-time");

        header.getChildren().addAll(notificationTypeLabel, notificationTime);

        // Crea titolo e descrizione basati sul tipo di listing
        String title = createNotificationTitle(listing.getType(), listing);
        String description = createNotificationDescription(listing.getType(), offer, listing, offerUser, offeredItems);

        Label notificationTitle = new Label(title);
        notificationTitle.getStyleClass().add("notification-title");

        Label notificationDescription = new Label(description);
        notificationDescription.getStyleClass().add("notification-description");

        Region verticalSpacer = new Region();
        verticalSpacer.setPrefHeight(8);

        HBox actions = createActionButtons(offer, listing.getType());

        offerContent.getChildren().addAll(header, notificationTitle, notificationDescription, verticalSpacer, actions);
        content.getChildren().add(offerContent);
        card.getChildren().add(content);

        return card;
    }

    private VBox createRejectedOfferCard(Offer offer) throws SQLException {
        VBox card = new VBox();
        card.setStyle("-fx-effect: none !important; -fx-background-insets: 0; -fx-border-color: #ff6b6b; -fx-border-width: 1.5; -fx-border-radius: 4; -fx-padding: 12;");

        HBox content = new HBox();
        content.setAlignment(Pos.CENTER_LEFT);
        content.setSpacing(16);

        VBox offerContent = new VBox();
        offerContent.setSpacing(8);
        HBox.setHgrow(offerContent, Priority.ALWAYS);

        Listing listing = listingService.getListingByID(offer.getListingID());

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(10);
        header.setPadding(new Insets(0, 0, 4, 0));

        Label statusLabel = new Label("OFFERTA RIFIUTATA");
        statusLabel.getStyleClass().add("notification-type");
        statusLabel.setStyle("-fx-text-fill: #ff6b6b;");

        Label dateLabel = new Label(getTimeAgo(offer.getOfferDate()));
        dateLabel.getStyleClass().add("notification-time");

        header.getChildren().addAll(statusLabel, dateLabel);

        Label titleLabel = new Label("La tua offerta per '" + listing.getTitle() + "' è stata rifiutata");
        titleLabel.getStyleClass().add("notification-title");

        Label descriptionLabel = new Label("Offerta: " + formatAmount(offer.getAmount()) +
                " - Prezzo richiesto: " + formatAmount(listing.getPrice().doubleValue()));
        descriptionLabel.getStyleClass().add("notification-description");

        Region verticalSpacer = new Region();
        verticalSpacer.setPrefHeight(8);

        HBox actions = createRejectedOfferActions(offer, listing);

        offerContent.getChildren().addAll(header, titleLabel, descriptionLabel, verticalSpacer, actions);
        content.getChildren().add(offerContent);
        card.getChildren().add(content);

        return card;
    }
    private HBox createRejectedOfferActions(Offer offer, Listing listing) {
        HBox actions = new HBox();
        actions.setSpacing(10);
        actions.getStyleClass().add("notification-actions");
        actions.setPadding(new Insets(4, 0, 0, 0));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button dismissButton = new Button("Rimuovi");
        dismissButton.getStyleClass().add("decline-button");
        dismissButton.setOnAction(e -> handleDismissOffer(offer.getOfferID()));

        Button retryButton = new Button("Nuova Offerta");
        retryButton.getStyleClass().add("accept-button");
        retryButton.setOnAction(e -> handleRetryOffer(e, listing));

        actions.getChildren().addAll(spacer, dismissButton, retryButton);

        return actions;
    }
    private void handleDismissOffer(int offerId) {
        try {
            offerDao.deleteOffer(offerId);
            loadUserOffers();
            ValidationService.getInstance().showAlert(Alert.AlertType.INFORMATION,
                    "Offerta rimossa", "L'offerta è stata rimossa dalla lista.");
        } catch (Exception e) {
            e.printStackTrace();
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore",
                    "Impossibile rimuovere l'offerta: " + e.getMessage());
        }
    }

    private void handleRetryOffer(ActionEvent event, Listing listing) {
        try {
            // Naviga alla pagina di dettaglio del prodotto per fare una nuova offerta
            NavigationService.getInstance().navigateToMakeOfferView(event, listing);
        } catch (Exception e) {
            e.printStackTrace();
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore",
                    "Impossibile aprire la pagina del prodotto: " + e.getMessage());
        }
    }

    private String getNotificationTypeFromListing(typeListing type) {
        return switch (type) {
            case GIFT -> "RICHIESTA REGALO";
            case EXCHANGE -> "PROPOSTA SCAMBIO";
            case SALE -> "NUOVA OFFERTA";
            case UNDEFINED -> null;
        };
    }

    private String createNotificationTitle(typeListing type, Listing listing) {
        return switch (type) {
            case GIFT -> "Qualcuno vuole il tuo regalo: '" + listing.getTitle() + "'";
            case EXCHANGE -> "Proposta di scambio per '" + listing.getTitle() + "'";
            case SALE -> "Hai ricevuto un'offerta per '" + listing.getTitle() + "'";
            case UNDEFINED -> null;
        };
    }

    private String createNotificationDescription(typeListing type, Offer offer, Listing listing,
                                                 User offerUser, List<OfferedItem> offeredItems) {
        String username = offerUser != null ? offerUser.getUsername() : "Un utente";

        return switch (type) {
            case GIFT -> username + " vorrebbe ricevere il tuo regalo: " + listing.getTitle();

            case EXCHANGE -> {
                StringBuilder desc = new StringBuilder(username + " propone uno scambio per " + listing.getTitle());
                if (!offeredItems.isEmpty()) {
                    desc.append("\n\nOggetti offerti:");
                    for (OfferedItem item : offeredItems) {
                        try {
                            Listing offeredListing = listingService.getListingByID(item.getListingId());
                            if (offeredListing != null) {
                                desc.append("\n- ").append(offeredListing.getTitle());
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
                yield desc.toString();
            }

            case SALE -> username + " ha offerto " + formatAmount(offer.getAmount()) +
                    " per " + listing.getTitle() +
                    ". Prezzo richiesto: " + formatAmount(listing.getPrice().doubleValue());
            case UNDEFINED -> null;
        };
    }

    private HBox createActionButtons(Offer offer, typeListing listingType) {
        HBox actions = new HBox();
        actions.setSpacing(10);
        actions.getStyleClass().add("notification-actions");
        actions.setPadding(new Insets(4, 0, 0, 0));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button declineButton = new Button("Rifiuta");
        declineButton.getStyleClass().add("decline-button");
        declineButton.setOnAction(e -> handleDeclineOffer(e, offer.getOfferID()));

        String acceptButtonText = switch (listingType) {
            case GIFT -> "Regala";
            case EXCHANGE -> "Accetta Scambio";
            case SALE -> "Accetta";
            case UNDEFINED -> null;
        };

        Button acceptButton = new Button(acceptButtonText);
        acceptButton.getStyleClass().add("accept-button");
        acceptButton.setOnAction(e -> handleAcceptOffer(e, offer.getOfferID()));

        if (listingType != typeListing.GIFT) {
            Label offerAmount = new Label("Offerta: " + formatAmount(offer.getAmount()));
            offerAmount.getStyleClass().add("offer-amount");
            actions.getChildren().addAll(offerAmount, spacer, declineButton, acceptButton);
        } else {
            actions.getChildren().addAll(spacer, declineButton, acceptButton);
        }

        return actions;
    }

    private String getTimeAgo(LocalDate date) {
        long days = ChronoUnit.DAYS.between(date, LocalDate.now());

        if (days == 0) return "Oggi";
        if (days == 1) return "Ieri";
        if (days < 7) return days + " giorni fa";

        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

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

    private void handleAcceptOffer(ActionEvent event, int offerId) {
        try {
            Offer offer = offerDao.findOfferById(offerId);
            Listing listing = listingService.getListingByID(offer.getListingID());
            User buyer = userService.getUserById(offer.getUserID());

            // Usa il type del listing per determinare il tipo di transazione
            switch (listing.getType()) {
                case SALE:
                    // Vendita normale
                    transactionService.recordSale(listing, offer, buyer);
                    ValidationService.getInstance().showAlert(Alert.AlertType.INFORMATION,
                            "Vendita completata", "La vendita è stata completata con successo.");
                    break;

                case EXCHANGE:
                    // Scambio
                    transactionService.recordExchange(listing, offer, buyer);
                    ValidationService.getInstance().showAlert(Alert.AlertType.INFORMATION,
                            "Scambio accettato", "Lo scambio è stato accettato con successo.");
                    break;

                case GIFT:
                    // Regalo
                    transactionService.recordGift(listing, offer, buyer);
                    ValidationService.getInstance().showAlert(Alert.AlertType.INFORMATION,
                            "Regalo accettato", "Il regalo è stato accettato con successo.");
                    break;
            }

            // Aggiorna lo status dell'offerta e del listing
            offerDao.updateOfferStatus(offerId, ListingStatus.ACCEPTED);
            listingService.updateListingStatus(listing.getListingId(), ListingStatus.SOLD);

            loadUserOffers();
            //NavigationService.getInstance().navigateToMainView(event);

        } catch (Exception e) {
            e.printStackTrace();
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore",
                    "Impossibile accettare l'offerta: " + e.getMessage());
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
            //NavigationService.getInstance().navigateToMainView(event);
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