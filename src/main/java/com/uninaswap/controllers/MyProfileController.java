package com.uninaswap.controllers;

import com.uninaswap.model.*;
import com.uninaswap.services.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


public class MyProfileController {
    @FXML private Label userNameLabel;
    @FXML private Label totalAdsLabel;
    @FXML private Label activeSalesLabel;
    @FXML private Label completedSalesLabel;
    @FXML private Label totalEarningsLabel;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<String> facultyComboBox;
    @FXML private VBox userAdsContainer;
    @FXML private TextField currentUsernameField;
    @FXML private TextField newUsernameField;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label acceptedOffersStat;
    @FXML private Label pendingOffersStat;
    @FXML private Label rejectedOffersStat;
    @FXML private PieChart offerTypesPieChart;
    @FXML private BarChart<String, Number> acceptedOffersBarChart;
    @FXML private Label avgPriceLabel;
    @FXML private Label minPriceLabel;
    @FXML private Label maxPriceLabel;
    @FXML private VBox pendingOffersContainer;
    @FXML private VBox completedOperationsContainer;

    private final UserService userService = UserService.getInstance();
    private final InsertionService insertionService = InsertionService.getInstance();
    private final ValidationService validationService = ValidationService.getInstance();
    private final OfferService offerService = OfferService.getInstance();


    @FXML
    public void initialize() {

        User currentUser = userService.getUserById(UserSession.getInstance().getCurrentUserId());
        this.userNameLabel.setText(currentUser.getUsername());
        this.firstNameField.setText(currentUser.getFirst_name());
        this.lastNameField.setText(currentUser.getLast_name());
        this.currentUsernameField.setText(currentUser.getUsername());
        this.newUsernameField.setTextFormatter(createTextFormatter(30));
        this.newPasswordField.setTextFormatter(createTextFormatter(30));
        this.currentPasswordField.setTextFormatter(createTextFormatter(30));
        this.confirmPasswordField.setTextFormatter(createTextFormatter(30));

        for (Faculty faculty : Faculty.values()) {
            this.facultyComboBox.getItems().add(faculty.getFacultyName());
        }
        this.facultyComboBox.setValue(currentUser.getFaculty());
        setTotalAdsLabel();
        setStatisticsSection();
        loadUserInsertions();
        setPieChartAndBarChart();
        loadAcceptedSaleOfferStatistics();
        loadPendingOffers();
        loadCompletedOperations();
    }

