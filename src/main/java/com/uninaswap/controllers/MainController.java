package com.uninaswap.controllers;

import com.uninaswap.databaseUtils.FilterCriteria;
import com.uninaswap.model.Insertion;
import com.uninaswap.model.Offer;
import com.uninaswap.model.User;
import com.uninaswap.model.typeInsertion;
import com.uninaswap.services.*;
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
    @FXML private ToggleButton listViewButton;
    @FXML private ToggleButton gridViewButton;
    @FXML private GridPane itemsGrid;
    @FXML private ScrollPane itemsScrollPane;
    @FXML private Label resultsCountLabel;
    @FXML private Label maxPriceLabel;
    @FXML private Label minPriceLabel;
    @FXML private Label notificationCountLabel;
    @FXML private CheckBox computerScienceCheck;
    @FXML private CheckBox scienceCheck;
    @FXML private CheckBox mathCheck;
    @FXML private CheckBox economyCheck;
    @FXML private CheckBox artCheck;
    @FXML private CheckBox medicineCheck;
    @FXML private CheckBox biologyCheck;
    @FXML private CheckBox philosophyCheck;
    @FXML private CheckBox geographyCheck;
    @FXML private CheckBox psychologyCheck;
    @FXML private CheckBox chemistryCheck;
    @FXML private CheckBox astronomyCheck;
    @FXML private CheckBox tourismCheck;
    @FXML private CheckBox linguisticsCheck;
    @FXML private CheckBox musicCheck;
    @FXML private CheckBox saleCheck;
    @FXML private CheckBox swapCheck;
    @FXML private CheckBox giftCheck;



    //TODO migliora la gestione delle eccezioni.
    private final int ALL_CATEGORIES_ID = -1; //Used to represent "All filter Categories", needed to clear the arraylist and filters
    private FilterCriteria currentFilter = new FilterCriteria();
    private final List<Integer> selectedCategories = new ArrayList<>();
    private final CategoryService categoryService = CategoryService.getInstance();
    private final UserSession userSession = UserSession.getInstance();
    private final FilterService filterService = FilterService.getInstance();
    private final ValidationService validationService = ValidationService.getInstance();
    private final OfferService offerService = OfferService.getInstance();

    @FXML
    private void initialize() throws Exception {
        User currentUser = userSession.getCurrentUser();
        if (currentUser != null) {
            this.usernameLabel.setText("Ciao " + currentUser.getUsername());
        }
        //Aggiorna il contatore delle notifiche
        List<Offer> offers = offerService.getOffersToCurrentUser();
        List<Offer> rejectedOffers = offerService.getRejectedOffersForCurrentUser();
        int totalNotifications = offers.size() + rejectedOffers.size();
        this.notificationCountLabel.setText(String.valueOf(totalNotifications));


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
        searchField.focusedProperty().addListener((_, _, _) -> {
            searchButton.setDefaultButton(true);
        });
    }

    private void loadAllItems() {
        try {
            // Reset filtri
            this.currentFilter = new FilterCriteria();
            this.currentFilter.setSortBy("date_desc"); // Più recenti per default

            List<Insertion> insertions = filterService.searchListings(this.currentFilter);
            displayListings(insertions);

        } catch (SQLException e) {
            this.resultsCountLabel.setText("Errore durante il caricamento");
            e.printStackTrace();
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
            this.priceSlider.setMax(3000.0);
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
            List<Insertion> insertions = filterService.searchListings(currentFilter);
            displayListings(insertions);
        } catch (SQLException e) {
            this.resultsCountLabel.setText("Errore durante il caricamento");
            e.printStackTrace();
        }
    }
    private void displayListings(List<Insertion> insertions) {
        setupItemGrid();

        if (!insertions.isEmpty()) {
            int column = 0;
            int row = 0;

            for (Insertion insertion : insertions) {
                VBox itemCard = createItemCard(insertion);
                this.itemsGrid.add(itemCard, column, row);

                column++;
                if (column > 3) {
                    column = 0;
                    row++;
                }
            }

            // Aggiorna contatore risultati
            if (insertions.size() == 1) {
                this.resultsCountLabel.setText("Trovato 1 articolo");
            } else {
                this.resultsCountLabel.setText("Trovati " + insertions.size() + " articoli");
            }
        } else {
            this.resultsCountLabel.setText("Trovati 0 articoli");
        }
    }
    private VBox createItemCard(Insertion insertion) {
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
            File imageFile = new File(insertion.getImageUrl());
            imageView.setImage(new Image(imageFile.toURI().toString()));
        } catch (Exception e) {
            //System.out.println("Impossibile caricare l'immagine: " + e.getMessage());
            imageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(defaultImagePath))));
        }

        Label titleLabel = new Label(insertion.getTitle());
        titleLabel.setWrapText(true);
        titleLabel.setStyle("-fx-font-weight: bold;");
        Label priceLabel;
        if(insertion.getType().equals(typeInsertion.GIFT) || insertion.getType().equals(typeInsertion.EXCHANGE)) {
            priceLabel = new Label("Disponibile per: " + insertion.getType());
        } else {
            priceLabel = new Label("€" + insertion.getPrice());
        }
        priceLabel.setStyle("-fx-font-size: 14px;");

        String labelText = insertion.getCategory();
        if (labelText == null || labelText.isEmpty()) {
            labelText = insertion.getType().toString();
        }
        Label categoryLabel = new Label(labelText);
        categoryLabel.setStyle("-fx-font-size: 12px; -fx-background-color: #f0f0f0; -fx-padding: 2 5; -fx-background-radius: 3;");

        card.getChildren().addAll(imageView, titleLabel, priceLabel, categoryLabel);
        card.setOnMouseClicked(event -> showItemDetails(event, insertion));

        return card;
    }

    private void showItemDetails(MouseEvent event, Insertion insertion) {
        NavigationService.getInstance().navigateToProductDetailsView(event, insertion);
    }

    @FXML
    private void onSearchButtonClicked() {
        String searchText = this.searchField.getText().trim();
        if (!searchText.isEmpty()) {
            this.currentFilter.setSearchText(searchText);
            try {
                List<Insertion> insertions = this.filterService.searchByText(searchText);
                displayListings(insertions);
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
        if (this.computerScienceCheck.isSelected()) selectedFaculties.add("Informatica");
        if(this.scienceCheck.isSelected()) selectedFaculties.add("Scienze");
        if (this.mathCheck.isSelected()) selectedFaculties.add("Matematica");
        if (this.economyCheck.isSelected()) selectedFaculties.add("Economia");
        if (this.artCheck.isSelected()) selectedFaculties.add("Arte");
        if (this.medicineCheck.isSelected()) selectedFaculties.add("Medicina");
        if (this.biologyCheck.isSelected()) selectedFaculties.add("Biologia");
        if (this.philosophyCheck.isSelected()) selectedFaculties.add("Filosofia");
        if (this.geographyCheck.isSelected()) selectedFaculties.add("Geografia");
        if (this.psychologyCheck.isSelected()) selectedFaculties.add("Psicologia");
        if (this.chemistryCheck.isSelected()) selectedFaculties.add("Chimica");
        if (this.astronomyCheck.isSelected()) selectedFaculties.add("Astronomia");
        if (this.tourismCheck.isSelected()) selectedFaculties.add("Turismo");
        if (this.linguisticsCheck.isSelected()) selectedFaculties.add("Linguistica");
        if (this.musicCheck.isSelected()) selectedFaculties.add("Musica");

        // Set faculty filter if any selected
        if (!selectedFaculties.isEmpty()) {
            this.currentFilter.setFacultyNames(selectedFaculties);
        } else {
            this.currentFilter.setFacultyNames(null);
        }
        //Get type filter values
        List<typeInsertion> selectedTypes = new ArrayList<>();
        if (this.saleCheck.isSelected()) selectedTypes.add(typeInsertion.SALE);
        if (this.swapCheck.isSelected()) selectedTypes.add(typeInsertion.EXCHANGE);
        if (this.giftCheck.isSelected()) selectedTypes.add(typeInsertion.GIFT);

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
                    if (categoryId != ALL_CATEGORIES_ID) {
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

    @FXML
    private void onNotificationButtonClicked(ActionEvent event) {
        NavigationService.getInstance().navigateToNotificationsView(event);
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






    private void conditionToggleGroupSettings(){
        ToggleGroup conditionToggleGroup = new ToggleGroup();
        this.allConditionsRadio.setToggleGroup(conditionToggleGroup);
        this.likeNewRadio.setToggleGroup(conditionToggleGroup);
        this.excellentRadio.setToggleGroup(conditionToggleGroup);
        this.goodRadio.setToggleGroup(conditionToggleGroup);
        this.likeNewRadio.setSelected(true);
    }
}