package com.uninaswap.controllers;

import com.uninaswap.dao.CategoryDaoImpl;
import com.uninaswap.dao.ListingDao;
import com.uninaswap.dao.ListingDaoImpl;
import com.uninaswap.databaseUtils.FilterCriteria;
import com.uninaswap.model.Listing;
import com.uninaswap.model.User;
import com.uninaswap.model.typeListing;
import com.uninaswap.services.FilterService;
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



    private final int ALL_CATEGORIES_ID = -1; //Used to represent "All filter Categories", needed to clear the arraylist and filters
    private FilterCriteria currentFilter = new FilterCriteria();
    private final List<Integer> selectedCategories = new ArrayList<>();

    @FXML
    private void initialize() {
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            usernameLabel.setText("Ciao " + currentUser.getUsername());
        }
        sortComboBox.getItems().addAll("Più recenti", "Prezzo crescente", "Prezzo decrescente");
        sortComboBox.setValue("Più recenti");
        conditionToggleGroupSettings();
        //set up category buttons
        setUpCategoryButtons();
        // Select the "all conditions" option by default
        allConditionsRadio.setSelected(true);
        initializePriceRange();
        loadAllItems();
        maxPriceLabel.setText(((int)priceSlider.getMax() + "€"));
    }

    private void loadAllItems() {
        try {
            // Reset filtri
            currentFilter = new FilterCriteria();
            currentFilter.setSortBy("date_desc"); // Più recenti per default

            List<Listing> listings = FilterService.getInstance().searchListings(currentFilter);
            displayListings(listings);

        } catch (SQLException e) {
            resultsCountLabel.setText("Errore durante il caricamento");
            e.printStackTrace();
        }
    }
    private void initializePriceRange() {
        try {
            BigDecimal maxPrice = FilterService.getInstance().getMaxAvailablePrice();
            BigDecimal minPrice = FilterService.getInstance().getMinAvailablePrice();

            priceSlider.setMax(maxPrice.doubleValue());
            priceSlider.setMin(minPrice.doubleValue());
            priceSlider.setValue(minPrice.doubleValue());

            minPriceField.setText(minPrice.toString());
            maxPriceField.setText(maxPrice.toString());
            maxPriceLabel.setText(maxPrice + "€");
            minPriceLabel.setText(minPrice + "€");

        } catch (Exception e) {
            System.err.println("Errore nell'inizializzazione del range prezzi: " + e.getMessage());
            // Fallback ai valori di default
            priceSlider.setMax(1000.0);
            priceSlider.setMin(0.0);
        }
    }

    @FXML
    private void onApplyPriceButtonClicked() {
        try {
            double min = Double.parseDouble(minPriceField.getText().replace(',', '.'));
            double max = Double.parseDouble(maxPriceField.getText().replace(',', '.'));

            if (min > max) {
                ValidationService.getInstance().showInvalidPriceRangeError();
                return;
            }
            currentFilter.setMinPrice(BigDecimal.valueOf(min));
            currentFilter.setMaxPrice(BigDecimal.valueOf(max));
            applyCurrentFilters();

        } catch (NumberFormatException e) {
            ValidationService.getInstance().showInvalidPriceError();
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
    private void applyCurrentFilters() {
        try {
            List<Listing> listings = FilterService.getInstance().searchListings(currentFilter);
            displayListings(listings);
        } catch (SQLException e) {
            resultsCountLabel.setText("Errore durante il caricamento");
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
                itemsGrid.add(itemCard, column, row);

                column++;
                if (column > 3) {
                    column = 0;
                    row++;
                }
            }

            // Aggiorna contatore risultati
            if (listings.size() == 1) {
                resultsCountLabel.setText("Trovato 1 articolo");
            } else {
                resultsCountLabel.setText("Trovati " + listings.size() + " articoli");
            }
        } else {
            resultsCountLabel.setText("Trovati 0 articoli");
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
        if(listing.getType().equals(typeListing.GIFT) || listing.getType().equals(typeListing.EXCHANGE)) {
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
        String searchText = searchField.getText().trim();
        if (!searchText.isEmpty()) {
            currentFilter.setSearchText(searchText);
            applyCurrentFilters();
        }
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
        // Get selected condition
        /*if (likeNewRadio.isSelected()) {
            currentFilter.setStatus("LIKE_NEW");
        } else if (excellentRadio.isSelected()) {
            currentFilter.setStatus("EXCELLENT");
        } else if (goodRadio.isSelected()) {
            currentFilter.setStatus("GOOD");
        } else {
            // All conditions
            currentFilter.setStatus(null);
        }*/


        // Get faculty filter values
        List<String> selectedFaculties = new ArrayList<>();
        if (computerScienceCeck.isSelected()) selectedFaculties.add("Informatica");
        if (mathCeck.isSelected()) selectedFaculties.add("Matematica");
        if (economyCeck.isSelected()) selectedFaculties.add("Economia");
        if (artCeck.isSelected()) selectedFaculties.add("Arte");
        if (medicineCeck.isSelected()) selectedFaculties.add("Medicina");
        if (biologyCeck.isSelected()) selectedFaculties.add("Biologia");
        if (philosophyCeck.isSelected()) selectedFaculties.add("Filosofia");
        if (geographyCeck.isSelected()) selectedFaculties.add("Geografia");
        if (psicologyCeck.isSelected()) selectedFaculties.add("Psicologia");
        if (chemistryCeck.isSelected()) selectedFaculties.add("Chimica");
        if (astronomyCeck.isSelected()) selectedFaculties.add("Astronomia");
        if (tourismCeck.isSelected()) selectedFaculties.add("Turismo");
        if (linguisticsCeck.isSelected()) selectedFaculties.add("Linguistica");
        if (musicCeck.isSelected()) selectedFaculties.add("Musica");

        // Set faculty filter if any selected
        if (!selectedFaculties.isEmpty()) {
            currentFilter.setFacultyNames(selectedFaculties);
        } else {
            currentFilter.setFacultyNames(null);
        }
        //Get type filter values
        List<typeListing> selectedTypes = new ArrayList<>();
        if (saleCeck.isSelected()) selectedTypes.add(typeListing.SALE);
        if (swapCeck.isSelected()) selectedTypes.add(typeListing.EXCHANGE);
        if (giftCeck.isSelected()) selectedTypes.add(typeListing.GIFT);

        if(!selectedTypes.isEmpty()) {
            currentFilter.setTypes(selectedTypes);
        }
        else
            currentFilter.setTypes(null);

        //Esclude l'user loggato dai filtri
        currentFilter.setExcludeUserId(UserSession.getInstance().getCurrentUser().getId());
        applyCurrentFilters();
    }

    @FXML
    private void onResetFiltersClicked() {
        // Reset UI
        searchField.clear();
        allCategoryButton.setSelected(true);
        allConditionsRadio.setSelected(true);

        // Reset altri toggle buttons
        booksCategoryButton.setSelected(false);
        electronicCategoryButton.setSelected(false);
        clothingCategoryButton.setSelected(false);
        notesCategoryButton.setSelected(false);
        furnitureCategoryButton.setSelected(false);
        otherCategoryButton.setSelected(false);

        selectedCategories.clear();
        selectedCategories.add(ALL_CATEGORIES_ID);

        // Reset filtri
        currentFilter = new FilterCriteria();
        currentFilter.setSortBy("date_desc");
        initializePriceRange();

        applyCurrentFilters();
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

    private void toggleButtonsSettings() {
        CategoryDaoImpl categoryDao = new CategoryDaoImpl();

        allCategoryButton.setToggleGroup(null);
        booksCategoryButton.setToggleGroup(null);
        electronicCategoryButton.setToggleGroup(null);
        clothingCategoryButton.setToggleGroup(null);
        notesCategoryButton.setToggleGroup(null);
        furnitureCategoryButton.setToggleGroup(null);
        otherCategoryButton.setToggleGroup(null);

        allCategoryButton.setUserData(ALL_CATEGORIES_ID);
        booksCategoryButton.setUserData(categoryDao.getCategoryIdByName("Libri"));
        electronicCategoryButton.setUserData(categoryDao.getCategoryIdByName("Elettronica"));
        clothingCategoryButton.setUserData(categoryDao.getCategoryIdByName("Abbigliamento"));
        notesCategoryButton.setUserData(categoryDao.getCategoryIdByName("Appunti"));
        furnitureCategoryButton.setUserData(categoryDao.getCategoryIdByName("Arredamento"));
        otherCategoryButton.setUserData(categoryDao.getCategoryIdByName("Altro"));

    }

    private void setUpCategoryButtons() {

        toggleButtonsSettings();

        // "tutti" è selezionato di default
        allCategoryButton.setSelected(true);
        selectedCategories.add(ALL_CATEGORIES_ID);

        // do un azione a ciascun bottone
        allCategoryButton.setOnAction(e -> onCategoryButtonClicked(allCategoryButton));
        booksCategoryButton.setOnAction(e -> onCategoryButtonClicked(booksCategoryButton));
        electronicCategoryButton.setOnAction(e -> onCategoryButtonClicked(electronicCategoryButton));
        clothingCategoryButton.setOnAction(e -> onCategoryButtonClicked(clothingCategoryButton));
        notesCategoryButton.setOnAction(e -> onCategoryButtonClicked(notesCategoryButton));
        furnitureCategoryButton.setOnAction(e -> onCategoryButtonClicked(furnitureCategoryButton));
        otherCategoryButton.setOnAction(e -> onCategoryButtonClicked(otherCategoryButton));
    }


    @FXML
    private void onCategoryButtonClicked(ToggleButton button) {
        CategoryDaoImpl categoryDao = new CategoryDaoImpl();

        String categoryName = button.getText();
        if (categoryName.equals("Tutto")) {
            // se "tuttO" è selezionato, deseleziona gli altri
            booksCategoryButton.setSelected(false);
            electronicCategoryButton.setSelected(false);
            clothingCategoryButton.setSelected(false);
            notesCategoryButton.setSelected(false);
            furnitureCategoryButton.setSelected(false);
            otherCategoryButton.setSelected(false);
            selectedCategories.clear();
            selectedCategories.add(ALL_CATEGORIES_ID);
            allCategoryButton.setSelected(true);
        } else {
            if (button.isSelected()) {
                //se una specifica categoria è selezionata, rimuovi "tutti" se è selezionato
                if (selectedCategories.contains(ALL_CATEGORIES_ID)) {
                    selectedCategories.remove(Integer.valueOf(ALL_CATEGORIES_ID));
                    allCategoryButton.setSelected(false);
                }
                selectedCategories.add(categoryDao.getCategoryIdByName(categoryName));
            } else {
                selectedCategories.remove(categoryDao.getCategoryIdByName(categoryName));

                // se tutte le categorie sono deselezionate, aggiungi "tutti" e selezionalo
                if (selectedCategories.isEmpty()) {
                    selectedCategories.add(ALL_CATEGORIES_ID);
                    allCategoryButton.setSelected(true);
                }
            }
        }

        filterItemsByMultipleCategories();
    }

    // Add new filter method for multiple categories
    private void filterItemsByMultipleCategories() {
        if (selectedCategories.contains(ALL_CATEGORIES_ID)) {
            currentFilter.setCategoryIds(null);
        } else {
            currentFilter.setCategoryIds(new ArrayList<>(selectedCategories));
        }
        applyCurrentFilters();
    }

    //meetodo per il database che cerca per più categorie
    private List<Listing> findByMultipleCategories(List<Integer> categoryIds) throws SQLException {
        List<Listing> allListings = new ArrayList<>();
        ListingDao listingDao = new ListingDaoImpl();

        for (Integer categoryId : categoryIds) {
            allListings.addAll(listingDao.findByCategory(categoryId));
        }

        return allListings;
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
            ValidationService.getInstance().showFailedToOpenPageError();
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
        String selectedSort = sortComboBox.getValue();
        String sortBy = switch (selectedSort) {
            case "Prezzo crescente" -> "price_asc";
            case "Prezzo decrescente" -> "price_desc";
            default -> "date_desc";
        };

        currentFilter.setSortBy(sortBy);
        applyCurrentFilters();
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