    private void loadUserInsertions() {
        try {
            List<Insertion> insertions = insertionService.getCurrentUserInsertions();

            // Clear existing content
            this.userAdsContainer.getChildren().clear();

            if (insertions.isEmpty()) {
                Label emptyLabel = new Label("Nessun annuncio trovato");
                this.userAdsContainer.getChildren().add(emptyLabel);
            } else {
                for (Insertion insertion : insertions) {
                    this.userAdsContainer.getChildren().add(createInsertionCard(insertion));
                }
            }
        } catch (SQLException e) {
            Label errorLabel = new Label("Errore nel caricamento degli annunci" + e.getMessage());
            userAdsContainer.getChildren().add(errorLabel);
        }
    }
    private void setStatisticsSection() {
        try {
            List<Insertion> insertions = insertionService.getCurrentUserInsertions();

            double minPrice = Double.MAX_VALUE;
            double maxPrice = 0;
            double sumPrice = 0.0;
            int priceCount = 0;

            if (!insertions.isEmpty()) {
                for (Insertion insertion : insertions) {
                    if (insertion.getStatus() == InsertionStatus.SOLD) {
                        this.acceptedOffersStat.setText(String.valueOf(Integer.parseInt(this.acceptedOffersStat.getText()) + 1));
                    } else if (insertion.getStatus() == InsertionStatus.PENDING) {
                        this.pendingOffersStat.setText(String.valueOf(Integer.parseInt(this.pendingOffersStat.getText()) + 1));
                    } else if (insertion.getStatus() == InsertionStatus.REJECTED) {
                        this.rejectedOffersStat.setText(String.valueOf(Integer.parseInt(this.rejectedOffersStat.getText()) + 1));
                    }

                    // Safely handle price calculations - check if price is not null
                    if (insertion.getPrice() != null) {
                        double price = insertion.getPrice().doubleValue();
                        if (price < minPrice) {
                            minPrice = price;
                        }
                        if (price > maxPrice) {
                            maxPrice = price;
                        }
                        sumPrice += price;
                        priceCount++;
                    }
                }
                if (priceCount > 0) {
                    double avgPrice = sumPrice / priceCount;
                    this.avgPriceLabel.setText(String.format("€%.2f", avgPrice));
                } else {
                    this.avgPriceLabel.setText("€0.00");
                }
            } else {
                this.avgPriceLabel.setText("€0.00");
            }

            this.minPriceLabel.setText(minPrice == Double.MAX_VALUE ? "€0.00" : String.format("€%.2f", minPrice));
            this.maxPriceLabel.setText(String.format("€%.2f", maxPrice));

        } catch (Exception e) {
            this.maxPriceLabel.setText("€0.00");
            this.minPriceLabel.setText("€0.00");
            this.avgPriceLabel.setText("€0.00");
        }
    }
    private void loadAcceptedSaleOfferStatistics() {
        try {
            Map<String, Double> stats = offerService.getAcceptedSaleOfferStatistics();

            this.avgPriceLabel.setText(String.format("€%.2f", stats.get("avg")));
            this.minPriceLabel.setText(String.format("€%.2f", stats.get("min")));
            this.maxPriceLabel.setText(String.format("€%.2f", stats.get("max")));
        } catch (SQLException e) {
            this.avgPriceLabel.setText("€0.00");
            this.minPriceLabel.setText("€0.00");
            this.maxPriceLabel.setText("€0.00");
        }
    }
    private HBox createInsertionCard(Insertion insertion) {
        //Crea la card principale
        HBox card = new HBox(15);
        card.setPrefWidth(this.userAdsContainer.getPrefWidth() - 20);
        card.getStyleClass().add("listing-card");
        card.setPrefHeight(100);

        //Crea la card per l'imageview
        ImageView imageView = new ImageView();
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.getStyleClass().add("listing-image");

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
        titleLabel.getStyleClass().add("listing-title");
        titleLabel.setWrapText(true);

        String priceText;
        if (insertion.getType().equals(typeInsertion.GIFT)) {
            priceText = typeInsertion.GIFT.toString();
        } else if (insertion.getType().equals(typeInsertion.EXCHANGE)) {
            priceText = typeInsertion.EXCHANGE.toString();
        } else {
            priceText = String.format("€%.2f", insertion.getPrice());
        }

        Label priceLabel = new Label(priceText);
        priceLabel.getStyleClass().add("listing-price");

        HBox infoBox = new HBox(10);
        Label statusLabel = new Label(insertion.getStatus().toString());
        statusLabel.getStyleClass().add("listing-status");

        Label dateLabel = new Label(insertion.getPublishDate() != null ? insertion.getPublishDate().toString() : "");
        dateLabel.getStyleClass().add("listing-date");

        infoBox.getChildren().addAll(statusLabel, dateLabel);
        textContent.getChildren().addAll(titleLabel, priceLabel, infoBox);

        VBox actionButtons = new VBox(8);
        actionButtons.setAlignment(javafx.geometry.Pos.CENTER);
        actionButtons.setPrefWidth(80);

        Button editButton = new Button("Modifica");
        editButton.getStyleClass().add("edit-button");

        editButton.setOnAction(event -> {
            editInsertion(event, insertion);
        });


        Button deleteButton = new Button("Elimina");
        deleteButton.getStyleClass().add("delete-button");

        deleteButton.setOnAction(event -> {
            event.consume();
            deleteInsertion(insertion);
            setTotalAdsLabel();
        });
        if(insertion.getStatus().equals(InsertionStatus.PENDING)) {
            deleteButton.setDisable(true);
            editButton.setDisable(true);
        }

        actionButtons.getChildren().addAll(editButton, deleteButton);

        card.getChildren().addAll(imageView, textContent, actionButtons);
        card.setOnMouseClicked(event -> showItemDetails(event, insertion));

        return card;
    }

    private void setTotalAdsLabel(){
        try {
            List<Insertion> insertions = insertionService.getCurrentUserInsertions();
            int totalInsertions = insertions.size();
            int availableInsertions = 0;
            int completedSales = 0;
            double totalEarnings = 0;
            for(Insertion l : insertions){
                if(l.getStatus().equals(InsertionStatus.AVAILABLE))
                    availableInsertions++;
                if(l.getStatus().equals(InsertionStatus.SOLD)) {
                    completedSales++;
                    totalEarnings += l.getPrice() != null ? l.getPrice().doubleValue() : 0.0;
                }
            }
            this.totalAdsLabel.setText(String.valueOf(totalInsertions));
            this.activeSalesLabel.setText(String.valueOf(availableInsertions));
            this.completedSalesLabel.setText(String.valueOf(completedSales));
            this.totalEarningsLabel.setText(String.format("€%.2f", totalEarnings));
        }catch (Exception e){
            this.totalAdsLabel.setText("0");
            this.activeSalesLabel.setText("0");
            this.completedSalesLabel.setText("0");
            this.totalEarningsLabel.setText("€0.00");
        }
    }

