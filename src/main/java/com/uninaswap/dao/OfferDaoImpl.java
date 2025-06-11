package com.uninaswap.dao;

import com.uninaswap.model.ListingStatus;
import com.uninaswap.model.Offer;
import com.uninaswap.services.UserSession;
import com.uninaswap.services.ValidationService;
import com.uninaswap.utility.DatabaseUtil;
import javafx.scene.control.Alert;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OfferDaoImpl implements OfferDao {
    @Override
    public void createOffer(Offer o) throws SQLException {
        String sql = "INSERT INTO offer (listingid, userid, amount, status, message, offer_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, o.getListingID());
            stmt.setInt(2, o.getUserID());
            stmt.setDouble(3, o.getAmount());
            stmt.setString(4, o.getListingStatus().toString().toUpperCase());
            stmt.setString(5, o.getMessage());
            stmt.setDate(6, java.sql.Date.valueOf(o.getOfferDate()));
            stmt.executeUpdate(); //Converte Localdate a Date per il db
        } catch (SQLException e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile inserire l'offerta: " + e.getMessage());
        }
    }

    @Override
    public void deleteOffer(int offerId) throws SQLException {
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
    public void updateOffer(int offerId, String title, String description, int categoryId, double price){

    }

    @Override
    public boolean acceptOffer(int offerId, int userId) {
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
        return false;
    }

    @Override
    public boolean rejectOffer(int offerId, int userId){
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

    @Override
    public Offer findOfferById(int offerId){
        String sql = "SELECT * FROM offer WHERE offerid = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, offerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOffer(rs);
                }
            }
        } catch (SQLException e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile trovare l'offerta: " + e.getMessage());
        }
        return null;
    }
    @Override
    public List<Offer> findOffersForListing(int listingId){
        String sql = "SELECT * FROM offer WHERE listingid = ?";
        try(Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, listingId);
            return getListingOffers(stmt);
        } catch (SQLException e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile trovare le offerte per l'inserzione: " + e.getMessage());
        }
        return null;
    }
    @Override
    public List<Offer> findOfferMadeByCurrentUserID(){
        String sql = "SELECT * FROM offer WHERE userid = ?";
        try(Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, UserSession.getInstance().getCurrentUserId());
            return getListingOffers(stmt);
        } catch (SQLException e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile trovare le offerte per l'utente corrente: " + e.getMessage());
        }
        return null;
    }
    @Override
    public List<Offer> findOffersToCurrentUser(){
        String sql = "SELECT o.* FROM offer o JOIN listings l ON o.listingid = l.listingid WHERE l.userid = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, UserSession.getInstance().getCurrentUserId());
            return getListingOffers(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void updateOfferStatus(int offerId, ListingStatus status){
        String sql = "UPDATE offer SET status = ? WHERE offerid = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.getStatus());
            stmt.setInt(2, offerId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile aggiornare lo stato dell'offerta: " + e.getMessage());
        }
    }

    @NotNull
    private List<Offer> getListingOffers(PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            List<Offer> offers = new ArrayList<>();
            while(rs.next()) {
                Offer o = mapResultSetToOffer(rs);
                offers.add(o);
            }
            return offers;
        }
    }
    @NotNull
    private Offer mapResultSetToOffer(ResultSet rs) throws SQLException {
        Offer offer = new Offer();
        offer.setOfferID(rs.getInt("offerid"));
        offer.setListingID(rs.getInt("listingid"));
        offer.setUserID(rs.getInt("userid"));
        offer.setAmount(rs.getDouble("amount"));
        offer.setMessage(rs.getString("message"));
        offer.setListingStatus(ListingStatus.valueOf(rs.getString("status")));
        offer.setOfferDate(rs.getDate("offer_date").toLocalDate());
        return offer;
    }
}
