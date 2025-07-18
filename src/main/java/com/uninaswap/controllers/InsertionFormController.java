package com.uninaswap.controllers;

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

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class InsertionFormController {
    @FXML private TextField titleField;
    @FXML private TextField imagePathField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> locationComboBox;
    @FXML private Label priceLabel;
    @FXML private Label characterCountLabel;
    @FXML private ImageView mainImagePreview;
    @FXML private Button publishInsertionButton;
    @FXML private ComboBox<String> deliveryMethodComboBox;
    @FXML private TitledPane productDescription;
    @FXML private Label headFunctionalityLabel;

    private final String defaultImagePath = "file:/home/dan/Desktop/UninaSwap/src/main/resources/com/uninaswap/images/default_image.png";
    private File selectedImageFile;
    private final InsertionService insertionService = InsertionService.getInstance();
    private boolean isEditedInsertion = false; // Flag che serve per capire se si sta modificando un'inserzione esistente o creando una nuova
    private Insertion insertionToEdit = null;

    @FXML
    private void initialize() {
        // Initialize comboboxes with values
        this.typeComboBox.getItems().addAll("Vendita", "Scambio", "Regalo");
        this.categoryComboBox.getItems().addAll("Libri", "Appunti", "Elettronica", "Arredamento", "Abbigliamento", "Altro");
        this.locationComboBox.getItems().addAll("Monte Sant'Angelo", "Fuorigrotta", "Agnano", "Centro Storico");

        this.deliveryMethodComboBox.getItems().addAll("Consegna a mano", "Ritiro in sede", "Spedizione");
        this.deliveryMethodComboBox.setValue("Consegna a mano"); // Default value

        // Set default values
        this.typeComboBox.setValue("Vendita");
        this.categoryComboBox.setValue("Altro");
        this.locationComboBox.setValue("Monte Sant'Angelo");
        this.mainImagePreview.setImage(new Image(this.defaultImagePath));

        this.typeComboBox.valueProperty().addListener((_, _, _) -> {
            if(this.typeComboBox.getValue().equals("Regalo")){
                this.priceField.setDisable(true);
            } else if (this.typeComboBox.getValue().equals("Scambio")) {
                this.priceLabel.setText("Valore dell'oggetto: ");
                this.productDescription.setText("Descrizione dettagliata del prodotto e cio che vorresti ricevere in cambio");
            }
            else{
                this.priceField.setDisable(false);
                this.priceLabel.setText("Prezzo: ");
            }
        });
        this.priceField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            if (newText.isEmpty()) {
                return change;
            }
            if (change.getText().equals(",")) {
                change.setText(".");
            }
            if (newText.matches("\\d*(\\.\\d{0,2})?")) {
                return change;
            }
            return null;
        }));
        updateCharacterCount();
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
            if (this.titleField.getText().isEmpty() || this.descriptionArea.getText().isEmpty()) {
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
                imagePath = this.selectedImageFile.getAbsolutePath();
            } else {
                imagePath = this.defaultImagePath;
            }

            Integer insertionId = null;
            if (this.isEditedInsertion && this.insertionToEdit != null) {
                insertionId = this.insertionToEdit.getInsertionID();
            }
            String deliveryMethod = this.locationComboBox.getValue() + " " + this.deliveryMethodComboBox.getValue();

            Insertion insertion = InsertionFactory.createInsertion(
                    this.titleField.getText(),
                    imagePath,
                    this.descriptionArea.getText(),
                    type,
                    price,
                    InsertionStatus.AVAILABLE,
                    LocalDate.now(),
                    UserSession.getInstance().getCurrentUser().getId(),
                    this.categoryComboBox.getValue(),
                    deliveryMethod
            );

            if (this.isEditedInsertion && insertionId != null) {
                insertion.setInsertionID(insertionId);
            }

            if (!this.isEditedInsertion)
                insertionService.createInsertion(insertion);
            else
                insertionService.updateInsertion(insertion);

            ValidationService.getInstance().showNewInsertionSuccess();
            NavigationService.getInstance().navigateToMainView(event);

        } catch (Exception e) {
            ValidationService.getInstance().showNewInsertionError();
        }
    }

    @FXML
    private void updateCharacterCount() {
        if (this.descriptionArea != null && this.characterCountLabel != null) {
            int currentLength = this.descriptionArea.getText().length();
            int maxLength = 500;
            int remaining = maxLength - currentLength;

            this.characterCountLabel.setText(remaining + "/" + maxLength);

            if (remaining < 50) {
                this.characterCountLabel.setStyle("-fx-text-fill: red;");
            } else if (remaining < 100) {
                this.characterCountLabel.setStyle("-fx-text-fill: orange;");
            } else {
                this.characterCountLabel.setStyle("-fx-text-fill: green;");
            }

            if (currentLength > maxLength) {
                this.descriptionArea.setText(this.descriptionArea.getText().substring(0, maxLength));
                this.descriptionArea.positionCaret(maxLength);
            }
        }
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

    public void setInsertion(Insertion insertion) {
        if (insertion != null) {
            this.titleField.setText(insertion.getTitle());
            this.descriptionArea.setText(insertion.getDescription());
            this.typeComboBox.setValue(insertion.getType().toString());
            this.categoryComboBox.setValue(insertion.getCategory());
            this.headFunctionalityLabel.setText("Modifica Inserzione");

            if (insertion.getDeliveryMethod() != null && !insertion.getDeliveryMethod().isEmpty()) {
                this.deliveryMethodComboBox.setValue(insertion.getDeliveryMethod());
            } else {
                this.deliveryMethodComboBox.setValue("Consegna a mano"); // Default
            }

            this.publishInsertionButton.setText("Modifica Inserzione");
            this.isEditedInsertion = true;
            this.insertionToEdit = insertion;


            if (insertion.getPrice() != null) {
                this.priceField.setText(insertion.getPrice().toString());
            } else {
                this.priceField.setText("");
            }

            if (insertion.getImageUrl() != null && !insertion.getImageUrl().isEmpty()) {
                File imageFile = new File(insertion.getImageUrl());
                this.mainImagePreview.setImage(new Image(imageFile.toURI().toString()));
                this.imagePathField.setText(insertion.getImageUrl());
            } else {
                this.mainImagePreview.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(defaultImagePath))));
                this.imagePathField.setText(this.defaultImagePath);
            }
        }
    }
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
}