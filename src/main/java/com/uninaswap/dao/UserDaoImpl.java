package com.uninaswap.dao;

import com.uninaswap.model.User;
import com.uninaswap.services.ValidationService;
import com.uninaswap.utility.DatabaseUtil;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDaoImpl implements UserDao {

    @Override
    public User authenticateUser(String username, String hashedPassword) {
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
    public boolean insertUser(User u) {
        String sql = "INSERT INTO users (first_name, last_name, faculty, username, password) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, u.getFirst_name());
            stmt.setString(2, u.getLast_name());
            stmt.setString(3, u.getFaculty());
            stmt.setString(4, u.getUsername());
            stmt.setString(5, u.getPassword());
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0; //Restituisce true se è stata inserita almeno una riga
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public boolean updatePassword(int userId, String hashedPassword) {
        String sql = "UPDATE users SET password = ? WHERE userId = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0; //Restituisce true se è stata aggiornata almeno una riga
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateUsername(int userId, String username) {
        String sql = "UPDATE users SET username = ? WHERE userId = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setInt(2, userId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0; //Restituisce true se è stata aggiornata almeno una riga
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean usernameAlreadyExists(String username) {
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
    public String findUsernameFromID(int id) {
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
    public String findFullNameFromID(int id) {
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
    public User findUserFromID(int id){
        String sql = "SELECT * FROM users WHERE userId = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            User user = getNewUser(stmt);
            if (user != null) return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ValidationService.getInstance().showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Errore", "Nessun utente trovato con questo ID.");
        return null;
    }


    @Override
    public User findUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            return getNewUser(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateFaculty(int userId, String faculty) {
        String sql = "UPDATE users SET faculty = ? WHERE userId = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, faculty);
            stmt.setInt(2, userId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0; //Restituisce true se è stata aggiornata almeno una riga
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Nullable
    private User getNewUser(PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("userId"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFirst_name(rs.getString("first_name"));
                user.setLast_name(rs.getString("last_name"));
                user.setFaculty(rs.getString("faculty"));
                return user;
            }
        }
        return null;
        }
    }
