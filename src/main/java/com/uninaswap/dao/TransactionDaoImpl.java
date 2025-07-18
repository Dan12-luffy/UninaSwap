package com.uninaswap.dao;
import com.uninaswap.model.Transaction;
import com.uninaswap.utility.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDaoImpl implements TransactionDao {

    public int createTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (insertion_id, offer_id, seller_id, buyer_id, amount, transaction_type, status, transaction_date, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, transaction.getInsertionID());
            if(transaction.getOfferId() == null) {
                preparedStatement.setNull(2, Types.INTEGER);
            } else {
                preparedStatement.setInt(2, transaction.getOfferId());
            }
            preparedStatement.setInt(3, transaction.getSellerId());
            preparedStatement.setInt(4, transaction.getBuyerId());
            preparedStatement.setDouble(5, transaction.getAmount());
            preparedStatement.setString(6, transaction.getTransactionType());
            preparedStatement.setString(7, transaction.getStatus());
            preparedStatement.setTimestamp(8, Timestamp.valueOf(transaction.getTransactionDate()));
            preparedStatement.setString(9, transaction.getDescription());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Se ritorna -1, significa che la transazione non è stata creata correttamente
    }

    public Transaction findById(int transactionId) {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, transactionId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return mapRowToTransaction(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Transaction> findByUserId(int userId) {
        String sql = "SELECT * FROM transactions WHERE buyer_id = ? OR seller_id = ?";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                transactions.add(mapRowToTransaction(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public List<Transaction> findByInsertionID(int insertionID) {
        String sql = "SELECT * FROM transactions WHERE insertion_id = ?";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, insertionID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                transactions.add(mapRowToTransaction(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    private Transaction mapRowToTransaction(ResultSet resultSet) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(resultSet.getInt("transaction_id"));
        transaction.setInsertionID(resultSet.getInt("insertion_id"));
        transaction.setOfferId(resultSet.getInt("offer_id"));
        transaction.setSellerId(resultSet.getInt("seller_id"));
        transaction.setBuyerId(resultSet.getInt("buyer_id"));
        transaction.setAmount(resultSet.getDouble("amount"));
        transaction.setTransactionType(resultSet.getString("transaction_type"));
        transaction.setStatus(resultSet.getString("status"));
        transaction.setTransactionDate(resultSet.getTimestamp("transaction_date").toLocalDateTime());
        transaction.setDescription(resultSet.getString("description"));
        return transaction;
    }
}