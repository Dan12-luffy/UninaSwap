package com.uninaswap.services;

import com.uninaswap.controllers.ExchangeController;
import com.uninaswap.controllers.ProductDetailsController;
import com.uninaswap.model.Listing;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.input.MouseEvent;
import java.io.IOException;

public class NavigationService {
    private static final NavigationService instance = new NavigationService();

    private NavigationService(){
    }

    public static NavigationService getInstance() {
        return instance;
    }

    public void navigateToLoginView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/loginInterface.fxml"));
            Parent root = loader.load();
            setScene(event, root);

        } catch (IOException e) {
            ValidationService.getInstance().showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore",
                    "Impossibile aprire la schermata di login.");
        }
    }

    public void navigateToRegisterView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/registerInterface.fxml"));
            Parent root = loader.load();
            setScene(event, root);
        } catch (IOException e) {
            ValidationService.getInstance().showFailedToOpenPageError();
        }
    }

    public void navigateToMainView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/mainInterface.fxml"));
            Parent root = loader.load();
            setScene(event, root);

        } catch (IOException e) {
            e.printStackTrace();
            ValidationService.getInstance().showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore","Impossibile aprire la schermata principale.");
        }
    }
    public void navigateToNewInsertionView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/newInsertionInterface.fxml"));
            Parent root = loader.load();
            setScene(event, root);
        } catch (IOException e) {
            ValidationService.getInstance().showFailedToOpenPageError();
        }
    }
    public void navigateToProductDetailsView(MouseEvent event, Listing listing) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/productDetailsInterface.fxml"));
            Parent root = loader.load();
            ProductDetailsController controller = loader.getController();
            controller.loadProductDetails(listing);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            ValidationService.getInstance().showFailedToOpenPageError();
        }
    }

    private void setScene(ActionEvent event, Parent root) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }


    public void navigateToMyProfileView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/myProfileInterface.fxml"));
            Parent root = loader.load();
            setScene(event, root);
        } catch (IOException e) {
            ValidationService.getInstance().showFailedToOpenPageError();
        }
    }

    public void navigateToCreateListingView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/newInsertionInterface.fxml"));
            Parent root = loader.load();
            setScene(event, root);
        } catch (IOException e) {
            ValidationService.getInstance().showFailedToOpenPageError();
        }
    }

    public void navigateToExchangeView(ActionEvent event, Listing listing) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/exchangeInterface.fxml"));
            Parent root = loader.load();
            // Assuming you have a controller for the exchange view
            ExchangeController controller = loader.getController();
            controller.setListing(listing);
            setScene(event, root);
        } catch (IOException e) {
            ValidationService.getInstance().showFailedToOpenPageError();
        }
    }
}