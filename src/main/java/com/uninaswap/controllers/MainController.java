package com.uninaswap.controllers;

import com.uninaswap.dao.ListingDao;
import com.uninaswap.dao.ListingDaoImpl;
import com.uninaswap.model.Listing;
import com.uninaswap.model.User;
import com.uninaswap.services.NavigationService;
import com.uninaswap.services.UserSession;
import com.uninaswap.services.ValidationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import javafx.scene.input.MouseEvent;
import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class MainController {
    private String username;

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button sellButton;
    @FXML private Button applyFilters;
    @FXML private Button resetFilters;
    @FXML private Button profileButton;
    @FXML private Button logoutButton;
    @FXML private Label usernameLabel;

    // New UI element references
    @FXML private Button notificationButton;
    @FXML private Button wishlistButton;
    @FXML private Button messagesButton;
    @FXML private ToggleButton allCategoryButton;
    @FXML private ToggleButton booksCategoryButton;
    @FXML private ToggleButton electronicsCategoryButton;
    @FXML private ToggleButton clothingCategoryButton;
    @FXML private ToggleButton notesCategoryButton;
    @FXML private ToggleButton toolsCategoryButton;
    @FXML private ToggleButton otherCategoryButton;
    @FXML private RadioButton allConditionsRadio;
    @FXML private RadioButton likeNewRadio;
    @FXML private RadioButton excellentRadio;
    @FXML private RadioButton goodRadio;
    @FXML private Slider priceSlider;
    @FXML private TextField minPriceField;
    @FXML private TextField maxPriceField;
    @FXML private Button applyPriceButton;
    @FXML private Button loadMoreButton;
    @FXML private Button userItemsButton;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private ToggleButton listViewButton;
    @FXML private ToggleButton gridViewButton;
    @FXML private GridPane itemsGrid;
    @FXML private ScrollPane itemsScrollPane;
    @FXML private Label resultsCountLabel;
    @FXML private Label maxPriceLabel;
    @FXML private Label minPriceLabel;

    @FXML
    private void initialize() {
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            usernameLabel.setText("Ciao " + currentUser.getUsername());
        }
        sortComboBox.getItems().addAll("Più recenti", "Prezzo crescente", "Prezzo decrescente");
        sortComboBox.setValue("Più recenti");
        conditionToggleGroupSettings();

        // Select the "all conditions" option by default
        allConditionsRadio.setSelected(true);
        loadItems();
        maxPriceLabel.setText(((int)priceSlider.getMax() + "€"));
    }

    private void loadItems() {
        try {
            ListingDao listingDao = new ListingDaoImpl();
            List<Listing> listings = listingDao.findAllOtherInsertions();
            setupItemGrid();

            if (!listings.isEmpty()) {
                int column = 0;
                int row = 0;
                BigDecimal maxPriceAmongListings = BigDecimal.ZERO;
                for (Listing listing : listings) {
                    VBox itemCard = createItemCard(listing);
                    itemsGrid.add(itemCard, column, row);

                    column++;

                    if (column > 3) {  // Massimo 4 colonne
                        column = 0;
                        row++;
                    }
                    //Cerco il massimo tra i listing
                    if(listing.getPrice() != null && listing.getPrice().compareTo(maxPriceAmongListings) > 0) {
                        maxPriceAmongListings = listing.getPrice();
                    }
                }
                // Imposta il numero massimo dello slider
                priceSlider.setMax(maxPriceAmongListings.doubleValue());

                if (listings.size() == 1) {
                    resultsCountLabel.setText("Trovato 1 articolo");
                } else {
                    resultsCountLabel.setText("Trovati " + listings.size() + " articoli");
                }
            } else {
                resultsCountLabel.setText("Trovati 0 articoli");
            }

        } catch (SQLException e) {
            resultsCountLabel.setText("Errore durante il caricamento");
            e.printStackTrace();
        }
    }
    @FXML
    private void onApplyPriceButtonClicked() {
        try {
            double min = Double.parseDouble(minPriceField.getText());
            double max = Double.parseDouble(maxPriceField.getText());
            ListingDao listingDao = new ListingDaoImpl();
            List<Listing> listings = listingDao.findByPriceRange(min, max);
            setupItemGrid();

            if (!listings.isEmpty()) {
                int column = 0;
                int row = 0;
                //BigDecimal maxPriceAmongListings = BigDecimal.ZERO;
                for (Listing listing : listings) {
                    VBox itemCard = createItemCard(listing);
                    itemsGrid.add(itemCard, column, row);

                    column++;

                    if (column > 3) {  // Massimo 4 colonne
                        column = 0;
                        row++;
                    }
                }

                if (listings.size() == 1) {
                    resultsCountLabel.setText("Trovato 1 articolo");
                } else {
                    resultsCountLabel.setText("Trovati " + listings.size() + " articoli");
                }
            } else {
                resultsCountLabel.setText("Trovati 0 articoli");
            }

        } catch (NumberFormatException e) {
            ValidationService.getInstance().showInvalidPriceError();
        } catch (Exception e) {
            resultsCountLabel.setText("Errore durante il caricamento");
            e.printStackTrace();
        }
    }
    private void setupItemGrid() {
        itemsGrid.getChildren().clear();

        itemsGrid.setHgap(12);
        itemsGrid.setVgap(20);
        itemsGrid.getColumnConstraints().clear();
        int colCount = 4;
        double colWidth = 210;

        for (int i = 0; i < colCount; ++i) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHalignment(HPos.LEFT);
            column.setPrefWidth(colWidth);
            column.setMaxWidth(colWidth);
            column.setMinWidth(colWidth);
            itemsGrid.getColumnConstraints().add(column);
        }
    }

    private VBox createItemCard(Listing listing) {
        // Create card layout
        VBox card = new VBox(10);
        card.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5;");
        card.setPrefWidth(200);
        card.setPrefHeight(250);
        card.setMaxWidth(200);
        card.setMaxHeight(250);

        // Create and configure image view
        ImageView imageView = new ImageView();
        imageView.setFitWidth(180);
        imageView.setFitHeight(140);
        imageView.setSmooth(true);
        imageView.setPreserveRatio(false);

        // Default image path
        String defaultImagePath = "/com/uninaswap/images/default_image.png";
        try {
            File imageFile = new File(listing.getImageUrl());
            imageView.setImage(new Image(imageFile.toURI().toString()));
        } catch (Exception e) {
            //System.out.println("Impossibile caricare l'immagine: " + e.getMessage());
            imageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(defaultImagePath))));
        }

        Label titleLabel = new Label(listing.getTitle());
        titleLabel.setWrapText(true);
        titleLabel.setStyle("-fx-font-weight: bold;");
        Label priceLabel;
        if(listing.getPrice() == null) {
            priceLabel = new Label("Disponibile per: " + listing.getType());
        } else {
            priceLabel = new Label("€" + listing.getPrice());
        }
        priceLabel.setStyle("-fx-font-size: 14px;");

        String labelText = listing.getCategory();
        if (labelText == null || labelText.isEmpty()) {
            labelText = listing.getType().toString();
        }
        Label categoryLabel = new Label(labelText);
        categoryLabel.setStyle("-fx-font-size: 12px; -fx-background-color: #f0f0f0; -fx-padding: 2 5; -fx-background-radius: 3;");

        card.getChildren().addAll(imageView, titleLabel, priceLabel, categoryLabel);
        card.setOnMouseClicked(event -> showItemDetails(event, listing));

        return card;
    }

    private void showItemDetails(MouseEvent event, Listing listing) {
        NavigationService.getInstance().navigateToProductDetailsView(event, listing);
    }

    @FXML
    private void onSearchButtonClicked() {

    }

    @FXML
    private void onLogoutButtonClicked(ActionEvent event) {
        UserSession.getInstance().logout();
        ValidationService.getInstance().showLogoutSuccess();
        try {
            NavigationService.getInstance().navigateToLoginView(event);
        } catch (Exception e) {
            ValidationService.getInstance().showFailedToOpenLoginPageError();
        }
    }

    @FXML
    private void onSellButtonClicked() {
        // TODO: Implement sell/proposals logic
        System.out.println("Sell/Proposals button clicked");
    }

    @FXML
    private void onApplyFiltersClicked() {
        // TODO: Implement apply filters logic
        System.out.println("Apply filters button clicked");
    }

    @FXML
    private void onResetFiltersClicked() {
        // TODO: Implement reset filters logic
        System.out.println("Reset filters button clicked");
    }

    @FXML
    private void onProfileButtonClicked(ActionEvent event) {
        NavigationService.getInstance().navigateToMyProfileView(event);

    }
    @FXML
    private void sliderPriceSelection() {
        double minPrice = priceSlider.getValue();
        double maxPrice = priceSlider.getMax();

        minPriceField.setText(String.format("%.2f", minPrice));
        maxPriceField.setText(String.valueOf((int)(maxPrice)));
    }

    // New handler methods
    @FXML
    private void onNotificationButtonClicked() {
        System.out.println("Notification button clicked");
    }

    @FXML
    private void onWishlistButtonClicked() {
        System.out.println("Wishlist button clicked");
    }

    @FXML
    private void onMessagesButtonClicked() {
        System.out.println("Messages button clicked");
    }




    @FXML
    private void onLoadMoreButtonClicked() {
        System.out.println("Loading more results");
    }

    @FXML
    private void onUserItemsButtonClicked() {
        System.out.println("My items button clicked");
    }

    @FXML
    private void onCategoryButtonClicked(ActionEvent event) {
        ToggleButton source = (ToggleButton) event.getSource();
        System.out.println("Selected category: " + source.getText());
    }

    @FXML
    private void onSortChanged() {
        System.out.println("Ordine cambiato a: " + sortComboBox.getValue());
    }

    @FXML
    private void onViewToggled(ActionEvent event) {
        boolean isList = listViewButton.isSelected();
        System.out.println("View changed to: " + (isList ? "List" : "Grid"));
    }
    private void conditionToggleGroupSettings(){
        ToggleGroup conditionToggleGroup = new ToggleGroup();
        allConditionsRadio.setToggleGroup(conditionToggleGroup);
        likeNewRadio.setToggleGroup(conditionToggleGroup);
        excellentRadio.setToggleGroup(conditionToggleGroup);
        goodRadio.setToggleGroup(conditionToggleGroup);
        likeNewRadio.setSelected(true);
    }
}