package com.uninaswap.controllers;

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
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;

public class NotificationsController implements Initializable {

    @FXML private Label notificationsCountLabel;
    @FXML private VBox notificationsContainer;

    private final OfferService offerService = OfferService.getInstance();
    private final InsertionService insertionService = InsertionService.getInstance();
    private final UserService userService = UserService.getInstance();
    private final TransactionService transactionService = TransactionService.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadUserOffers();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void onHomeButtonClicked(ActionEvent event) {
        NavigationService.getInstance().navigateToMainView(event);
    }
    private void loadUserOffers() throws Exception {
        this.notificationsContainer.getChildren().clear();
        List<Offer> offers = offerService.getOffersToCurrentUser();
        List<Offer> rejectedOffers = offerService.getRejectedOffersForCurrentUser();

        int totalNotifications = offers.size() + rejectedOffers.size();
        this.notificationsCountLabel.setText(totalNotifications + " notifiche");

        for (Offer offer : offers) {
            VBox offerCard = createOfferCard(offer);
            this.notificationsContainer.getChildren().add(offerCard);
        }

        for (Offer rejectedOffer : rejectedOffers) {
            VBox rejectedOfferCard = createRejectedOfferCard(rejectedOffer);
            this.notificationsContainer.getChildren().add(rejectedOfferCard);
        }
    }

    private VBox createOfferCard(Offer offer) throws SQLException {
        //Crea la card principale
        VBox card = new VBox();
        card.setStyle("-fx-effect: none !important; -fx-background-insets: 0; -fx-border-color: #e0e0e0; -fx-border-width: 1.5; -fx-border-radius: 4; -fx-padding: 12;");

        HBox content = new HBox();
        content.setAlignment(Pos.CENTER_LEFT);
        content.setSpacing(16);

        //Crea la vbox per il contenuto dell'offerta
        VBox offerContent = new VBox();
        offerContent.setSpacing(8);
        HBox.setHgrow(offerContent, Priority.ALWAYS);

        Insertion insertion = insertionService.getInsertionByID(offer.getInsertionID());
        User offerUser = userService.getUserById(offer.getUserID());
        List<OfferedItem> offeredItems = OfferedItemsService.getInstance().findOfferedItemsByOfferId(offer.getOfferID());

        String notificationType = getNotificationTypeFromInsertion(insertion.getType());

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(10);
        header.setPadding(new Insets(0, 0, 4, 0));

        Label notificationTypeLabel = new Label(notificationType);
        notificationTypeLabel.getStyleClass().add("notification-type");

        Label notificationTime = new Label(getTimeAgo(offer.getOfferDate()));
        notificationTime.getStyleClass().add("notification-time");

        header.getChildren().addAll(notificationTypeLabel, notificationTime);

        String title = createNotificationTitle(insertion.getType(), insertion);
        String description = createNotificationDescription(insertion.getType(), offer, insertion, offerUser, offeredItems);

        Label notificationTitle = new Label(title);
        notificationTitle.getStyleClass().add("notification-title");

        Label notificationDescription = new Label(description);
        notificationDescription.getStyleClass().add("notification-description");

        Region verticalSpacer = new Region();
        verticalSpacer.setPrefHeight(8);

        HBox actions = createActionButtons(offer, insertion.getType());

        offerContent.getChildren().addAll(header, notificationTitle, notificationDescription, verticalSpacer, actions);
        content.getChildren().add(offerContent);
        card.getChildren().add(content);

        return card;
    }

