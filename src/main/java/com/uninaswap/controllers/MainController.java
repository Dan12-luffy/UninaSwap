package com.uninaswap.controllers;

import com.kitfox.svg.A;
import com.uninaswap.dao.CategoryDaoImpl;
import com.uninaswap.dao.ListingDao;
import com.uninaswap.dao.ListingDaoImpl;
import com.uninaswap.databaseUtils.FilterCriteria;
import com.uninaswap.model.Listing;
import com.uninaswap.model.User;
import com.uninaswap.model.typeListing;
import com.uninaswap.services.*;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
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
    @FXML private ToggleGroup categoryTogglegroup;
    @FXML private ToggleButton allCategoryButton;
    @FXML private ToggleButton booksCategoryButton;
    @FXML private ToggleButton electronicCategoryButton;
    @FXML private ToggleButton clothingCategoryButton;
    @FXML private ToggleButton notesCategoryButton;
    @FXML private ToggleButton furnitureCategoryButton;
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
    @FXML private ComboBox<String> sortComboBox;
    @FXML private ToggleButton listViewButton;
    @FXML private ToggleButton gridViewButton;
    @FXML private GridPane itemsGrid;
    @FXML private ScrollPane itemsScrollPane;
    @FXML private Label resultsCountLabel;
    @FXML private Label maxPriceLabel;
    @FXML private Label minPriceLabel;
    @FXML private CheckBox computerScienceCeck;
    @FXML private CheckBox scienceCeck;
    @FXML private CheckBox mathCeck;
    @FXML private CheckBox economyCeck;
    @FXML private CheckBox artCeck;
    @FXML private CheckBox medicineCeck;
    @FXML private CheckBox biologyCeck;
    @FXML private CheckBox philosophyCeck;
    @FXML private CheckBox geographyCeck;
    @FXML private CheckBox psicologyCeck;
    @FXML private CheckBox chemistryCeck;
    @FXML private CheckBox astronomyCeck;
    @FXML private CheckBox tourismCeck;
    @FXML private CheckBox linguisticsCeck;
    @FXML private CheckBox musicCeck;
    @FXML private CheckBox saleCeck;
    @FXML private CheckBox swapCeck;
    @FXML private CheckBox giftCeck;



    //TODO migliora la gestione delle eccezioni.
    private final int ALL_CATEGORIES_ID = -1; //Used to represent "All filter Categories", needed to clear the arraylist and filters
    private FilterCriteria currentFilter = new FilterCriteria();
    private final List<Integer> selectedCategories = new ArrayList<>();
    private final CategoryService categoryService = CategoryService.getInstance();
    private final UserSession userSession = UserSession.getInstance();
    private final FilterService filterService = FilterService.getInstance();
    private final ValidationService validationService = ValidationService.getInstance();

    @FXML
    private void initialize() {
        User currentUser = userSession.getCurrentUser();
        if (currentUser != null) {
            this.usernameLabel.setText("Ciao " + currentUser.getUsername());
        }
        this.sortComboBox.getItems().addAll("Più recenti", "Prezzo crescente", "Prezzo decrescente");
        this.sortComboBox.setValue("Più recenti");
        conditionToggleGroupSettings();
        //set up category buttons
        try {
            setUpCategoryButtons();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Select the "all conditions" option by default
        this.allConditionsRadio.setSelected(true);
        initializePriceRange();
        loadAllItems();
        this.maxPriceLabel.setText(((int)this.priceSlider.getMax() + "€"));
        wishlistButton.setOnAction(event -> showFavorites());
        searchField.focusedProperty().addListener((_, _, _) -> {
            searchButton.setDefaultButton(true);
        });
    }

    private void loadAllItems() {
        try {
            // Reset filtri
            this.currentFilter = new FilterCriteria();
            this.currentFilter.setSortBy("date_desc"); // Più recenti per default

            List<Listing> listings = filterService.searchListings(this.currentFilter);
            displayListings(listings);

        } catch (SQLException e) {
            this.resultsCountLabel.setText("Errore durante il caricamento");
            e.printStackTrace();
        }
    }
    public void showFavorites() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/favouriteInterface.fxml"));
            Parent root = loader.load();

            FavouritesViewController controller = loader.getController();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("I miei preferiti");
            stage.setScene(scene);
            stage.setResizable(false);

            stage.initOwner(wishlistButton.getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            validationService.showAlert(Alert.AlertType.ERROR,
                    "Errore",
                    "Impossibile visualizzare i preferiti: " + e.getMessage());
        }
    }
    private void initializePriceRange() {
        try {
            BigDecimal maxPrice = filterService.getMaxAvailablePrice();
            BigDecimal minPrice = filterService.getMinAvailablePrice();

            this.priceSlider.setMax(maxPrice.doubleValue());
            this.priceSlider.setMin(minPrice.doubleValue());
            this.priceSlider.setValue(minPrice.doubleValue());

            this.minPriceField.setText(minPrice.toString());
            this.maxPriceField.setText(maxPrice.toString());
            this.minPriceLabel.setText(minPrice + "€");

        } catch (Exception e) {
            System.err.println("Errore nell'inizializzazione del range prezzi: " + e.getMessage());
            // Fallback ai valori di default
            this.priceSlider.setMax(1000.0);
            this.priceSlider.setMin(0.0);
        }
    }

    @FXML
    private void onApplyPriceButtonClicked() {
        try {
            double min = Double.parseDouble(this.minPriceField.getText().replace(',', '.'));
            double max = Double.parseDouble(this.maxPriceField.getText().replace(',', '.'));

            if (min > max) {
                this.validationService.showInvalidPriceRangeError();
                return;
            }
            this.currentFilter.setMinPrice(BigDecimal.valueOf(min));
            this.currentFilter.setMaxPrice(BigDecimal.valueOf(max));
            applyCurrentFilters();

        } catch (NumberFormatException e) {
            validationService.showInvalidPriceError();
        }
    }

    /*private void displayFilteredListings(List<Listing> listings) {
        if (!listings.isEmpty()) {
            int column = 0;
            int row = 0;
            for (Listing listing : listings) {
                VBox itemCard = createItemCard(listing);
                itemsGrid.add(itemCard, column, row);

                column++;

                if (column > 3) {  // Max 4 columns
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
    }*/


    private void setupItemGrid() {
        this.itemsGrid.getChildren().clear();
        this.itemsGrid.setHgap(12);
        this.itemsGrid.setVgap(20);
        this.itemsGrid.getColumnConstraints().clear();
        int colCount = 4;
        double colWidth = 210;

        for (int i = 0; i < colCount; ++i) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHalignment(HPos.LEFT);
            column.setPrefWidth(colWidth);
            column.setMaxWidth(colWidth);
            column.setMinWidth(colWidth);
            this.itemsGrid.getColumnConstraints().add(column);
        }
    }
    private void applyCurrentFilters() {
        try {
            List<Listing> listings = filterService.searchListings(currentFilter);
            displayListings(listings);
        } catch (SQLException e) {
            this.resultsCountLabel.setText("Errore durante il caricamento");
            e.printStackTrace();
        }
    }
    private void displayListings(List<Listing> listings) {
        setupItemGrid();

        if (!listings.isEmpty()) {
            int column = 0;
            int row = 0;

            for (Listing listing : listings) {
                VBox itemCard = createItemCard(listing);
                this.itemsGrid.add(itemCard, column, row);

                column++;
                if (column > 3) {
                    column = 0;
                    row++;
                }
            }

            // Aggiorna contatore risultati
            if (listings.size() == 1) {
                this.resultsCountLabel.setText("Trovato 1 articolo");
            } else {
                this.resultsCountLabel.setText("Trovati " + listings.size() + " articoli");
            }
        } else {
            this.resultsCountLabel.setText("Trovati 0 articoli");
        }
    }

    private VBox createItemCard(Listing listing) {
        // Create card layout
        VBox card = new VBox(10);
        card.getStyleClass().add("item-card");
        card.setPrefWidth(200);
        card.setPrefHeight(250);
        card.setMaxWidth(200);
        card.setMaxHeight(250);

        // Create and configure image view
        ImageView imageView = new ImageView();
        imageView.getStyleClass().add("item-image");
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
            imageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(defaultImagePath))));
        }

        Label titleLabel = new Label(listing.getTitle());
        titleLabel.getStyleClass().add("item-title");

        Label priceLabel;
        if(listing.getType().equals(typeListing.GIFT) || listing.getType().equals(typeListing.EXCHANGE)) {
            priceLabel = new Label("Disponibile per: " + listing.getType());
        } else {
            priceLabel = new Label("€" + listing.getPrice());
        }
        priceLabel.getStyleClass().add("item-price");

        String labelText = listing.getCategory();
        if (labelText == null || labelText.isEmpty()) {
            labelText = listing.getType().toString();
        }
        Label categoryLabel = new Label(labelText);
        categoryLabel.getStyleClass().add("item-category");

        card.getChildren().addAll(imageView, titleLabel, priceLabel, categoryLabel);
        card.setOnMouseClicked(event -> showItemDetails(event, listing));

        return card;
    }

    private void showItemDetails(MouseEvent event, Listing listing) {
        NavigationService.getInstance().navigateToProductDetailsView(event, listing);
    }

    @FXML
    private void onSearchButtonClicked() {
        String searchText = this.searchField.getText().trim();
        if (!searchText.isEmpty()) {
            this.currentFilter.setSearchText(searchText);
            try {
                List<Listing> listings = this.filterService.searchByText(searchText);
                displayListings(listings);
            } catch (SQLException e) {
                this.resultsCountLabel.setText("Errore durante il caricamento");
                e.printStackTrace();
            }
        }
        else{
            this.currentFilter.setSearchText(null);
            applyCurrentFilters();
        }
    }

    @FXML
    private void onLogoutButtonClicked(ActionEvent event) {
        UserSession.getInstance().logout();
        validationService.showLogoutSuccess();
        try {
            NavigationService.getInstance().navigateToLoginView(event);
        } catch (Exception e) {
            validationService.showFailedToOpenLoginPageError();
        }
    }

    @FXML
    private void onSellButtonClicked() {
        // TODO: Implement sell/proposals logic
        System.out.println("Sell/Proposals button clicked");
    }

    @FXML
    private void onApplyFiltersClicked() {

        // Get faculty filter values
        List<String> selectedFaculties = new ArrayList<>();
        if (this.computerScienceCeck.isSelected()) selectedFaculties.add("Informatica");
        if(this.scienceCeck.isSelected()) selectedFaculties.add("Scienze");
        if (this.mathCeck.isSelected()) selectedFaculties.add("Matematica");
        if (this.economyCeck.isSelected()) selectedFaculties.add("Economia");
        if (this.artCeck.isSelected()) selectedFaculties.add("Arte");
        if (this.medicineCeck.isSelected()) selectedFaculties.add("Medicina");
        if (this.biologyCeck.isSelected()) selectedFaculties.add("Biologia");
        if (this.philosophyCeck.isSelected()) selectedFaculties.add("Filosofia");
        if (this.geographyCeck.isSelected()) selectedFaculties.add("Geografia");
        if (this.psicologyCeck.isSelected()) selectedFaculties.add("Psicologia");
        if (this.chemistryCeck.isSelected()) selectedFaculties.add("Chimica");
        if (this.astronomyCeck.isSelected()) selectedFaculties.add("Astronomia");
        if (this.tourismCeck.isSelected()) selectedFaculties.add("Turismo");
        if (this.linguisticsCeck.isSelected()) selectedFaculties.add("Linguistica");
        if (this.musicCeck.isSelected()) selectedFaculties.add("Musica");

        // Set faculty filter if any selected
        if (!selectedFaculties.isEmpty()) {
            this.currentFilter.setFacultyNames(selectedFaculties);
        } else {
            this.currentFilter.setFacultyNames(null);
        }
        //Get type filter values
        List<typeListing> selectedTypes = new ArrayList<>();
        if (this.saleCeck.isSelected()) selectedTypes.add(typeListing.SALE);
        if (this.swapCeck.isSelected()) selectedTypes.add(typeListing.EXCHANGE);
        if (this.giftCeck.isSelected()) selectedTypes.add(typeListing.GIFT);

        if(!selectedTypes.isEmpty()) {
            this.currentFilter.setTypes(selectedTypes);
        }
        else
            this.currentFilter.setTypes(null);

        //Esclude l'user loggato dai filtri
        this.currentFilter.setExcludeUserId(UserSession.getInstance().getCurrentUser().getId());
        applyCurrentFilters();
    }

    @FXML
    private void onResetFiltersClicked() {
        // Reset UI
        this.searchField.clear();
        this.allCategoryButton.setSelected(true);
        this.allConditionsRadio.setSelected(true);

        // Reset altri toggle buttons
        this.booksCategoryButton.setSelected(false);
        this.electronicCategoryButton.setSelected(false);
        this.clothingCategoryButton.setSelected(false);
        this.notesCategoryButton.setSelected(false);
        this.furnitureCategoryButton.setSelected(false);
        this.otherCategoryButton.setSelected(false);

        this.selectedCategories.clear();
        this.selectedCategories.add(ALL_CATEGORIES_ID);

        // Reset filtri
        this.currentFilter = new FilterCriteria();
        this.currentFilter.setSortBy("date_desc");
        initializePriceRange();

        applyCurrentFilters();
    }

    @FXML
    private void onProfileButtonClicked(ActionEvent event) {
        NavigationService.getInstance().navigateToMyProfileView(event);

    }

    @FXML
    private void sliderPriceSelection() {
        double minPrice = this.priceSlider.getValue();
        double maxPrice = this.priceSlider.getMax();

        this.minPriceField.setText(String.format("%.2f", minPrice));
        this.maxPriceField.setText(String.valueOf((int)(maxPrice)));
    }

    private void toggleButtonsSettings() throws Exception {
        //CategoryDaoImpl categoryDao = new CategoryDaoImpl();

        this.allCategoryButton.setToggleGroup(null);
        this.booksCategoryButton.setToggleGroup(null);
        this.electronicCategoryButton.setToggleGroup(null);
        this.clothingCategoryButton.setToggleGroup(null);
        this.notesCategoryButton.setToggleGroup(null);
        this.furnitureCategoryButton.setToggleGroup(null);
        this.otherCategoryButton.setToggleGroup(null);

        this.allCategoryButton.setUserData(ALL_CATEGORIES_ID);
        this.booksCategoryButton.setUserData(categoryService.getCategoryIdByName("Libri"));
        this.electronicCategoryButton.setUserData(categoryService.getCategoryIdByName("Elettronica"));
        this.clothingCategoryButton.setUserData(categoryService.getCategoryIdByName("Abbigliamento"));
        this.notesCategoryButton.setUserData(categoryService.getCategoryIdByName("Appunti"));
        this.furnitureCategoryButton.setUserData(categoryService.getCategoryIdByName("Arredamento"));
        this.otherCategoryButton.setUserData(categoryService.getCategoryIdByName("Altro"));
    }

    private void setUpCategoryButtons() throws Exception {

        toggleButtonsSettings();

        // "tutti" è selezionato di default
        this.allCategoryButton.setSelected(true);
        this.selectedCategories.add(ALL_CATEGORIES_ID);

        // do un azione a ciascun bottone
        this.allCategoryButton.setOnAction(e -> {
            try {
                onCategoryButtonClicked(allCategoryButton);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        this.booksCategoryButton.setOnAction(e -> onCategoryButtonClicked(booksCategoryButton));
        this.electronicCategoryButton.setOnAction(e -> onCategoryButtonClicked(electronicCategoryButton));
        this.clothingCategoryButton.setOnAction(e -> onCategoryButtonClicked(clothingCategoryButton));
        this.notesCategoryButton.setOnAction(e -> onCategoryButtonClicked(notesCategoryButton));
        this.furnitureCategoryButton.setOnAction(e -> onCategoryButtonClicked(furnitureCategoryButton));
        this.otherCategoryButton.setOnAction(e -> onCategoryButtonClicked(otherCategoryButton));
    }


    @FXML
    private void onCategoryButtonClicked(ToggleButton button) {
        try {
            String categoryName = button.getText();
            if (categoryName.equals("Tutto")) {
                this.booksCategoryButton.setSelected(false);
                this.electronicCategoryButton.setSelected(false);
                this.clothingCategoryButton.setSelected(false);
                this.notesCategoryButton.setSelected(false);
                this.furnitureCategoryButton.setSelected(false);
                this.otherCategoryButton.setSelected(false);
                this.selectedCategories.clear();
                this.selectedCategories.add(ALL_CATEGORIES_ID);
                this.allCategoryButton.setSelected(true);
            } else {
                int categoryId = categoryService.getCategoryIdByName(categoryName);
                if (button.isSelected()) {
                    if (this.selectedCategories.contains(ALL_CATEGORIES_ID)) {
                        this.selectedCategories.remove(Integer.valueOf(ALL_CATEGORIES_ID));
                        this.allCategoryButton.setSelected(false);
                    }
                    if (categoryId != -ALL_CATEGORIES_ID) {
                        this.selectedCategories.add(categoryId);
                    }
                } else {
                    if (categoryId != ALL_CATEGORIES_ID) {
                        this.selectedCategories.remove(Integer.valueOf(categoryId));
                    }
                    if (this.selectedCategories.isEmpty()) {
                        this.selectedCategories.add(ALL_CATEGORIES_ID);
                        this.allCategoryButton.setSelected(true);
                    }
                }
            }
            filterItemsByMultipleCategories();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Add new filter method for multiple categories
    private void filterItemsByMultipleCategories() {
        if (this.selectedCategories.contains(ALL_CATEGORIES_ID)) {
            this.currentFilter.setCategoryIds(null);
        } else {
            this.currentFilter.setCategoryIds(new ArrayList<>(selectedCategories));
        }
        applyCurrentFilters();
    }


    // New handler methods
    @FXML
    private void onNotificationButtonClicked() {
        System.out.println("Notification button clicked");
    }

    @FXML
    private void onSellButtonClicked(ActionEvent event) {
        try {
            NavigationService.getInstance().navigateToCreateListingView(event);
        }catch(Exception e){
            validationService.showFailedToOpenPageError();
        }
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
    private void onSortChanged() {
        String selectedSort = this.sortComboBox.getValue();
        String sortBy = switch (selectedSort) {
            case "Prezzo crescente" -> "price_asc";
            case "Prezzo decrescente" -> "price_desc";
            default -> "date_desc";
        };

        this.currentFilter.setSortBy(sortBy);
        applyCurrentFilters();
    }

    @FXML
    private void onViewToggled(ActionEvent event) {
        boolean isList = listViewButton.isSelected();
        System.out.println("View changed to: " + (isList ? "List" : "Grid"));
    }

    private void conditionToggleGroupSettings(){
        ToggleGroup conditionToggleGroup = new ToggleGroup();
        this.allConditionsRadio.setToggleGroup(conditionToggleGroup);
        this.likeNewRadio.setToggleGroup(conditionToggleGroup);
        this.excellentRadio.setToggleGroup(conditionToggleGroup);
        this.goodRadio.setToggleGroup(conditionToggleGroup);
        this.likeNewRadio.setSelected(true);
    }
}