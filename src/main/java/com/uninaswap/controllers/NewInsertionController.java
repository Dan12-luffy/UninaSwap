package com.uninaswap.controllers; //TODO CLASSE NON FINITA, DA FINIRE

import com.uninaswap.model.Listing;
import com.uninaswap.dao.ListingDao;
import com.uninaswap.dao.ListingDaoImpl;
import com.uninaswap.model.ListingStatus;
import com.uninaswap.model.typeListing;
import com.uninaswap.services.NavigationService;
import com.uninaswap.services.UserSession;
import com.uninaswap.services.ValidationService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

public class NewInsertionController {
    @FXML private TextField titleField;
    @FXML private TextField imagePathField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button cancelButton;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private Label characterCountLabel;
    @FXML private Button previewButton;
    @FXML private Button saveAsDraftButton;
    @FXML private Button backButton;
    @FXML private Button previewFinalButton;
    @FXML private Button saveDraftButton;
    @FXML private ComboBox<String> locationComboBox;
    @FXML private ComboBox<String> deliveryComboBox;
    @FXML private ComboBox<String> contactComboBox;
    @FXML private ImageView logoImage;
    @FXML private ImageView mainImagePreview;

    //private final String defaultImagePath = "file:/home/dan/Desktop/UninaSwap/src/main/resources/com/uninaswap/images/default_image.png"; // Default image path portatile danilo
    //private final String defaultImagePath = "file:/home/pr/Desktop/UninaSwap/src/main/resources/com/uninaswap/images/default_image.png";
    private final String defaultImagePath = "file:/home/drc/Desktop/UninaSwap/src/main/resources/com/uninaswap/images/default_image.png"; // Default image path fisso danilo// Default image path fisso danilo
    private File selectedImageFile;

    @FXML
    private void initialize() {
        // Initialize comboboxes with values
        typeComboBox.getItems().addAll("Vendita", "Scambio", "Regalo");
        statusComboBox.getItems().addAll("Nuovo", "Come nuovo", "Buone condizioni", "Usato");
        categoryComboBox.getItems().addAll("Libri", "Appunti", "Elettronica", "Arredamento", "Abbigliamento", "Altro");
        locationComboBox.getItems().addAll("Monte Sant'Angelo", "Fuorigrotta", "Agnano", "Centro Storico");

        // Set default values
        typeComboBox.setValue("Vendita");
        statusComboBox.setValue("Usato");
        categoryComboBox.setValue("Altro");
        locationComboBox.setValue("Monte Sant'Angelo");
        mainImagePreview.setImage(new Image(defaultImagePath));
    }

    @FXML
    private void handleSelectImage() {
        selectImage();
    }
    private void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona un'immagine");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        selectedImageFile = fileChooser.showOpenDialog(null);

        if (selectedImageFile != null) {
            imagePathField.setText(selectedImageFile.getAbsolutePath());
            mainImagePreview.setImage(new Image(selectedImageFile.toURI().toString()));
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        try {
            if (titleField.getText().isEmpty() || descriptionArea.getText().isEmpty() ) {
                ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore di validazione", "Titolo e descrizione sono campi obbligatori.");
                return;
            }

            BigDecimal price = BigDecimal.valueOf(0.0);
            if (!priceField.getText().isEmpty()) {
                try {
                    price = BigDecimal.valueOf(Double.parseDouble(priceField.getText().replace(',', '.')));
                } catch (NumberFormatException e) {
                    ValidationService.getInstance().showAlert(Alert.AlertType.ERROR,
                            "Errore di validazione", "Il formato del prezzo non è valido.");
                    return;
                }
            }

            typeListing type = getTypeListing();
            String imagePath;
            if (selectedImageFile != null) {
                imagePath = selectedImageFile.getAbsolutePath() ;
            } else {
                imagePath = defaultImagePath;
            }

            Listing listing = new Listing(titleField.getText(), imagePath, descriptionArea.getText(), type, price, ListingStatus.AVAILABLE,
                    Date.valueOf(LocalDate.now()), UserSession.getInstance().getCurrentUser().getId(), categoryComboBox.getValue());

            ListingDao listingDao = new ListingDaoImpl();
            listingDao.insert(listing);
            ValidationService.getInstance().showAlert(Alert.AlertType.INFORMATION, "Inserzione salvata", "L'inserzione è stata salvata con successo!");
            NavigationService.getInstance().navigateToMainView(event);

        } catch (Exception e) {
            e.printStackTrace();
            ValidationService.getInstance().showNewInsertionError();
        }
    }

    @NotNull
    private typeListing getTypeListing() {
        typeListing type;
        String typeValue = typeComboBox.getValue();
        if ("Vendita".equals(typeValue)) {
            type = typeListing.SALE;
        } else if ("Scambio".equals(typeValue)) {
            type = typeListing.EXCHANGE;
        } else if ("Regalo".equals(typeValue)) {
            type = typeListing.GIFT;
        } else {
            type = typeListing.SALE; // Default value
        }
        return type;
    }


    @FXML
    private void goBack(ActionEvent event) {
        NavigationService.getInstance().navigateToMainView(event);
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }
}