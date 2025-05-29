package com.uninaswap.controllers;

import com.uninaswap.dao.ListingDaoImpl;
import com.uninaswap.dao.UserDaoImpl;
import com.uninaswap.model.*;
import com.uninaswap.services.NavigationService;
import com.uninaswap.services.ProfileSecurityService;
import com.uninaswap.services.UserSession;
import com.uninaswap.services.ValidationService;
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
import java.util.Objects;
import java.util.Optional;


public class MyProfileController {
    @FXML private ImageView logoImage;
    @FXML private Button notificationsButton;
    @FXML private Button helpButton;
    @FXML private Button editProfileButton;
    @FXML private Button backButton;
    @FXML private ImageView profileAvatarView;
    @FXML private Button changeAvatarButton;
    @FXML private Label userNameLabel;
    @FXML private Label userEmailLabel;
    @FXML private Label memberSinceLabel;
    @FXML private Label userRatingLabel;
    @FXML private Label verifiedBadge;
    @FXML private Label totalAdsLabel;
    @FXML private Label activeSalesLabel;
    @FXML private Label completedSalesLabel;
    @FXML private Label totalEarningsLabel;
    @FXML private TabPane profileTabPane;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> facultyComboBox;
    @FXML private TextArea bioTextArea;
    @FXML private Button cancelChangesButton;
    @FXML private Button saveChangesButton;
    @FXML private ComboBox<String> adsFilterComboBox;
    @FXML private Button newAdButton;
    @FXML private VBox userAdsContainer;
    @FXML private TextField currentUsernameField;
    @FXML private TextField newUsernameField;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button changeUsernameButton;
    @FXML private Button emailVisibilityButton;
    @FXML private Button messagesFromStrangersButton;
    @FXML private Button pushNotificationsButton;
    @FXML private Button changePasswordButton;
    @FXML private Button enable2FAButton;
    @FXML private Button downloadDataButton;
    @FXML private Button deleteAccountButton;
    @FXML private Button logoutButton;
    @FXML private Label totalOffersStat;
    @FXML private Label acceptedOffersStat;
    @FXML private Label pendingOffersStat;
    @FXML private Label rejectedOffersStat;
    @FXML private PieChart offerTypesPieChart;
    @FXML private BarChart<String, Number> acceptedOffersBarChart;
    @FXML private Label avgPriceLabel;
    @FXML private Label minPriceLabel;
    @FXML private Label maxPriceLabel;


    @FXML
    public void initialize() {
        User currentUser = new UserDaoImpl().getUserFromID(UserSession.getInstance().getCurrentUserId());
        this.userNameLabel.setText(currentUser.getUsername());
        this.firstNameField.setText(currentUser.getFirst_name());
        this.lastNameField.setText(currentUser.getLast_name());
        this.currentUsernameField.setText(currentUser.getUsername());

        for (Faculty faculty : Faculty.values()) {
            this.facultyComboBox.getItems().add(faculty.getFacultyName());
        }
        this.facultyComboBox.setValue(currentUser.getFaculty());
        setTotalAdsLabel();
        setStatisticsSection();
        loadUserListings();
        setPieChartAndBarChart();
    }

