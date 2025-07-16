package com.uninaswap.dao;

import com.uninaswap.model.OfferedItem;

import java.math.BigDecimal;
import java.util.List;

public interface OfferedItemDao {
    void createOfferedItem(OfferedItem offeredItem);
    void deleteOfferedItem(int offerItemId);
    //void updateOfferedItem(int offerItemId, String offeredItemDescription, BigDecimal amount);
    OfferedItem findOfferedItemById(int offerItemId);
    List<OfferedItem> findOfferedItemsByOfferId(int offerId);
    List<OfferedItem> findOfferedItemsForListingId(int listingId);
    List<OfferedItem> findOfferedItemsByOfferIdAndListingId(int offerId, int listingId);
}
