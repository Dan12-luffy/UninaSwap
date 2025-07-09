package com.uninaswap.dao;

import com.uninaswap.model.ListingStatus;
import com.uninaswap.model.Offer;

import java.sql.SQLException;
import java.util.List;

public interface OfferDao {
    int createOffer(Offer o) throws Exception;
    void deleteOffer(int offerId) throws Exception;
    void updateOffer(int offerId, String title, String description, int categoryId, double price) throws Exception;
    boolean acceptOffer(int offerId, int userId) throws Exception;
    boolean rejectOffer(int offerId, int userId) throws Exception;
    Offer findOfferById(int offerId) throws Exception;
    List<Offer> findOffersForListing(int listingId) throws Exception;
    List<Offer> findOfferMadeByCurrentUserID() throws SQLException;
    List<Offer> findOffersToCurrentUser() throws SQLException;
    void updateOfferStatus(int offerId, ListingStatus status) throws Exception;

    List<Offer> findRejectedOffersForCurrentUser();
}