    private void loadUserListings() {
        try {
            ListingDaoImpl listingDao = new ListingDaoImpl();
            List<Listing> listings = listingDao.findMyAviableInsertions();

            // Clear existing content
            this.userAdsContainer.getChildren().clear();

            if (listings.isEmpty()) {
                Label emptyLabel = new Label("Nessun annuncio trovato");
                this.userAdsContainer.getChildren().add(emptyLabel);
            } else {
                for (Listing listing : listings) {
                    this.userAdsContainer.getChildren().add(createListingCard(listing));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Errore nel caricamento degli annunci");
            userAdsContainer.getChildren().add(errorLabel);
        }
    }
    private void setStatisticsSection(){
        try{
            ListingDaoImpl listingDao = new ListingDaoImpl();
            List<Listing> listings = listingDao.findMyAviableInsertions();

            double minPrice = listings.getFirst().getType() == typeListing.SALE ? listings.getFirst().getPrice().doubleValue() : Integer.MAX_VALUE;
            double maxPrice = listings.getFirst().getType() == typeListing.SALE ? listings.getFirst().getPrice().doubleValue() : 0;
            double sumPrice = 0.0;
            for(Listing listing : listings){
                if(listing.getStatus() == ListingStatus.SOLD){
                    this.acceptedOffersStat.setText(String.valueOf(Integer.parseInt(this.acceptedOffersStat.getText()) + 1));
                }
                if(listing.getStatus() == ListingStatus.PENDING){
                    this.pendingOffersStat.setText(String.valueOf(Integer.parseInt(this.pendingOffersStat.getText()) + 1));
                }
                if(listing.getStatus() == ListingStatus.REJECTED){
                    this.rejectedOffersStat.setText(String.valueOf(Integer.parseInt(this.rejectedOffersStat.getText()) + 1));
                }
                if(listing.getPrice().doubleValue() < minPrice) {
                    minPrice = listing.getPrice().doubleValue();
                }
                if(listing.getPrice().doubleValue() > maxPrice) {
                    maxPrice = listing.getPrice().doubleValue();
                }
                sumPrice += listing.getPrice() != null ? listing.getPrice().doubleValue() : 0.0;
            }
            double avgPrice = sumPrice / listings.size();

            this.avgPriceLabel.setText(String.format("€%.2f", avgPrice));
            this.minPriceLabel.setText(String.format("€%.2f", minPrice));
            this.maxPriceLabel.setText(String.format("€%.2f", maxPrice));
        }catch (Exception e){
            e.printStackTrace();
            this.totalOffersStat.setText("0");
            this.acceptedOffersStat.setText("0");
            this.pendingOffersStat.setText("0");
            this.rejectedOffersStat.setText("0");
            this.avgPriceLabel.setText("€0.00");
            this.minPriceLabel.setText("€0.00");
            this.maxPriceLabel.setText("€0.00");
        }
    }
    private HBox createListingCard(Listing listing) {
        HBox card = new HBox(15);
        card.setPrefWidth(this.userAdsContainer.getPrefWidth() - 20);
        card.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: white;");
        card.setPrefHeight(100);

        // Add hover effect
        card.setOnMouseEntered(e -> card.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: #f8f8f8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: white;"));

        ImageView imageView = new ImageView();
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 0); -fx-background-radius: 3;");

        String defaultImagePath = "/com/uninaswap/images/default_image.png";
        try {
            File imageFile = new File(listing.getImageUrl());
            imageView.setImage(new Image(imageFile.toURI().toString()));
        } catch (Exception e) {
            imageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(defaultImagePath))));
        }

        VBox textContent = new VBox(5);
        textContent.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        HBox.setHgrow(textContent, javafx.scene.layout.Priority.ALWAYS);

        Label titleLabel = new Label(listing.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        titleLabel.setWrapText(true);

        // Price and type
        //String priceText = listing.getType().equals(typeListing.GIFT) ? String.format("€%.2f", listing.getPrice()) : listing.getType().toString();
        String priceText;
        if (listing.getType().equals(typeListing.GIFT)) {
            priceText = typeListing.GIFT.toString();
        }
        else if (listing.getType().equals(typeListing.EXCHANGE)) {
            priceText = typeListing.EXCHANGE.toString();
        } else {
            priceText = String.format("€%.2f", listing.getPrice());
        }

        Label priceLabel = new Label(priceText);
        priceLabel.setStyle("-fx-font-size: 14px;");

        HBox infoBox = new HBox(10);
        Label statusLabel = new Label(listing.getStatus().toString());
        statusLabel.setStyle("-fx-font-size: 12px; -fx-background-color: #f0f0f0; -fx-padding: 2 5; -fx-background-radius: 3;");

        Label dateLabel = new Label(listing.getPublishDate() != null ? listing.getPublishDate().toString() : "");
        dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");

        infoBox.getChildren().addAll(statusLabel, dateLabel);
        textContent.getChildren().addAll(titleLabel, priceLabel, infoBox);

        VBox actionButtons = new VBox(8);
        actionButtons.setAlignment(javafx.geometry.Pos.CENTER);
        actionButtons.setPrefWidth(80);

        Button editButton = new Button("Modifica");
        editButton.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #444; -fx-background-radius: 3;");
        editButton.setPrefWidth(75);
        editButton.setOnAction(event -> {
            event.consume(); editListing(listing);
        });

        Button deleteButton = new Button("Elimina");
        deleteButton.setStyle("-fx-background-color: #ffecec; -fx-text-fill: #d32f2f; -fx-background-radius: 3;");
        deleteButton.setPrefWidth(75);
        deleteButton.setOnAction(event -> {
            event.consume();
            deleteListing(listing);
        });

        actionButtons.getChildren().addAll(editButton, deleteButton);

        card.getChildren().addAll(imageView, textContent, actionButtons);
        card.setOnMouseClicked(event -> showItemDetails(event, listing));

        return card;
    }

    private void setTotalAdsLabel(){
        try {
            List<Listing> listingDao = new ListingDaoImpl().findMyInsertions();
            int totalInsertions = listingDao.size();
            int availableListings = 0;
            int completedSales = 0;
            double totalEarnings = 0;
            for(Listing l : listingDao){
                if(l.getStatus().equals(ListingStatus.AVAILABLE))
                    availableListings++;
                if(l.getStatus().equals(ListingStatus.SOLD)) {
                    completedSales++;
                    totalEarnings += l.getPrice() != null ? l.getPrice().doubleValue() : 0.0;
                }
            }
            this.totalAdsLabel.setText(String.valueOf(totalInsertions));
            this.activeSalesLabel.setText(String.valueOf(availableListings));
            this.completedSalesLabel.setText(String.valueOf(completedSales));
            this.totalEarningsLabel.setText(String.format("€%.2f", totalEarnings));
        }catch (Exception e){
            this.totalAdsLabel.setText("0");
            this.activeSalesLabel.setText("0");
        }
    }

