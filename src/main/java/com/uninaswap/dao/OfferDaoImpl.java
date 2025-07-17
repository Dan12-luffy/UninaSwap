package com.uninaswap.dao;

import com.uninaswap.model.InsertionStatus;
import com.uninaswap.model.Offer;
import com.uninaswap.model.typeOffer;
import com.uninaswap.services.UserSession;
import com.uninaswap.services.ValidationService;
import com.uninaswap.utility.DatabaseUtil;
import javafx.scene.control.Alert;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OfferDaoImpl implements OfferDao {
    @Override
    public int createOffer(Offer o) {
        String sql = "INSERT INTO offer (insertionid, userid, amount, status, message, typeoffer, offer_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, o.getListingID());
            stmt.setInt(2, o.getUserID());
            if (o.getAmount() != 0) {
                stmt.setDouble(3, o.getAmount());
            } else {
                stmt.setNull(3, java.sql.Types.DECIMAL);
            }
            stmt.setString(4, o.getListingStatus().getStatus());
            stmt.setString(5, o.getMessage());
            stmt.setString(6, o.getTypeOffer().getType());
            stmt.setDate(7, java.sql.Date.valueOf(o.getOfferDate()));
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    o.setOfferID(generatedId);
                    return generatedId;
                } else {
                    throw new SQLException("Creating offer failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile inserire l'offerta: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public void deleteOffer(int offerId) {
        String sql = "DELETE FROM offer WHERE offerid = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, offerId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile eliminare l'offerta: " + e.getMessage());
        }
    }

    @Override
    public void updateOffer(Offer offer) throws SQLException {
        String query = "UPDATE offer SET insertionid = ?, userid = ?, amount = ?, message = ?, status = ?, typeoffer = ?, offer_date = ? WHERE offerid = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, offer.getListingID());
            stmt.setInt(2, offer.getUserID());
            stmt.setDouble(3, offer.getAmount());
            stmt.setString(4, offer.getMessage());
            stmt.setString(5, offer.getListingStatus().getStatus());
            stmt.setString(6, mapTypeOfferToDatabase(offer.getTypeOffer()));
            stmt.setDate(7, java.sql.Date.valueOf(offer.getOfferDate()));
            stmt.setInt(8, offer.getOfferID());
            stmt.executeUpdate();
        }
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
        String sql = "SELECT * FROM offer WHERE insertionid = ?";
        try(Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, listingId);
            return getListingOffers(stmt);
        } catch (SQLException e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile trovare le offerte per l'inserzione: " + e.getMessage());
        }
        return new ArrayList<>();
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
        return new ArrayList<>();
    }

    @Override
    public List<Offer> findOffersToCurrentUser(){
        String sql = "SELECT o.* FROM offer o JOIN insertion i ON o.insertionid = i.insertionid WHERE i.userid = ? AND o.status = 'PENDING'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, UserSession.getInstance().getCurrentUserId());
            return getListingOffers(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public void updateOfferStatus(int offerId, InsertionStatus status){
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

    @Override
    public List<Offer> findRejectedOffersForCurrentUser(){
        String sql = "SELECT * FROM offer WHERE userid = ? AND status = 'REJECTED'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, UserSession.getInstance().getCurrentUserId());
            return getListingOffers(stmt);
        } catch (SQLException e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile trovare le offerte rifiutate per l'utente corrente: " + e.getMessage());
        }
        return new ArrayList<>();
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
    @Override
    public Map<String, Double> getAcceptedSaleOfferStatistics() throws SQLException {
        Map<String, Double> statistics = new HashMap<>();
        statistics.put("avg", 0.0);
        statistics.put("min", 0.0);
        statistics.put("max", 0.0);

        String sql = "SELECT AVG(t.amount) as avg_price, MIN(t.amount) as min_price, " +
                "MAX(t.amount) as max_price FROM transactions t " +
                "JOIN insertion i ON t.insertion_id = i.insertionid " +
                "WHERE t.status = 'COMPLETED' AND i.type = 'SALE'";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                double avg = rs.getDouble("avg_price");
                double min = rs.getDouble("min_price");
                double max = rs.getDouble("max_price");

                if (!rs.wasNull()) {
                    statistics.put("avg", avg);
                    statistics.put("min", min);
                    statistics.put("max", max);
                }
            }
        }

        return statistics;
    }
    public List<Offer> getPendingOffersByUser() throws SQLException {
        List<Offer> pendingOffers = new ArrayList<>();

        String sentSql = "SELECT * FROM offer WHERE userid = ? AND status = 'PENDING'";


        // try per le offerte che sono state inviate dall'utente
        try (Connection conn = DatabaseUtil.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sentSql)) {
                stmt.setInt(1, UserSession.getInstance().getCurrentUserId());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        pendingOffers.add(mapResultSetToOffer(rs));
                    }
                }
            }


        }

        return pendingOffers;
    }

    public List<Offer> getCompletedOffersByUser(int userId) throws SQLException {
        List<Offer> completedOffers = new ArrayList<>();

        // Get offers sent by the user with ACCEPTED status
        String sentSql = "SELECT * FROM offer WHERE userid = ? AND status = 'ACCEPTED'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sentSql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    completedOffers.add(mapResultSetToOffer(rs));
                }
            }
        }
        return completedOffers;
    }
    @NotNull
    private Offer mapResultSetToOffer(ResultSet rs) throws SQLException {
        Offer offer = new Offer();
        offer.setOfferID(rs.getInt("offerid"));
        offer.setListingID(rs.getInt("insertionid"));
        offer.setUserID(rs.getInt("userid"));

        // Handle nullable amount
        double amount = rs.getDouble("amount");
        if (!rs.wasNull()) {
            offer.setAmount(amount);
        }

        offer.setMessage(rs.getString("message"));
        offer.setListingStatus(InsertionStatus.valueOf(rs.getString("status")));
        offer.setTypeOffer(mapDatabaseToTypeOffer(rs.getString("typeoffer")));
        offer.setOfferDate(rs.getDate("offer_date").toLocalDate());
        return offer;
    }

    private String mapTypeOfferToDatabase(typeOffer typeOffer) {
        return switch (typeOffer) {
            case EXCHANGE_OFFER -> "EXCHANGE";
            case GIFT_OFFER -> "GIFT";
            default -> "SALE";
        };
    }

    private typeOffer mapDatabaseToTypeOffer(String dbValue) {
        return switch (dbValue) {
            case "EXCHANGE" -> typeOffer.EXCHANGE_OFFER;
            case "GIFT" -> typeOffer.GIFT_OFFER;
            case "SALE" -> typeOffer.SALE_OFFER;
            default -> typeOffer.UNDEFINED;
        };
    }
}