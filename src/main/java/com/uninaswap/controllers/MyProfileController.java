package com.uninaswap.controllers;

import com.uninaswap.dao.InsertionDaoImpl;
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
    @FXML private Tab favoritesTab;
    @FXML private VBox favoritesContainer;

    private final UserService userService = UserService.getInstance();
    private final InsertionService insertionService = InsertionService.getInstance();
    private final ValidationService validationService = ValidationService.getInstance();

    @FXML
    public void initialize() {

        User currentUser = userService.getUserById(UserSession.getInstance().getCurrentUserId());
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
            List<Insertion> insertions = insertionService.getCurrentUserAvailableInsertions();

            // Clear existing content
            this.userAdsContainer.getChildren().clear();

            if (insertions.isEmpty()) {
                Label emptyLabel = new Label("Nessun annuncio trovato");
                this.userAdsContainer.getChildren().add(emptyLabel);
            } else {
                for (Insertion insertion : insertions) {
                    this.userAdsContainer.getChildren().add(createListingCard(insertion));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Errore nel caricamento degli annunci");
            userAdsContainer.getChildren().add(errorLabel);
        }
    }


    private void setStatisticsSection() {
        try {
            List<Insertion> insertions = insertionService.getCurrentUserAvailableInsertions();

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
            this.totalOffersStat.setText("0");
            this.maxPriceLabel.setText("€0.00");
            this.minPriceLabel.setText("€0.00");
            this.avgPriceLabel.setText("€0.00");
            e.printStackTrace();
        }
    }
    private HBox createListingCard(Insertion insertion) {
        HBox card = new HBox(15);
        card.setPrefWidth(this.userAdsContainer.getPrefWidth() - 20);
        card.getStyleClass().add("listing-card");
        card.setPrefHeight(100);

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
            editListing(event, insertion);
        });

        Button deleteButton = new Button("Elimina");
        deleteButton.getStyleClass().add("delete-button");

        deleteButton.setOnAction(event -> {
            event.consume();
            deleteListing(insertion);
            setTotalAdsLabel();
        });

        actionButtons.getChildren().addAll(editButton, deleteButton);

        card.getChildren().addAll(imageView, textContent, actionButtons);
        card.setOnMouseClicked(event -> showItemDetails(event, insertion));

        return card;
    }

    private void setTotalAdsLabel(){
        try {
            List<Insertion> insertionDao = insertionService.getCurrentUserAvailableInsertions();
            int totalInsertions = insertionDao.size();
            int availableListings = 0;
            int completedSales = 0;
            double totalEarnings = 0;
            for(Insertion l : insertionDao){
                if(l.getStatus().equals(InsertionStatus.AVAILABLE))
                    availableListings++;
                if(l.getStatus().equals(InsertionStatus.SOLD)) {
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

    private void showItemDetails(MouseEvent event, Insertion insertion) {
        NavigationService.getInstance().navigateToProductDetailsView(event, insertion);
    }

    private void editListing(ActionEvent event, Insertion insertion) {
        NavigationService.getInstance().navigateToEditListingView(event, insertion);
    }

    private void deleteListing(Insertion insertion) {
        // Implement delete confirmation and functionality
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma eliminazione");
        alert.setHeaderText("Eliminare l'annuncio?");
        alert.setContentText("Stai per eliminare l'annuncio: " + insertion.getTitle() + "\nL'operazione non può essere annullata.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                InsertionDaoImpl listingDao = new InsertionDaoImpl();
                listingDao.delete(insertion.getInsertionID());
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
        validationService.showLogoutSuccess();
        UserSession.getInstance().logout();
        NavigationService.getInstance().navigateToLoginView(event);
    }

    @FXML
    private void handleChangeUsername(ActionEvent event) {
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
                currentUsernameField.getText() + "' a '" + newUsername + "'.\n" +
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



}