    private void showItemDetails(MouseEvent event, Listing listing) {
        NavigationService.getInstance().navigateToProductDetailsView(event, listing);
    }

    private void editListing(Listing listing) {
        // Placeholder for future edit functionality
        System.out.println("Edit listing: " + listing.getListingId());
    }

    private void deleteListing(Listing listing) {
        // Implement delete confirmation and functionality
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma eliminazione");
        alert.setHeaderText("Eliminare l'annuncio?");
        alert.setContentText("Stai per eliminare l'annuncio: " + listing.getTitle() + "\nL'operazione non può essere annullata.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                ListingDaoImpl listingDao = new ListingDaoImpl();
                listingDao.delete(listing.getListingId());
                loadUserListings();
            } catch (Exception e) {
                e.printStackTrace();
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
           ListingDaoImpl listingDao = new ListingDaoImpl();
           List<Listing> listings = listingDao.findMyInsertions();
           ObservableList<Listing> listingObservableList = FXCollections.observableArrayList(listings);

           this.offerTypesPieChart.getData().clear();
           ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                   new PieChart.Data("Disponibili", listingObservableList.stream().filter(l -> l.getStatus() == ListingStatus.AVAILABLE).count()),
                   new PieChart.Data("In attesa", listingObservableList.stream().filter(l -> l.getStatus() == ListingStatus.PENDING).count()),
                   new PieChart.Data("Venduti", listingObservableList.stream().filter(l -> l.getStatus() == ListingStatus.SOLD).count()),
                   new PieChart.Data("Rifiutati", listingObservableList.stream().filter(l -> l.getStatus() == ListingStatus.REJECTED).count())
           );
           this.offerTypesPieChart.setData(pieChartData);

           this.acceptedOffersBarChart.getData().clear();
           BarChart.Series<String, Number> series = new BarChart.Series<>();
           series.setName("Stato");
           series.getData().add(new BarChart.Data<>("Disponibili", listingObservableList.stream().filter(l -> l.getStatus() == ListingStatus.AVAILABLE).count()));
           series.getData().add(new BarChart.Data<>("In attesa", listingObservableList.stream().filter(l -> l.getStatus() == ListingStatus.PENDING).count()));
           series.getData().add(new BarChart.Data<>("Venduti", listingObservableList.stream().filter(l -> l.getStatus() == ListingStatus.SOLD).count()));
           series.getData().add(new BarChart.Data<>("Rifiutati", listingObservableList.stream().filter(l -> l.getStatus() == ListingStatus.REJECTED).count()));
           this.acceptedOffersBarChart.getData().add(series);
       } catch (Exception e) {
           e.printStackTrace();
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
        ValidationService.getInstance().showLogoutSuccess();
        UserSession.getInstance().logout();
        NavigationService.getInstance().navigateToLoginView(event);
    }

    @FXML
    private void handleChangeUsername(ActionEvent event) {
        String newUsername = newUsernameField.getText().trim();

        if (newUsername.isEmpty()) {
            ValidationService.getInstance().showAlert(Alert.AlertType.WARNING,
                    "Attenzione", "Inserisci il nuovo username.");
            return;
        }

        // Conferma dell'operazione
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Conferma cambio username");
        confirmAlert.setHeaderText("Cambiare username?");
        confirmAlert.setContentText("Stai per cambiare il tuo username da '" +
                currentUsernameField.getText() + "' a '" + newUsername + "'.\n" +
                "Sei sicuro di voler continuare?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Use the user directly from the session
            User currentUser = UserSession.getInstance().getCurrentUser();
            boolean success = ProfileSecurityService.getInstance().changeUsername(
                    currentUser.getId(), newUsername);

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
            ValidationService.getInstance().showAlert(Alert.AlertType.WARNING,
                    "Attenzione", "Compila tutti i campi password.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR,
                    "Errore", "La nuova password e la conferma non coincidono.");
            return;
        }

        if (currentPassword.equals(newPassword)) {
            ValidationService.getInstance().showAlert(Alert.AlertType.WARNING,
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
            // Use the user directly from the session
            User currentUser = UserSession.getInstance().getCurrentUser();
            boolean success = ProfileSecurityService.getInstance().changePassword(
                    currentUser.getId(), currentPassword, newPassword);

            if (success) {
                // Pulisci i campi password
                currentPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();
            }
        }
    }



}
