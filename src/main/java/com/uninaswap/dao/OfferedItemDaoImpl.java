package com.uninaswap.dao;

import com.uninaswap.exceptions.DatabaseOperationException;
import com.uninaswap.model.OfferedItem;
import com.uninaswap.services.ValidationService;
import com.uninaswap.utility.DatabaseUtil;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OfferedItemDaoImpl implements OfferedItemDao {

    @Override
    public void createOfferedItem(OfferedItem offeredItem) {
        String sql = "INSERT INTO offereditems (offerid, offeredinsertionid) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, offeredItem.getOfferId());
            stmt.setInt(2, offeredItem.getInsertionId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseOperationException("findInsertionsExcludingCurrentUser", e);
        }
    }

    @Override
    public void deleteOfferedItem(int offerItemId) {
        String sql = "DELETE FROM offereditems WHERE offereditemid = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, offerItemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseOperationException("findInsertionsExcludingCurrentUser", e);
        }
    }

    @Override
    public OfferedItem findOfferedItemById(int offerItemId) {
        String sql = "SELECT * FROM offereditems WHERE offereditemid = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, offerItemId);
            ResultSet rs = stmt.executeQuery();
            OfferedItem offeredItem = new OfferedItem();
            if (rs.next()) {
                offeredItem.setOfferedItemId(rs.getInt("offereditemid"));
                offeredItem.setOfferId(rs.getInt("offerid"));
            }
            return offeredItem;
        } catch (SQLException e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile trovare l'oggetto offerto: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<OfferedItem> findOfferedItemsByOfferId(int offerId) {
        String sql = "SELECT * FROM offereditems WHERE offerid = ?";
        List<OfferedItem> offeredItems = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, offerId);
            var rs = stmt.executeQuery();
            while (rs.next()) {
                OfferedItem offeredItem = new OfferedItem();
                offeredItem.setOfferedItemId(rs.getInt("offereditemid"));
                offeredItem.setOfferId(rs.getInt("offerid"));
                offeredItem.setInsertionID(rs.getInt("offeredinsertionid"));
                offeredItems.add(offeredItem);
            }
            return offeredItems;
        } catch (SQLException e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile trovare gli oggetti offerti per l'ID offerta: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<OfferedItem> findOfferedItemsForInsertionID(int insertionID) {
        String sql = "SELECT * FROM offereditems WHERE offeredinsertionid = ?";
        List<OfferedItem> offeredItems = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, insertionID);
            var rs = stmt.executeQuery();
            while (rs.next()) {
                OfferedItem offeredItem = new OfferedItem();
                offeredItem.setOfferedItemId(rs.getInt("offereditemid"));
                offeredItem.setOfferId(rs.getInt("offerid"));
                offeredItem.setInsertionID(rs.getInt("offeredinsertionid"));
                offeredItems.add(offeredItem);
            }
            return offeredItems;
        } catch (SQLException e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile trovare gli oggetti offerti per l'ID inserzione: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<OfferedItem> findOfferedItemsByOfferIdAndInsertionID(int offerId, int insertionID) {
        String sql = "SELECT * FROM offereditems WHERE offerid = ? AND offeredinsertionid = ?";
        List<OfferedItem> offeredItems = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, offerId);
            stmt.setInt(2, insertionID);
            var rs = stmt.executeQuery();
            while (rs.next()) {
                OfferedItem offeredItem = new OfferedItem();
                offeredItem.setOfferedItemId(rs.getInt("offereditemid"));
                offeredItem.setOfferId(rs.getInt("offerid"));
                offeredItem.setInsertionID(rs.getInt("offeredinsertionid"));
                offeredItems.add(offeredItem);
            }
            return offeredItems;
        } catch (SQLException e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile trovare gli oggetti offerti per l'ID offerta e inserzione: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}