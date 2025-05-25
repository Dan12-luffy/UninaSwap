package com.uninaswap.dao;

import com.uninaswap.model.User;
import com.uninaswap.services.ValidationService;
import com.uninaswap.utility.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDaoImpl implements UserDao {

    @Override
    public User authenticate(String username, String hashedPassword) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("userId"));
                    user.setUsername(rs.getString("username"));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean create(String name, String surname, String faculty, String username, String hashedPassword) {
        String sql = "INSERT INTO users (first_name, last_name, faculty, username, password) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, surname);
            stmt.setString(3, faculty);
            stmt.setString(4, username);
            stmt.setString(5, hashedPassword);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0; //Restituisce true se è stata inserita almeno una riga
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?"; //Conta quante righe ci sono con lo stesso username
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) { //Cerca se esiste un utente con lo stesso username, se esiste c'è una colonna, quindi restitituirà true ne conta una
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String usernameFromID(int id) {
        String sql = "SELECT username FROM users WHERE userId = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ValidationService.getInstance().showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore", "Nessun utente trovato con questo ID.");
        return null;
    }
    @Override
    public String fullNameFromID(int id) {
        String sql = "SELECT first_name, last_name FROM users WHERE userId = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("first_name") + " " + rs.getString("last_name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ValidationService.getInstance().showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore", "Nessun utente trovato con questo ID.");
        return null;
    }
    @Override
    public User getUserFromID(int id){
        String sql = "SELECT first_name, last_name, faculty, username, password FROM users WHERE userId = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(id, rs.getString("username"), rs.getString("password"), rs.getString("first_name"), rs.getString("last_name"), rs.getString("faculty"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ValidationService.getInstance().showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore", "Nessun utente trovato con questo ID.");
        return null;
    }
}