package com.uninaswap.dao;

import com.uninaswap.model.InsertionStatus;
import com.uninaswap.model.Offer;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface OfferDao {
    int createOffer(Offer o) throws Exception;
    void deleteOffer(int offerId) throws Exception;
    void updateOffer(Offer o) throws Exception;
    boolean acceptOffer(int offerId, int userId) throws Exception;
    boolean rejectOffer(int offerId, int userId) throws Exception;
    Offer findOfferById(int offerId) throws Exception;
    List<Offer> findOffersForInsertion(int insertionID) throws Exception;
    List<Offer> findOfferMadeByCurrentUserID() throws SQLException;
    List<Offer> findOffersToCurrentUser() throws SQLException;
    void updateOfferStatus(int offerId, InsertionStatus status) throws Exception;
    Map<String, Double> getAcceptedSaleOfferStatistics() throws SQLException;
    List<Offer> findRejectedOffersForCurrentUser();
    List<Offer> getPendingOffersByUser() throws SQLException;
    List<Offer> getCompletedOffersByUser(int userId) throws SQLException;
    List<Offer> getDirectPurchaseByUser(int userId) throws SQLException;
    List<Offer> getAllCompletedOperationsByUser(int userId) throws SQLException;
}
