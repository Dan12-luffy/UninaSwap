package com.uninaswap.dao;

import com.uninaswap.model.ListingStatus;
import com.uninaswap.model.typeListing;
import com.uninaswap.services.ValidationService;
import com.uninaswap.utility.DatabaseUtil;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class OfferDaoImpl implements OfferDao{
    @Override
    public void createOffer(int listingId, int userId, double amount, String message, ListingStatus status, LocalDate offerDate) throws Exception {
        String sql = "INSERT INTO offer (listingid, userid, amount, status, message, offer_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, listingId);
            stmt.setInt(2, userId);
            stmt.setDouble(3, amount);
            stmt.setString(4, message);
            stmt.setString(5, status.toString());
            stmt.setObject(6, offerDate.toEpochDay() * 24 * 60 * 60 * 1000);
            stmt.executeUpdate();

        } catch (SQLException e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile inserire l'offerta: " + e.getMessage());
        }

    }

    @Override
    public void deleteOffer(int offerId) throws Exception {
        String Sql = "DELETE FROM offer WHERE offerid = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(Sql)) {
            stmt.setInt(1, offerId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile eliminare l'offerta: " + e.getMessage());
        }
    }

    @Override
    public void updateOffer(int offerId, String title, String description, int categoryId, double price) throws Exception {

    }

    @Override
    public boolean acceptOffer(int offerId, int userId) throws Exception {
        String sql = "UPDATE offer SET status = 'ACCEPTED' WHERE offerid = ? AND userid = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, offerId);
            stmt.setInt(2, userId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile accettare l'offerta: " + e.getMessage());
        }
        return false; // Placeholder return value
    }

    @Override
    public boolean rejectOffer(int offerId, int userId) throws Exception {
        String sql = "UPDATE offer SET status = 'REJECTED' WHERE offerid = ? AND userid = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, offerId);
            stmt.setInt(2, userId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile rifiutare l'offerta: " + e.getMessage());
        }
        return false;
    }
}
