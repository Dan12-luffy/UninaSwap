package com.uninaswap.controllers;

import com.uninaswap.dao.UserDaoImpl;
import com.uninaswap.model.*;
import com.uninaswap.services.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class ExchangeController{

    @FXML private Button helpButton;
    @FXML private ImageView logoImage;
    @FXML private Button backButton;
    @FXML private VBox yourProductsContainer;
    @FXML private Label selectedCountLabel;
    @FXML private Label totalValueLabel;
    @FXML private ImageView desiredProductImageView;
    @FXML private Label desiredProductNameLabel;
    @FXML private Label desiredProductDescriptionLabel;
    @FXML private Label desiredProductPriceLabel;
    @FXML private Label desiredProductOwnerLabel;
    @FXML private Label yourTotalValueLabel;
    @FXML private Label desiredValueLabel;
    @FXML private Label differenceLabel;
    @FXML private VBox differenceMessageContainer;
    @FXML private Label differenceMessageLabel;
    @FXML private Button cancelExchangeButton;
    @FXML private Button previewExchangeButton;
    @FXML private Button confirmExchangeButton;

    private Offer currentOffer;
    private Insertion desiredProduct;
    private final List<Insertion> selectedInsertions = new ArrayList<>();
    private final InsertionService insertionService = InsertionService.getInstance();
    private final OfferService offerService = OfferService.getInstance();
    private final OfferedItemsService offeredItemsService = OfferedItemsService.getInstance();

   @FXML
    public void initialize() {
        loadUserListings();
    }

    @FXML
    private void goBack(ActionEvent event) {
        NavigationService.getInstance().navigateToMainView(event);
    }

    @FXML
    private void cancelExchange(ActionEvent event) {
       ValidationService.getInstance().showCancelExchangeMessage();
       NavigationService.getInstance().navigateToMainView(event);
    }

    @FXML
    private void confirmExchange(ActionEvent event) {
       if(currentOffer != null) {
           counterOffer(event);
           return;
       }
        double differenceValue = Double.parseDouble(this.differenceLabel.getText().replace("Differenza: €", ""));
        String confirmMessage;

        if(differenceValue > 0) {
            confirmMessage = "Sei sicuro di voler procedere con lo scambio? Il valore dei tuoi prodotti è superiore a quello del prodotto desiderato.";
        } else if(differenceValue < 0) {
            confirmMessage = "Sei sicuro di voler procedere con lo scambio? Il valore dei tuoi prodotti è inferiore a quello del prodotto desiderato.";
        } else {
            confirmMessage = "Sei sicuro di voler procedere con lo scambio?";
        }

        confirmAction(confirmMessage).ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Offer offer = new Offer(this.desiredProduct.getInsertionID(), UserSession.getInstance().getCurrentUserId(), 0, null,typeOffer.EXCHANGE_OFFER, InsertionStatus.PENDING, LocalDate.now());
                    int offerId = offerService.createOffer(offer);
                    if (offerId > 0) {
                        for (Insertion insertion : selectedInsertions) {
                            OfferedItem offeredItem = new OfferedItem(offerId, insertion.getInsertionID());
                            offeredItemsService.createOfferedItem(offeredItem);
                        }
                        ValidationService.getInstance().showOfferProposalSuccess();
                        NavigationService.getInstance().navigateToMainView(event);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile completare lo scambio: " + e.getMessage());
                }
            }
        });
    }

    private void updateCalculations(Insertion insertion, boolean isSelected) {
        String valueText = this.totalValueLabel.getText().replace("€", "");
        double totalValue = Double.parseDouble(valueText);

        String countText = this.selectedCountLabel.getText().replaceAll("[^0-9]", "");
        int count = Integer.parseInt(countText);

        if(isSelected) {
            totalValue += insertion.getPrice().doubleValue();
            count += 1;
        } else {
            totalValue -= insertion.getPrice().doubleValue();
            count -= 1;
        }

        selectedCountLabel.setText(String.valueOf(count));
        totalValueLabel.setText("€" + String.format("%.2f", totalValue));
        yourTotalValueLabel.setText("Il tuo valore totale: €" + String.format("%.2f", totalValue));

        double difference = this.desiredProduct.getPrice().doubleValue() - totalValue;
        differenceLabel.setText("Differenza: €" + String.format("%.2f", difference));

        updateDifferenceMessage();
        toggleConfirmButton();
    }
    private void updateDifferenceMessage() {
        // Update the message explaining the exchange difference
    }

    private void toggleConfirmButton() {
        // Enable/disable the confirm button based on selection status
    }

    private HBox createListingCard(Insertion insertion) {
        HBox card = new HBox(15);
        card.setPrefWidth(this.yourProductsContainer.getPrefWidth() - 20);
        card.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: white;");
        card.setPrefHeight(100);

        card.setUserData(false);

        card.setOnMouseEntered(e -> {
            if (!((boolean) card.getUserData())) {
                card.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: #f8f8f8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
            }
        });
        card.setOnMouseExited(e -> {
            if (!((boolean) card.getUserData())) {
                card.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: white;");
            }
        });

        // Product image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 0); -fx-background-radius: 3;");

        String defaultImagePath = "/com/uninaswap/images/default_image.png";
        try {
            File imageFile = new File(insertion.getImageUrl());
            imageView.setImage(new Image(imageFile.toURI().toString()));
        } catch (Exception e) {
            imageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(defaultImagePath))));
        }

        VBox textContent = new VBox(5);
        textContent.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        HBox.setHgrow(textContent, javafx.scene.layout.Priority.ALWAYS);

        Label titleLabel = new Label(insertion.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        titleLabel.setWrapText(true);

        String priceText;
        if (insertion.getType() == typeInsertion.GIFT) {
            priceText = "Gratis";
        }
        else {
            priceText = String.format("€%.2f", insertion.getPrice());
        }

        Label priceLabel = new Label(priceText);
        priceLabel.setStyle("-fx-font-size: 14px;");

        textContent.getChildren().addAll(titleLabel, priceLabel);

        card.setOnMouseClicked(event -> {
            boolean currentState = (boolean)card.getUserData();
            boolean newState = !currentState;
            card.setUserData(newState);

            if (newState) {
                card.setStyle("-fx-padding: 10; -fx-border-color: #4CAF50; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: #f1f8e9;");
                selectedInsertions.add(insertion); // Add to selected listings
            } else {
                card.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: white;");
                selectedInsertions.remove(insertion); // Remove from selected listings
            }
            updateCalculations(insertion, newState);
        });

        VBox actionBox = new VBox(5);
        actionBox.setAlignment(javafx.geometry.Pos.CENTER);

        card.getChildren().addAll(imageView, textContent, actionBox);
        return card;
    }

    private void loadUserListings() {
        try {
            List<Insertion> insertions = insertionService.getCurrentUserAvailableInsertions();
            this.yourProductsContainer.getChildren().clear();

            if (insertions.isEmpty()) {
                Label emptyLabel = new Label("Nessun annuncio trovato");
                this.yourProductsContainer.getChildren().add(emptyLabel);
            } else {
                for (Insertion insertion : insertions) {
                    this.yourProductsContainer.getChildren().add(createListingCard(insertion));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Errore nel caricamento degli annunci");
            yourProductsContainer.getChildren().add(errorLabel);
        }
    }
    private void counterOffer(ActionEvent actionEvent) {
        try {
            if (this.currentOffer == null) {
                ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Nessuna offerta da controbattere.");
                return;
            }

            if (selectedInsertions.isEmpty()) {
                ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Seleziona almeno un prodotto per la controfferta.");
                return;
            }

            List<OfferedItem> oldOfferedItems = OfferedItemsService.getInstance().findOfferedItemsByOfferId(this.currentOffer.getOfferID());

            // Elimina l'offerta precedente e libera gli item
            for (OfferedItem item : oldOfferedItems) {
                Insertion insertion = insertionService.getInsertionByID(item.getInsertionId());
                insertion.setStatus(InsertionStatus.AVAILABLE);
                insertionService.updateInsertion(insertion);
            }
            offerService.deleteOffer(this.currentOffer.getOfferID());

            // Crea la controfferta per il prodotto dell'altro utente che hai selezionato
            Insertion targetInsertion = selectedInsertions.get(0); // Prodotto dell'altro utente che vuoi

            // CORREZIONE: Temporaneamente cambia il tipo del prodotto target per permettere la controfferta
            typeInsertion originalType = targetInsertion.getType();
            targetInsertion.setType(typeInsertion.EXCHANGE);
            insertionService.updateInsertion(targetInsertion);

            Offer counterOffer = new Offer(
                    targetInsertion.getInsertionID(), // Prodotto dell'altro utente che vuoi
                    UserSession.getInstance().getCurrentUserId(), // Tu stai facendo la controfferta
                    0,
                    null,
                    typeOffer.EXCHANGE_OFFER,
                    InsertionStatus.PENDING,
                    LocalDate.now()
            );

            int newOfferId = offerService.createOffer(counterOffer);

            if (newOfferId > 0) {
                // Aggiungi il TUO prodotto originale come controfferta
                OfferedItem offeredItem = new OfferedItem(newOfferId, this.desiredProduct.getInsertionID());
                OfferedItemsService.getInstance().createOfferedItem(offeredItem);

                // Marca il prodotto dell'altro utente come PENDING
                targetInsertion.setStatus(InsertionStatus.PENDING);
                insertionService.updateInsertion(targetInsertion);

                // Marca anche il TUO prodotto originale come PENDING
                this.desiredProduct.setStatus(InsertionStatus.PENDING);
                insertionService.updateInsertion(this.desiredProduct);

                ValidationService.getInstance().showAlert(Alert.AlertType.INFORMATION, "Controfferta inviata", "La controfferta è stata inviata con successo.");
                NavigationService.getInstance().navigateToMainView(actionEvent);
            } else {
                // Se la creazione dell'offerta fallisce, ripristina il tipo originale
                targetInsertion.setType(originalType);
                insertionService.updateInsertion(targetInsertion);
                ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile creare la controfferta.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile inviare la controfferta: " + e.getMessage());
        }
    }
    /*private void counterOffer(ActionEvent actionEvent) {
        try {
            if (this.currentOffer == null) {
                ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Nessuna offerta da controbattere.");
                return;
            }

            if (selectedInsertions.isEmpty()) {
                ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Seleziona almeno un prodotto per la controfferta.");
                return;
            }

            List<OfferedItem> oldOfferedItems = OfferedItemsService.getInstance().findOfferedItemsByOfferId(this.currentOffer.getOfferID());
            int originalOfferUserId = this.currentOffer.getUserID();

            // Elimina l'offerta precedente e libera gli item
            for (OfferedItem item : oldOfferedItems) {
                Insertion insertion = insertionService.getInsertionByID(item.getInsertionId());
                insertion.setStatus(InsertionStatus.AVAILABLE);
                insertionService.updateInsertion(insertion);
            }
            offerService.deleteOffer(this.currentOffer.getOfferID());

            // CORREZIONE: Crea la controfferta per il prodotto dell'altro utente che hai selezionato
            Insertion targetInsertion = selectedInsertions.get(0); // Il prodotto dell'altro utente che vuoi

            Offer counterOffer = new Offer(
                    targetInsertion.getInsertionID(), // Prodotto dell'altro utente che vuoi
                    UserSession.getInstance().getCurrentUserId(), // Tu stai facendo la controfferta
                    0,
                    null,
                    typeOffer.EXCHANGE_OFFER,
                    InsertionStatus.PENDING,
                    LocalDate.now()
            );

            int newOfferId = offerService.createOffer(counterOffer);

            if (newOfferId > 0) {
                // Aggiungi il TUO prodotto originale come controfferta
                OfferedItem offeredItem = new OfferedItem(newOfferId, this.desiredProduct.getInsertionID());
                OfferedItemsService.getInstance().createOfferedItem(offeredItem);

                ValidationService.getInstance().showAlert(Alert.AlertType.INFORMATION, "Controfferta inviata", "La controfferta è stata inviata con successo.");
                NavigationService.getInstance().navigateToMainView(actionEvent);
            } else {
                ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile creare la controfferta.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile inviare la controfferta: " + e.getMessage());
        }
    }*/
    /*private void counterOffer(ActionEvent actionEvent) {
        try {
            if (this.currentOffer == null) {
                ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Nessuna offerta da controbattere.");
                return;
            }

            if (selectedInsertions.isEmpty()) {
                ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Seleziona almeno un prodotto per la controfferta.");
                return;
            }

            List<OfferedItem> oldOfferedItems = OfferedItemsService.getInstance().findOfferedItemsByOfferId(this.currentOffer.getOfferID());

            // Elimina l'offerta precedente e libera gli item
            for (OfferedItem item : oldOfferedItems) {
                Insertion insertion = insertionService.getInsertionByID(item.getInsertionId());
                insertion.setStatus(InsertionStatus.AVAILABLE);
                insertionService.updateInsertion(insertion);
            }
            offerService.deleteOffer(this.currentOffer.getOfferID());

            // CORREZIONE: selectedInsertions ora contiene i prodotti dell'altro utente
            // Crea la controfferta per il prodotto dell'altro utente che hai selezionato
            Insertion targetInsertion = selectedInsertions.get(0); // Prodotto dell'altro utente che vuoi

            Offer counterOffer = new Offer(
                    targetInsertion.getInsertionID(), // Prodotto dell'altro utente che vuoi
                    UserSession.getInstance().getCurrentUserId(), // Tu stai facendo la controfferta
                    0,
                    null,
                    typeOffer.EXCHANGE_OFFER,
                    InsertionStatus.PENDING,
                    LocalDate.now()
            );

            int newOfferId = offerService.createOffer(counterOffer);

            if (newOfferId > 0) {
                // Aggiungi il TUO prodotto originale come controfferta
                OfferedItem offeredItem = new OfferedItem(newOfferId, this.desiredProduct.getInsertionID());
                OfferedItemsService.getInstance().createOfferedItem(offeredItem);

                ValidationService.getInstance().showAlert(Alert.AlertType.INFORMATION, "Controfferta inviata", "La controfferta è stata inviata con successo.");
                NavigationService.getInstance().navigateToMainView(actionEvent);
            } else {
                ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile creare la controfferta.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile inviare la controfferta: " + e.getMessage());
        }
    }*/
    private void loadProductsForCounterOffer(int originalOfferId) throws Exception {
        yourProductsContainer.getChildren().clear();

        // 1. Trova l'utente che ha fatto l'offerta originale
        Offer originalOffer = offerService.getOfferById(originalOfferId);
        int originalOfferUserId = originalOffer.getUserID();

        // 2. Carica i prodotti dell'utente che ha fatto l'offerta originale che erano stati offerti
        List<OfferedItem> originalOfferedItems = OfferedItemsService.getInstance().findOfferedItemsByOfferId(originalOfferId);

        // 3. Carica tutti i prodotti disponibili dell'utente che ha fatto l'offerta originale
        List<Insertion> offererAvailableInsertions = insertionService.getAvailableInsertionsByUserId(originalOfferUserId);

        // 4. Crea un Set per evitare duplicati
        Set<Integer> addedInsertions = new HashSet<>();

        // Aggiungi prima i prodotti che erano stati offerti originalmente
        for (OfferedItem item : originalOfferedItems) {
            Insertion insertion = insertionService.getInsertionByID(item.getInsertionId());
            if (insertion != null && insertion.getUserId() == originalOfferUserId && addedInsertions.add(insertion.getInsertionID())) {
                HBox card = createListingCard(insertion);
                yourProductsContainer.getChildren().add(card);
            }
        }

        // Aggiungi poi tutti gli altri prodotti disponibili dell'utente che ha fatto l'offerta
        for (Insertion insertion : offererAvailableInsertions) {
            if (addedInsertions.add(insertion.getInsertionID())) {
                HBox card = createListingCard(insertion);
                yourProductsContainer.getChildren().add(card);
            }
        }

        // Se non ci sono prodotti da mostrare
        if (yourProductsContainer.getChildren().isEmpty()) {
            Label emptyLabel = new Label("Nessun prodotto disponibile dall'utente");
            yourProductsContainer.getChildren().add(emptyLabel);
        }
    }

    public void loadOffer(Offer offer) {
        this.currentOffer = offer;

        if (offer != null) {
            try {
                // Carica i prodotti dell'utente che ha fatto l'offerta originale
                loadProductsForCounterOffer(offer.getOfferID());
            } catch (Exception e) {
                e.printStackTrace();
                ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore",
                        "Impossibile caricare i prodotti: " + e.getMessage());
            }
        }
    }
    public void loadDesiredProduct(Insertion insertion) {
        if (insertion == null) {
            return;
        }
        this.desiredProduct = insertion;
        this.desiredProductNameLabel.setText(insertion.getTitle());
        this.desiredProductDescriptionLabel.setText(insertion.getDescription());
        this.desiredProductPriceLabel.setText("€" + insertion.getPrice().toString());
        this.desiredValueLabel.setText("€" + insertion.getPrice().toString());
        this.desiredProductOwnerLabel.setText("Utente: " + new UserDaoImpl().findFullNameFromID(insertion.getUserId()));

        String defaultImagePath = "/com/uninaswap/images/default_image.png";
        try {
            File imageFile = new File(insertion.getImageUrl());
            this.desiredProductImageView.setImage(new Image(imageFile.toURI().toString()));
        } catch (Exception e) {
            System.out.println("Impossibile caricare l'immagine: " + e.getMessage());
            this.desiredProductImageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(defaultImagePath))));
        }
    }
    private Optional<ButtonType> confirmAction(String message) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Conferma scambio");
        confirmDialog.setHeaderText("Conferma operazione");
        confirmDialog.setContentText(message);

        return confirmDialog.showAndWait();
   }
    private void loadUserListings(int userId) throws SQLException {
        List<Insertion> insertions;

        if (userId == UserSession.getInstance().getCurrentUserId()) {
            insertions = insertionService.getCurrentUserAvailableInsertions();
        } else {
            insertions = insertionService.getAvailableInsertionsByUserId(userId);
        }

        yourProductsContainer.getChildren().clear();

        if (insertions.isEmpty()) {
            Label emptyLabel = new Label("Nessun annuncio trovato");
            yourProductsContainer.getChildren().add(emptyLabel);
        } else {
            for (Insertion insertion : insertions) {
                yourProductsContainer.getChildren().add(createListingCard(insertion));
            }
        }
    }
}