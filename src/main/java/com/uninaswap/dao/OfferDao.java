package com.uninaswap.dao;

import com.uninaswap.model.ListingStatus;
import com.uninaswap.model.typeListing;

import java.time.LocalDate;

public interface OfferDao {
    void createOffer(int listingId, int userId, double amount, String message, ListingStatus status, LocalDate offerDate) throws Exception;
    void deleteOffer(int offerId) throws Exception;
    void updateOffer(int offerId, String title, String description, int categoryId, double price) throws Exception;
    boolean acceptOffer(int offerId, int userId) throws Exception;
    boolean rejectOffer(int offerId, int userId) throws Exception;
}