    private VBox createRejectedOfferCard(Offer offer) throws SQLException {
        //Crea la card principale per l'offerta rifiutata
        VBox card = new VBox();
        card.setStyle("-fx-effect: none !important; -fx-background-insets: 0; -fx-border-color: #ff6b6b; -fx-border-width: 1.5; -fx-border-radius: 4; -fx-padding: 12;");

        HBox content = new HBox();
        content.setAlignment(Pos.CENTER_LEFT);
        content.setSpacing(16);

        VBox offerContent = new VBox();
        offerContent.setSpacing(8);
        HBox.setHgrow(offerContent, Priority.ALWAYS);

        Insertion insertion = insertionService.getInsertionByID(offer.getInsertionID());

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

        Label titleLabel = new Label("La tua offerta per '" + insertion.getTitle() + "' è stata rifiutata");
        titleLabel.getStyleClass().add("notification-title");

        // Crea descrizione basata sul tipo di inserzione
        String description;
        switch (insertion.getType()) {
            case SALE:
                description = "Offerta: " + formatAmount(offer.getAmount()) +
                        " - Prezzo richiesto: " + formatAmount(insertion.getPrice().doubleValue());
                break;
            case EXCHANGE:
                description = "Il venditore ha rifiutato la tua proposta di scambio per gli oggetti offerti\n";
                break;
            case GIFT:
                description = offer.getMessage() != null && !offer.getMessage().isEmpty() ?
                        "Messaggio: \"" + offer.getMessage() + "\"" :
                        "Nessun messaggio";
                break;
            default:
                description = "";
        }

        Label descriptionLabel = new Label(description);
        descriptionLabel.getStyleClass().add("notification-description");

        Region verticalSpacer = new Region();
        verticalSpacer.setPrefHeight(8);

        HBox actions = createRejectedOfferActions(offer, insertion);

        offerContent.getChildren().addAll(header, titleLabel, descriptionLabel, verticalSpacer, actions);
        content.getChildren().add(offerContent);
        card.getChildren().add(content);

        return card;
    }
    private HBox createRejectedOfferActions(Offer offer, Insertion insertion) {
        //Crea la card principale per le azioni dell'offerta rifiutata
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
        retryButton.setOnAction(e -> handleRetryOffer(e, insertion));

        actions.getChildren().addAll(spacer, dismissButton, retryButton);

        return actions;
    }
    private void handleDismissOffer(int offerId) {
        try {
            offerService.updateOfferStatus(offerId, InsertionStatus.DISMISSED);
            loadUserOffers();
            ValidationService.getInstance().showAlert(Alert.AlertType.INFORMATION,
                    "Offerta rimossa", "L'offerta è stata rimossa dalla lista.");
        } catch (Exception e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore",
                    "Impossibile rimuovere l'offerta: " + e.getMessage());
        }
    }

    private void handleRetryOffer(ActionEvent event, Insertion insertion) {
        try {
            if(insertion.getType() == typeInsertion.SALE)
                NavigationService.getInstance().navigateToMakeOfferView(event, insertion);
            else if(insertion.getType() == typeInsertion.EXCHANGE)
                NavigationService.getInstance().navigateToExchangeView(event, insertion);
        } catch (Exception e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore",
                    "Impossibile aprire la pagina del prodotto: " + e.getMessage());
        }
    }

    private String getNotificationTypeFromInsertion(typeInsertion type) {
        return switch (type) {
            case GIFT -> "RICHIESTA REGALO";
            case EXCHANGE -> "PROPOSTA SCAMBIO";
            case SALE -> "NUOVA OFFERTA";
            case UNDEFINED -> null;
        };
    }

    private String createNotificationTitle(typeInsertion type, Insertion insertion) {
        return switch (type) {
            case GIFT -> "Qualcuno vuole il tuo regalo: '" + insertion.getTitle() + "'";
            case EXCHANGE -> "Proposta di scambio per '" + insertion.getTitle() + "'";
            case SALE -> "Hai ricevuto un'offerta per '" + insertion.getTitle() + "'";
            case UNDEFINED -> null;
        };
    }

    private String createNotificationDescription(typeInsertion type, Offer offer, Insertion insertion,
                                                 User offerUser, List<OfferedItem> offeredItems) {
        String username = offerUser != null ? offerUser.getUsername() : "Un utente";

        return switch (type) {
            case GIFT -> {
                StringBuilder desc = new StringBuilder(username + " vorrebbe ricevere il tuo regalo: " + insertion.getTitle());
                // Include il messaggio di motivazione per le richieste regalo
                if (offer.getMessage() != null && !offer.getMessage().isEmpty()) {
                    desc.append("\n\nMessaggio: \"").append(offer.getMessage()).append("\"");
                }
                yield desc.toString();
            }
            case EXCHANGE -> {
                StringBuilder desc = new StringBuilder(username + " propone uno scambio per " + insertion.getTitle());
                if (!offeredItems.isEmpty()) {
                    desc.append("\n\nOggetti offerti:");
                    for (OfferedItem item : offeredItems) {
                        try {
                            Insertion offeredInsertion = insertionService.getInsertionByID(item.getInsertionId());
                            if (offeredInsertion != null) {
                                desc.append("\n- ").append(offeredInsertion.getTitle());
                            }
                        } catch (SQLException e) {
                        }
                    }
                }
                yield desc.toString();
            }
            case SALE -> username + " ha offerto " + formatAmount(offer.getAmount()) +
                    " per " + insertion.getTitle() +
                    ". Prezzo richiesto: " + formatAmount(insertion.getPrice().doubleValue());
            case UNDEFINED -> null;
        };
    }

    private HBox createActionButtons(Offer offer, typeInsertion insertionType) {
        // Crea la card principale per le azioni dell'offerta
        HBox actions = new HBox();
        actions.setSpacing(10);
        actions.getStyleClass().add("notification-actions");
        actions.setPadding(new Insets(4, 0, 0, 0));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button declineButton = new Button("Rifiuta");
        declineButton.getStyleClass().add("decline-button");
        declineButton.setOnAction(e -> handleDeclineOffer(offer.getOfferID()));

        String acceptButtonText = switch (insertionType) {
            case GIFT -> "Regala";
            case EXCHANGE -> "Accetta Scambio";
            case SALE -> "Accetta";
            case UNDEFINED -> null;
        };

        Button acceptButton = new Button(acceptButtonText);
        acceptButton.getStyleClass().add("accept-button");
        acceptButton.setOnAction(e -> handleAcceptOffer(e, offer.getOfferID()));

        if (insertionType == typeInsertion.SALE) {
            Label offerAmount = new Label("Offerta: " + formatAmount(offer.getAmount()));
            offerAmount.getStyleClass().add("offer-amount");

            actions.getChildren().addAll(spacer, offerAmount, declineButton, acceptButton);

        } else if(insertionType == typeInsertion.EXCHANGE){
            actions.getChildren().addAll(spacer, declineButton, acceptButton);

        }else{
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

    private void handleAcceptOffer(ActionEvent event, int offerId) {
        try {
            Offer offer = offerService.getOfferById(offerId);
            Insertion insertion = insertionService.getInsertionByID(offer.getInsertionID());
            User buyer = userService.getUserById(offer.getUserID());

            switch (insertion.getType()) {
                case SALE:
                    transactionService.recordSale(insertion, offer, buyer);
                    ValidationService.getInstance().showAlert(Alert.AlertType.INFORMATION,
                            "Vendita completata", "La vendita è stata completata con successo.");
                    break;
                case EXCHANGE:
                    transactionService.recordExchange(insertion, offer, buyer);
                    ValidationService.getInstance().showAlert(Alert.AlertType.INFORMATION,
                            "Scambio accettato", "Lo scambio è stato accettato con successo.");
                    break;
                case GIFT:
                    transactionService.recordGift(insertion, offer, buyer);
                    ValidationService.getInstance().showAlert(Alert.AlertType.INFORMATION,
                            "Regalo accettato", "Il regalo è stato accettato con successo.");
                    break;
            }

            offerService.acceptOffer(offerId);
            insertionService.updateInsertionStatus(insertion.getInsertionID(), InsertionStatus.SOLD);


            loadUserOffers();
        } catch (Exception e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore",
                    "Impossibile accettare l'offerta: " + e.getMessage());
        }
    }

    private void handleDeclineOffer(int offerId) {
        try {
            List<OfferedItem> offeredItems = OfferedItemsService.getInstance().findOfferedItemsByOfferId(offerId);
            for (OfferedItem item : offeredItems) {
                Insertion insertion = insertionService.getInsertionByID(item.getInsertionId());
                insertion.setStatus(InsertionStatus.AVAILABLE);
                insertionService.updateInsertion(insertion);
            }

            offerService.rejectOffer(offerId);
            loadUserOffers();
            ValidationService.getInstance().showAlert(Alert.AlertType.INFORMATION,
                    "Offerta rifiutata", "L'offerta è stata rifiutata con successo.");
        } catch (Exception e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore",
                    "Impossibile rifiutare l'offerta: " + e.getMessage());
        }
    }

}