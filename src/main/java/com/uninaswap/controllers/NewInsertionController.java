package com.uninaswap.controllers; //TODO CLASSE NON FINITA, DA FINIRE

import com.uninaswap.model.Insertion;
import com.uninaswap.model.InsertionFactory;
import com.uninaswap.model.InsertionStatus;
import com.uninaswap.model.typeInsertion;
import com.uninaswap.services.InsertionService;
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
import java.math.BigDecimal;
import java.time.LocalDate;

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
    @FXML private Label priceLabel;
    @FXML private ImageView logoImage;
    @FXML private ImageView mainImagePreview;

    //private final String defaultImagePath = "file:/home/dan/Desktop/UninaSwap/src/main/resources/com/uninaswap/images/default_image.png"; // Default image path portatile danilo
    //private final String defaultImagePath = "file:/home/pr/Desktop/UninaSwap/src/main/resources/com/uninaswap/images/default_image.png";
    private final String defaultImagePath = "file:/home/drc/Desktop/UninaSwap/src/main/resources/com/uninaswap/images/default_image.png"; // Default image path fisso danilo// Default image path fisso danilo
    private File selectedImageFile;
    private final InsertionService insertionService = InsertionService.getInstance();

    @FXML
    private void initialize() {
        // Initialize comboboxes with values
        this.typeComboBox.getItems().addAll("Vendita", "Scambio", "Regalo");
        this.statusComboBox.getItems().addAll("Nuovo", "Come nuovo", "Buone condizioni", "Usato");
        this.categoryComboBox.getItems().addAll("Libri", "Appunti", "Elettronica", "Arredamento", "Abbigliamento", "Altro");
        this.locationComboBox.getItems().addAll("Monte Sant'Angelo", "Fuorigrotta", "Agnano", "Centro Storico");

        // Set default values
        this.typeComboBox.setValue("Vendita");
        this.statusComboBox.setValue("Usato");
        this.categoryComboBox.setValue("Altro");
        this.locationComboBox.setValue("Monte Sant'Angelo");
        this.mainImagePreview.setImage(new Image(this.defaultImagePath));

        this.typeComboBox.valueProperty().addListener((_, _, _) -> {
            if(this.typeComboBox.getValue().equals("Regalo")){
                this.priceField.setDisable(true);
            } else if (this.typeComboBox.getValue().equals("Scambio")) {
                this.priceLabel.setText("Valore dell'oggetto: ");
            }
            else{
                this.priceField.setDisable(false);
                this.priceLabel.setText("Prezzo: ");
            }

        });
    }

    @FXML
    private void handleSelectImage() {
        selectImage();
    }
    private void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona un'immagine");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        this.selectedImageFile = fileChooser.showOpenDialog(null);

        if (this.selectedImageFile != null) {
            this.imagePathField.setText(this.selectedImageFile.getAbsolutePath());
            this.mainImagePreview.setImage(new Image(this.selectedImageFile.toURI().toString()));
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        try {
            if (this.titleField.getText().isEmpty() || this.descriptionArea.getText().isEmpty() ) {
                ValidationService.getInstance().showNewInsertionMissingCampsError();
                return;
            }
            typeInsertion type = getTypeListing();

            BigDecimal price = BigDecimal.valueOf(0.0);
            if (!this.priceField.getText().isEmpty()) {
                try {
                    price = BigDecimal.valueOf(Double.parseDouble(this.priceField.getText().replace(',', '.')));
                } catch (NumberFormatException e) {
                    ValidationService.getInstance().showPriceFormatError();
                    return;
                }
            }

            String imagePath;
            if (this.selectedImageFile != null) {
                imagePath = this.selectedImageFile.getAbsolutePath() ;
            } else {
                imagePath = this.defaultImagePath;
            }

            Insertion insertion = InsertionFactory.createListing(
                    this.titleField.getText(),
                    imagePath,
                    this.descriptionArea.getText(),
                    type,
                    price,
                    InsertionStatus.AVAILABLE,
                    LocalDate.now(),
                    UserSession.getInstance().getCurrentUser().getId(),
                    this.categoryComboBox.getValue()
            );

            insertionService.createInsertion(insertion);
            ValidationService.getInstance().showNewInsertionSuccess();
            NavigationService.getInstance().navigateToMyProfileView(event);

        } catch (Exception e) {
            e.printStackTrace();
            ValidationService.getInstance().showNewInsertionError();
        }
    }

    @NotNull
    private typeInsertion getTypeListing() {
        typeInsertion type;
        String typeValue = this.typeComboBox.getValue();
        type = switch (typeValue) {
            case "Vendita" -> typeInsertion.SALE;
            case "Scambio" -> typeInsertion.EXCHANGE;
            case "Regalo" -> typeInsertion.GIFT;
            case null, default -> typeInsertion.SALE; // Default value
        };
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
        Stage stage = (Stage) this.titleField.getScene().getWindow();
        stage.close();
    }
}