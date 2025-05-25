package com.uninaswap.controllers;

import com.uninaswap.dao.ListingDaoImpl;
import com.uninaswap.dao.UserDao;
import com.uninaswap.dao.UserDaoImpl;
import com.uninaswap.model.Faculty;
import com.uninaswap.model.Listing;
import com.uninaswap.model.User;
import com.uninaswap.services.NavigationService;
import com.uninaswap.services.UserSession;
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
import java.text.SimpleDateFormat;
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

        // Load all listings into the userAdsContainer
        loadUserListings();
    }

    private void loadUserListings() {
        try {
            ListingDaoImpl listingDao = new ListingDaoImpl();
            List<Listing> listings = listingDao.findAll();

            // Clear existing content
            userAdsContainer.getChildren().clear();

            if (listings.isEmpty()) {
                Label emptyLabel = new Label("Nessun annuncio trovato");
                emptyLabel.getStyleClass().add("placeholder-text");
                userAdsContainer.getChildren().add(emptyLabel);
            } else {
                for (Listing listing : listings) {
                    userAdsContainer.getChildren().add(createListingCard(listing));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Errore nel caricamento degli annunci");
            errorLabel.getStyleClass().add("error-text");
            userAdsContainer.getChildren().add(errorLabel);
        }
    }

    private HBox createListingCard(Listing listing) {
        HBox card = new HBox(15);
        card.getStyleClass().add("listing-card");
        card.setPrefWidth(userAdsContainer.getPrefWidth() - 20);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);

        String defaultImagePath = "/com/uninaswap/images/default_image.png";
        try {
            File imageFile = new File(listing.getImageUrl());
            imageView.setImage(new Image(imageFile.toURI().toString()));
        } catch (Exception e) {
            //System.out.println("Impossibile caricare l'immagine: " + e.getMessage());
            imageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(defaultImagePath))));
        }


        VBox textContent = new VBox(5);
        textContent.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        HBox.setHgrow(textContent, javafx.scene.layout.Priority.ALWAYS);

        Label titleLabel = new Label(listing.getTitle());
        titleLabel.getStyleClass().add("listing-title");

        HBox infoBox = new HBox(10);
        Label statusLabel = new Label(listing.getStatus().toString());
        statusLabel.getStyleClass().addAll("status-label", "status-" + listing.getStatus().toString().toLowerCase());

        Label dateLabel = new Label(listing.getPublishDate() != null ?
                new SimpleDateFormat("dd/MM/yyyy").format(listing.getPublishDate()) : "");
        dateLabel.getStyleClass().add("date-label");

        infoBox.getChildren().addAll(statusLabel, dateLabel);

        // Price and type
        String priceText = listing.getPrice() != null ? String.format("€%.2f", listing.getPrice()) : listing.getType().toString();
        Label priceLabel = new Label(priceText);
        priceLabel.getStyleClass().add("price-label");

        textContent.getChildren().addAll(titleLabel, infoBox, priceLabel);

        VBox actionButtons = new VBox(5);
        actionButtons.setAlignment(javafx.geometry.Pos.CENTER);

        Button editButton = new Button("Modifica");
        editButton.getStyleClass().add("edit-button");
        editButton.setOnAction(event -> editListing(listing));

        Button deleteButton = new Button("Elimina");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(event -> deleteListing(listing));

        actionButtons.getChildren().addAll(editButton, deleteButton);

        card.getChildren().addAll(imageView, textContent, actionButtons);
        card.setOnMouseClicked(event -> showItemDetails(event, listing));

        return card;
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
                loadUserListings(); // Reload the listings
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
        UserSession.getInstance().logout();
        NavigationService.getInstance().navigateToLoginView(event);
    }
}
