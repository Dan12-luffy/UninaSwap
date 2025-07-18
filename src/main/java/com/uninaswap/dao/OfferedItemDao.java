package com.uninaswap.dao;

import com.uninaswap.model.OfferedItem;

import java.util.List;

public interface OfferedItemDao {
    void createOfferedItem(OfferedItem offeredItem);
    void deleteOfferedItem(int offerItemId);
    //void updateOfferedItem(int offerItemId, String offeredItemDescription, BigDecimal amount);
    OfferedItem findOfferedItemById(int offerItemId);
    List<OfferedItem> findOfferedItemsByOfferId(int offerId);
    List<OfferedItem> findOfferedItemsForInsertionID(int insertionID);
    List<OfferedItem> findOfferedItemsByOfferIdAndInsertionID(int offerId, int insertionID);
}