    private void showItemDetails(MouseEvent event, Insertion insertion) {
        NavigationService.getInstance().navigateToProductDetailsView(event, insertion);
    }

    private void editInsertion(ActionEvent event, Insertion insertion) {
        NavigationService.getInstance().navigateToEditInsertionView(event, insertion);
    }

    private void deleteInsertion(Insertion insertion) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma eliminazione");
        alert.setHeaderText("Eliminare l'annuncio?");
        alert.setContentText("Stai per eliminare l'annuncio: " + insertion.getTitle() + "\nL'operazione non può essere annullata.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                insertionService.deleteInsertion(insertion.getInsertionID());
                loadUserInsertions();
            } catch (Exception e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Errore");
                errorAlert.setHeaderText("Impossibile eliminare l'annuncio");
                errorAlert.setContentText("Si è verificato un errore: " + e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }
    private void setPieChartAndBarChart(){
       try {
           List<Insertion> insertions = insertionService.getCurrentUserInsertions();
           ObservableList<Insertion> insertionObservableList = FXCollections.observableArrayList(insertions);

           this.offerTypesPieChart.getData().clear();
           ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                   new PieChart.Data("Disponibili", insertionObservableList.stream().filter(l -> l.getStatus() == InsertionStatus.AVAILABLE).count()),
                   new PieChart.Data("In attesa", insertionObservableList.stream().filter(l -> l.getStatus() == InsertionStatus.PENDING).count()),
                   new PieChart.Data("Venduti", insertionObservableList.stream().filter(l -> l.getStatus() == InsertionStatus.SOLD).count()),
                   new PieChart.Data("Rifiutati", insertionObservableList.stream().filter(l -> l.getStatus() == InsertionStatus.REJECTED).count())
           );
           this.offerTypesPieChart.setData(pieChartData);

           this.acceptedOffersBarChart.getData().clear();
           BarChart.Series<String, Number> series = new BarChart.Series<>();
           series.setName("Stato");
           series.getData().add(new BarChart.Data<>("Disponibili", insertionObservableList.stream().filter(l -> l.getStatus() == InsertionStatus.AVAILABLE).count()));
           series.getData().add(new BarChart.Data<>("In attesa", insertionObservableList.stream().filter(l -> l.getStatus() == InsertionStatus.PENDING).count()));
           series.getData().add(new BarChart.Data<>("Venduti", insertionObservableList.stream().filter(l -> l.getStatus() == InsertionStatus.SOLD).count()));
           series.getData().add(new BarChart.Data<>("Rifiutati", insertionObservableList.stream().filter(l -> l.getStatus() == InsertionStatus.REJECTED).count()));
           this.acceptedOffersBarChart.getData().add(series);
       } catch (Exception e) {
           ValidationService.getInstance().showFailedToSetStatsError();
       }
    }

    @FXML
    private void makeNewInsertion(ActionEvent event) {
        NavigationService.getInstance().navigateToNewInsertionView(event);
    }

    @FXML
    public void goBack(ActionEvent event) {
        NavigationService.getInstance().navigateToMainView(event);
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        validationService.showLogoutSuccess();
        UserSession.getInstance().logout();
        NavigationService.getInstance().navigateToLoginView(event);
    }

    @FXML
    private void handleChangeUsername() {
        String newUsername = newUsernameField.getText().trim();

        if (newUsername.isEmpty()) {
            validationService.showAlert(Alert.AlertType.WARNING,
                    "Attenzione", "Inserisci il nuovo username.");
            return;
        }

        // Conferma dell'operazione
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Conferma cambio username");
        confirmAlert.setHeaderText("Cambiare username?");
        confirmAlert.setContentText("Stai per cambiare il tuo username da '" +
                this.currentUsernameField.getText() + "' a '" + newUsername + "'.\n" +
                "Sei sicuro di voler continuare?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = ProfileSecurityService.getInstance().changeUsername(newUsername);

            if (success) {
                // Aggiorna i campi nell'interfaccia
                currentUsernameField.setText(newUsername);
                userNameLabel.setText(newUsername);
                newUsernameField.clear();
            }
        }
    }

