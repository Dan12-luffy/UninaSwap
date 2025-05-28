package com.uninaswap.dao;

import com.uninaswap.model.Category;
import com.uninaswap.services.ValidationService;
import com.uninaswap.utility.DatabaseUtil;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDaoImpl implements CategoryDao {
    
    @Override
    public void insert(Category category){
        String sql = "INSERT INTO category (name, description) VALUES (?,?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.executeUpdate();

        } catch (SQLException e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Errore" +
                    "Impossibile inserire la categoria: " + e.getMessage());
        }
    }

    @Override
    public void delete(int categoryId){
        String sql = "DELETE FROM category WHERE category_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Errore" +
                    "Impossibile eliminare la categoria: " + e.getMessage());
        }
    }
    @Override
    public void update(int categoryId, String CategoryName) {
        String sql = "UPDATE category SET name = ? WHERE category_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, CategoryName);
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Errore" +
                    "Impossibile aggiornare la categoria: " + e.getMessage());
        }
    }
    @Override
    public String findById(int categoryId) {
        String sql = "SELECT name FROM category WHERE category_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Errore" +
                    "Impossibile trovare la categoria: " + e.getMessage());
        }
        return null;
    }
    @Override
    public String findByName(String categoryName) {
        String sql = "SELECT name FROM category WHERE name = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoryName);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Errore" +
                    "Impossibile trovare la categoria: " + e.getMessage());
        }
        return null;
    }
    @Override
    public List<Category> findAll() {
        String sql = "SELECT * FROM category";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            var rs = stmt.executeQuery();
            List<Category> categories = new ArrayList<>();
            while (rs.next()) {
                Category category = new Category(rs.getInt("category_id"), rs.getString("name"), rs.getString("description"));
                categories.add(category);
            }
            return categories;
        } catch (SQLException e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Errore" +
                    "Impossibile recuperare le categorie: " + e.getMessage());
        }
        return null;
    }
    @Override
    public int getCategoryIdByName(String categoryName) {
        String sql = "SELECT category_id FROM category WHERE name = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoryName);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("category_id");
            }
        } catch (SQLException e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Errore" +
                    "Impossibile trovare l'ID della categoria: " + e.getMessage());
        }
        return -1; // Indica che la categoria non è stata trovata
    }
    @Override
    public String getCategoryNameById(int categoryId) {
        String sql = "SELECT name FROM category WHERE category_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            ValidationService.getInstance().showAlert(Alert.AlertType.ERROR, "Errore", "Errore" +
                    "Impossibile trovare il nome della categoria: " + e.getMessage());
        }
        return null;
    }
}

