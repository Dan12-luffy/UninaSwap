package com.uninaswap.services;

import com.uninaswap.controllers.*;
import com.uninaswap.model.Insertion;
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/insertionFormInterface.fxml"));
            Parent root = loader.load();
            setScene(event, root);
        } catch (IOException e) {
            ValidationService.getInstance().showFailedToOpenPageError();
        }
    }
    public void navigateToProductDetailsView(MouseEvent event, Insertion insertion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/productDetailsInterface.fxml"));
            Parent root = loader.load();
            ProductDetailsController controller = loader.getController();
            controller.loadProductDetails(insertion);
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

    public void navigateToCreateInsertionView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/insertionFormInterface.fxml"));
            Parent root = loader.load();
            setScene(event, root);
        } catch (IOException e) {
            ValidationService.getInstance().showFailedToOpenPageError();
        }
    }
    public void navigateToEditInsertionView(ActionEvent event, Insertion insertion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/insertionFormInterface.fxml"));
            Parent root = loader.load();
            InsertionFormController controller = loader.getController();
            controller.setInsertion(insertion);
            setScene(event, root);
        } catch (IOException e) {
            ValidationService.getInstance().showFailedToOpenPageError();
        }
    }

    public void navigateToExchangeView(ActionEvent event, Insertion insertion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/exchangeOfferInterface.fxml"));
            Parent root = loader.load();
            // Assuming you have a controller for the exchange view
            ExchangeOfferController controller = loader.getController();
            controller.loadDesiredProduct(insertion);
            setScene(event, root);
        } catch (IOException e) {
            ValidationService.getInstance().showFailedToOpenPageError();
        }
    }

    public void closeFavoritesView(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }catch(Exception e){
            ValidationService.getInstance().showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore", "Impossibile chiudere la vista dei preferiti.");
        }
    }
    public void navigateToMakeOfferView(ActionEvent event, Insertion insertion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/saleOfferInterface.fxml"));
            Parent root = loader.load();

            SaleOfferController controller = loader.getController();
            controller.setInsertion(insertion);
            setScene(event, root);
        } catch (IOException e) {
            ValidationService.getInstance().showFailedToOpenPageError();
        }
    }

    public void navigateToGiftView(ActionEvent event, Insertion insertion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/giftOfferInterface.fxml"));
            Parent root = loader.load();
            GiftOfferController controller = loader.getController();
            controller.setInsertion(insertion);
            setScene(event, root);
        } catch (IOException e) {
            ValidationService.getInstance().showFailedToOpenPageError();
        }
    }

    public void navigateToPurchaseConfirmationView(ActionEvent event, Insertion insertion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/purchaseConfirmationInterface.fxml"));
            Parent root = loader.load();

            PurchaseConfirmationController controller = loader.getController();
            controller.setInsertion(insertion);
            setScene(event, root);
        } catch (IOException e) {
            ValidationService.getInstance().showFailedToOpenPageError();
        }
    }
    public void navigateToNotificationsView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uninaswap/gui/notificationsInterface.fxml"));
            Parent root = loader.load();
            setScene(event, root);
        } catch (IOException e) {
            ValidationService.getInstance().showFailedToOpenPageError();
        }
    }
}