    @FXML
    private void handleChangePassword(ActionEvent event) {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validazione input
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            validationService.showAlert(Alert.AlertType.WARNING,
                    "Attenzione", "Compila tutti i campi password.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            validationService.showAlert(Alert.AlertType.ERROR,
                    "Errore", "La nuova password e la conferma non coincidono.");
            return;
        }

        if (currentPassword.equals(newPassword)) {
            validationService.showAlert(Alert.AlertType.WARNING,
                    "Attenzione", "La nuova password deve essere diversa da quella attuale.");
            return;
        }

        // Conferma dell'operazione
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Conferma cambio password");
        confirmAlert.setHeaderText("Cambiare password?");
        confirmAlert.setContentText("Stai per cambiare la tua password.\nSei sicuro di voler continuare?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = ProfileSecurityService.getInstance().changePassword(currentPassword, newPassword);

            if (success) {
                currentPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();
            }
        }
    }

    private void loadPendingOffers(){

        try{
            pendingOffersContainer.getChildren().clear();

            List<Offer> pendingOffers = offerService.getPendingOffersByUser();
            if(pendingOffers.isEmpty()){
                Label emptyLabel = new Label("Nessuna offerta in sospeso");
                pendingOffersContainer.getChildren().add(emptyLabel);
            } else {
                for(Offer offer : pendingOffers){
                    pendingOffersContainer.getChildren().add(createOfferCard(offer,false));
                }
            }

        }catch(Exception e){
            Label errorLabel = new Label("Errore nel caricamento delle offerte in sospeso " + e.getMessage());
            pendingOffersContainer.getChildren().add(errorLabel);
        }
    }
    private HBox createOfferCard(Offer offer, boolean isCompleted) {
        HBox card = new HBox(15);
        card.getStyleClass().add("offer-card");
        card.setPrefHeight(100);
        try {
            Insertion insertion = insertionService.getInsertionByID(offer.getInsertionID());

            ImageView imageView = new ImageView();
            imageView.setFitWidth(80);
            imageView.setFitHeight(80);
            imageView.setPreserveRatio(true);

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
            titleLabel.getStyleClass().add("offer-title");

            String dateText = "Data: " + offer.getOfferDate();
            Label dateLabel = new Label(dateText);
            dateLabel.getStyleClass().add("offer-date");

            Label valueLabel = new Label(String.format("Valore: €%.2f", offer.getAmount()));
            valueLabel.getStyleClass().add("offer-value");

            textContent.getChildren().addAll(titleLabel, dateLabel, valueLabel);

            VBox buttonContainer = new VBox(8);
            buttonContainer.setAlignment(javafx.geometry.Pos.CENTER);

            if (!isCompleted) {
                Button cancelButton = new Button("Annulla");
                cancelButton.getStyleClass().add("cancel-button");
                cancelButton.setOnAction(event -> cancelOffer(offer.getOfferID()));
                buttonContainer.getChildren().add(cancelButton);
            }

            card.getChildren().addAll(imageView, textContent, buttonContainer);

        } catch (Exception e) {
            Label errorLabel = new Label("Errore nel caricamento dell'offerta" + e.getMessage());
            card.getChildren().add(errorLabel);
        }

        return card;
    }
    private void loadCompletedOperations() {
        try {
            completedOperationsContainer.getChildren().clear();

            List<Offer> completedOffers = offerService.getAllCompletedOperationsByUser(UserSession.getInstance().getCurrentUserId());
            if (completedOffers.isEmpty()) {
                Label emptyLabel = new Label("Nessuna operazione completata");
                emptyLabel.getStyleClass().add("palceholder-text");
                completedOperationsContainer.getChildren().add(emptyLabel);
            } else {
                for (Offer offer : completedOffers) {
                    completedOperationsContainer.getChildren().add(createOfferCard(offer, true));
                }
            }
        } catch (Exception e) {
            Label errorLabel = new Label("Errore nel caricamento delle operazioni completate " + e.getMessage());
            completedOperationsContainer.getChildren().add(errorLabel);
        }
    }

    private void cancelOffer(int offerId) {
        try {
            offerService.deleteOffer(offerId);
                validationService.showInsertionAnnulledSuccess();
                loadPendingOffers();
        } catch (Exception e) {
            validationService.showAlert(Alert.AlertType.ERROR, "Errore", "Si è verificato un errore durante l'annullamento dell'offerta.");
        }
    }
    private TextFormatter<String> createTextFormatter(int maxLength) {
        return new TextFormatter<>(change -> {
            if (change.getControlNewText().length() <= maxLength) {
                return change;
            }
            return null;
        });
    }
}
