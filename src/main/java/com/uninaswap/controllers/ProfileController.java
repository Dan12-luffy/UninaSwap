package com.uninaswap.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import java.io.IOException;

public class ProfileController {

    // Header components
    @FXML private Button notificationBtn;
    @FXML private Button profileBtn;
    @FXML private TextField searchField;
    @FXML private Button searchBtn;
    @FXML private Button wishlistBtn;
    @FXML private Button messagesBtn;
    @FXML private Button sellBtn;
    @FXML private Button backBtn;

    // Profile information
    @FXML private Circle profileImgCircle;
    @FXML private ImageView profileImg;
    @FXML private Button changeProfileImgBtn;
    @FXML private Label userFullNameLbl;
    @FXML private Label userRoleLbl;
    @FXML private Label itemsCountLbl;
    @FXML private Label ratingLbl;
    @FXML private Label dealsCountLbl;
    @FXML private Label userEmailLbl;
    @FXML private Label userPhoneLbl;
    @FXML private Label userFacultyLbl;
    @FXML private Label userYearLbl;

    // Navigation toggles
    @FXML private ToggleButton insertionsToggle;
    @FXML private ToggleButton proposalsToggle;
    @FXML private ToggleButton offersMadeToggle;
    @FXML private ToggleButton historyToggle;
    @FXML private ToggleButton favoritesToggle;
    @FXML private ToggleButton settingsToggle;

    // Content sections
    @FXML private VBox insertionsSection;
    @FXML private VBox proposalsSection;
    @FXML private VBox settingsSection;
    @FXML private VBox activeItemsContainer;

    // Action buttons
    @FXML private Button addNewItemBtn;
    @FXML private Button viewAllProposalsBtn;
    @FXML private Button saveSettingsBtn;

    private String username;
    private Stage stage;

    @FXML
    public void initialize() {
        // Set up toggle buttons group so only one can be selected at a time
        ToggleGroup toggleGroup = new ToggleGroup();
        insertionsToggle.setToggleGroup(toggleGroup);
        proposalsToggle.setToggleGroup(toggleGroup);
        offersMadeToggle.setToggleGroup(toggleGroup);
        historyToggle.setToggleGroup(toggleGroup);
        favoritesToggle.setToggleGroup(toggleGroup);
        settingsToggle.setToggleGroup(toggleGroup);

        // Set change listeners for toggle buttons to show appropriate sections
        insertionsToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) showSection(insertionsSection);
        });

        proposalsToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) showSection(proposalsSection);
        });

        settingsToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) showSection(settingsSection);
        });

        // Set up button actions
        backBtn.setOnAction(event -> navigateToHome());
        addNewItemBtn.setOnAction(event -> openNewItemForm());
        saveSettingsBtn.setOnAction(event -> saveUserSettings());

        // Default selected tab
        insertionsToggle.setSelected(true);
    }

    private void showSection(VBox sectionToShow) {
        // Hide all sections
        insertionsSection.setVisible(false);
        proposalsSection.setVisible(false);
        settingsSection.setVisible(false);

        // Show the selected section
        sectionToShow.setVisible(true);
    }

    private void navigateToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/mainInterface.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) backBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Errore", "Impossibile tornare alla home.");
        }
    }

    private void openNewItemForm() {
        // Method stub for opening new item form
        showAlert("Nuovo Articolo", "Apertura form per nuovo articolo...");
    }

    private void saveUserSettings() {
        // Method stub for saving user settings
        showAlert("Impostazioni", "Salvataggio impostazioni...");
    }

    public void setUsername(String username) {
        this.username = username;
        loadUserData();
    }

    private void loadUserData() {
        // This would typically load user data from a database
        // For now, just display the username
        userFullNameLbl.setText(username);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}