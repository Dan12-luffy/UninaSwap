package com.uninaswap.dao;

import com.uninaswap.model.Transaction;

import java.util.List;

public interface TransactionDao  {

    int createTransaction(Transaction transaction);
    Transaction findById(int transactionId);
    List<Transaction> findByUserId(int userId);
    List<Transaction> findByInsertionID(int insertionID);

}
