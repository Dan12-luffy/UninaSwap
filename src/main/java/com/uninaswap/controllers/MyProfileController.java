package com.uninaswap.controllers;

import com.uninaswap.dao.ListingDaoImpl;
import com.uninaswap.dao.UserDaoImpl;
import com.uninaswap.model.Faculty;
import com.uninaswap.model.Listing;
import com.uninaswap.model.ListingStatus;
import com.uninaswap.model.User;
import com.uninaswap.services.NavigationService;
import com.uninaswap.services.UserSession;
import com.uninaswap.services.ValidationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button emailVisibilityButton;
    @FXML private Button messagesFromStrangersButton;
    @FXML private Button pushNotificationsButton;
    @FXML private Button changePasswordButton;
    @FXML private Button enable2FAButton;
    @FXML private Button downloadDataButton;
    @FXML private Button deleteAccountButton;
    @FXML private Button logoutButton;
    //private User user;


    @FXML
    public void initialize() {
        User currentUser = new UserDaoImpl().getUserFromID(UserSession.getInstance().getCurrentUserId());
        userNameLabel.setText(currentUser.getUsername());
        firstNameField.setText(currentUser.getFirst_name());
        lastNameField.setText(currentUser.getLast_name());
        for (Faculty faculty : Faculty.values()) {
            facultyComboBox.getItems().add(faculty.getFacultyName());
        }
        facultyComboBox.setValue(currentUser.getFaculty());
        setTotalAdsLabel();
        // Load all listings into the userAdsContainer
        loadUserListings();
    }

    private void loadUserListings() {
        try {
            ListingDaoImpl listingDao = new ListingDaoImpl();
            List<Listing> listings = listingDao.findMyInsertions();

            // Clear existing content
            userAdsContainer.getChildren().clear();

            if (listings.isEmpty()) {
                Label emptyLabel = new Label("Nessun annuncio trovato");
                userAdsContainer.getChildren().add(emptyLabel);
            } else {
                for (Listing listing : listings) {
                    userAdsContainer.getChildren().add(createListingCard(listing));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Errore nel caricamento degli annunci");
            userAdsContainer.getChildren().add(errorLabel);
        }
    }

    private HBox createListingCard(Listing listing) {
        HBox card = new HBox(15);
        card.setPrefWidth(userAdsContainer.getPrefWidth() - 20);
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
        String priceText = listing.getPrice() != null ? String.format("€%.2f", listing.getPrice()) : listing.getType().toString();
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
            int temp = 0;
            for(Listing l : listingDao){
                if(l.getStatus().equals(ListingStatus.AVAILABLE))
                    temp++;
            }
            this.totalAdsLabel.setText(String.valueOf(totalInsertions));
            this.activeSalesLabel.setText(String.valueOf(temp));
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
}
