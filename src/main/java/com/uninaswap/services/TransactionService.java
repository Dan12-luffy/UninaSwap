package com.uninaswap.services;

import com.uninaswap.dao.TransactionDao;
import com.uninaswap.dao.TransactionDaoImpl;
import com.uninaswap.model.Listing;
import com.uninaswap.model.Transaction;
import com.uninaswap.model.User;

import java.sql.SQLException;
import java.util.List;

public class TransactionService {

    private static TransactionService instance;
    private TransactionDao transactionDao;

    private TransactionService() {
        this.transactionDao = new TransactionDaoImpl();
    }
    public static TransactionService getInstance() {
        if (instance == null) {
            instance = new TransactionService();
        }
        return instance;
    }
    //crea una nuova transazione e la registra nel database
    public int recordSale(Listing listing, User buyer) throws  SQLException{
        Transaction transaction = new Transaction(
                listing.getListingId(),
                listing.getUserId(), // seller ID
                buyer.getId(),       // buyer ID
                listing.getPrice().doubleValue(),
                "PURCHASE",
                "COMPLETED",
                "Vendita di: " + listing.getTitle()
        );
        return transactionDao.createTransaction(transaction);
    }

    //ottiene tutte le transazioni effettuate da un utente
    public List<Transaction> getTransactionsByUserId(int userId) throws SQLException {
        return transactionDao.findByUserId(userId);
    }

    public double getTotalSales(int sellerId) throws SQLException {
        List<Transaction> transactions = transactionDao.findByUserId(sellerId);  //recupera tutte le transizioni dell' utente(come venditore o compratore)
        return transactions.stream()
                //filtra solo le transazioni dove l' utente è il venditore e sono completate
                .filter(t -> t.getSellerId() == sellerId && "COMPLETED".equals(t.getStatus()))
                //estrae l' importo di ogni transazione
                .mapToDouble(Transaction::getAmount)
                //somma tutti gli importi delle transazioni filtrate
                .sum();
    }

    //calcola il totale degli acquisti per un compratore
    public double getTotalPurchases(int buyerId) throws SQLException {
        List<Transaction> transactions = transactionDao.findByUserId(buyerId);
        return transactions.stream()
                //DIFFERENZA: qui si filtra per l' ID del compratore
                .filter(t -> t.getBuyerId() == buyerId && "COMPLETED".equals(t.getStatus()))
                //estrae l' importo di ogni transazione
                .mapToDouble(Transaction::getAmount)
                //somma tutti gli importi delle transazioni filtrate
                .sum();
    }
}
