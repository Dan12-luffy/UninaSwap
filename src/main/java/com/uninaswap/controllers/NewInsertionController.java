package com.uninaswap.controllers; //TODO CLASSE NON FINITA, DA FINIRE

import com.uninaswap.model.Listing;
import com.uninaswap.dao.ListingDao;
import com.uninaswap.dao.ListingDaoImpl;
import com.uninaswap.model.ListingStatus;
import com.uninaswap.model.typeListing;
import com.uninaswap.services.UserSession;
import com.uninaswap.services.ValidationService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.sql.SQLException;
import java.util.UUID;

public class NewInsertionController {
    // FXML fields
    @FXML
    private TextField titleField;

    @FXML
    private TextField imagePathField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField priceField;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private DatePicker publishDatePicker;

    @FXML
    private ComboBox<String> categoryComboBox;

    private File selectedImageFile;

    @FXML
    private void initialize() {
        // Initialize comboboxes with values
        typeComboBox.getItems().addAll("Vendita", "Scambio", "Regalo");
        statusComboBox.getItems().addAll("Nuovo", "Come nuovo", "Buone condizioni", "Usato");
        categoryComboBox.getItems().addAll("Libri", "Appunti", "Elettronica", "Arredamento", "Abbigliamento", "Altro");

        // Set default values
        publishDatePicker.setValue(LocalDate.now());
        typeComboBox.setValue("Vendita");
        statusComboBox.setValue("Usato");
        categoryComboBox.setValue("Altro");
    }

    @FXML
    private void handleSelectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona un'immagine");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        selectedImageFile = fileChooser.showOpenDialog(titleField.getScene().getWindow());
        if (selectedImageFile != null) {
            imagePathField.setText(selectedImageFile.getName());
        }
    }

    @FXML
    private void handleSave() {

    }

    private String saveImageFile() throws IOException {
        // Generate a unique filename
        String extension = selectedImageFile.getName().substring(selectedImageFile.getName().lastIndexOf('.'));
        String filename = UUID.randomUUID().toString() + extension;

        // Define the path where the image will be saved
        String targetPath = "/com/uninaswap/images/listings/" + filename;

        // Get the absolute path to the resources directory
        String resourcesPath = getClass().getResource("/com/uninaswap/images/listings").getPath();
        Path targetFilePath = Paths.get(resourcesPath, filename);

        // Copy the file
        Files.copy(selectedImageFile.toPath(), targetFilePath, StandardCopyOption.REPLACE_EXISTING);

        return targetPath;
    }

    private Listing createListingFromInputs(String imagePath) {
        return  null;
    }
    private byte[] openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona un'immagine");

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("File immagine", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp");
        fileChooser.getExtensionFilters().add(extFilter);

        File selectedFile = fileChooser.showOpenDialog(null);
        byte[] imageData = null;
        if (selectedFile != null) {
            try {
                imageData = readFileToByteArray(selectedFile);
            } catch (IOException e) {
                e.printStackTrace();
                return imageData;
            }
        }
        return imageData;
    }
    private byte[] readFileToByteArray(File file) throws IOException {
        byte[] byteArray = new byte[(int) file.length()];

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            fileInputStream.read(byteArray);
        }
        return byteArray;
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