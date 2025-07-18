package com.uninaswap.services;

import com.uninaswap.dao.TransactionDao;
import com.uninaswap.dao.TransactionDaoImpl;
import com.uninaswap.model.Insertion;
import com.uninaswap.model.Offer;
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

    public int recordSale(Insertion insertion, Offer offer, User buyer){
        Transaction transaction = new Transaction(
                insertion.getInsertionID(),
                offer != null ? offer.getOfferID() : null,
                insertion.getUserId(), // seller ID
                buyer.getId(),       // buyer ID
                offer != null ? offer.getAmount(): insertion.getPrice().doubleValue(),
                "PURCHASE",
                "COMPLETED",
                "Vendita di: " + insertion.getTitle()
        );
        return transactionDao.createTransaction(transaction);
    }

    public void recordExchange(Insertion insertion, Offer offer, User buyer){
        Transaction transaction = new Transaction(
                insertion.getInsertionID(),
                offer.getOfferID(),
                insertion.getUserId(),
                buyer.getId(),
                0.0,
                "EXCHANGE",
                "COMPLETED",
                "Scambio di: " + insertion.getTitle()
        );
        transactionDao.createTransaction(transaction);
    }

    public void recordGift(Insertion insertion, Offer offer , User buyer){
        Transaction transaction = new Transaction(
                insertion.getInsertionID(),
                offer.getOfferID(),
                insertion.getUserId(),
                buyer.getId(),
                0.0,
                "GIFT",
                "COMPLETED",
                "Regalo di: " + insertion.getTitle()
        );
        transactionDao.createTransaction(transaction);
    }
    //ottiene tutte le transazioni effettuate da un utente
    public List<Transaction> getTransactionsByUserId(int userId) throws SQLException {
        return transactionDao.findByUserId(userId);
    }
}
