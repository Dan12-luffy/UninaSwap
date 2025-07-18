package com.uninaswap.services;

import com.uninaswap.dao.OfferedItemDao;
import com.uninaswap.dao.OfferedItemDaoImpl;
import com.uninaswap.model.OfferedItem;

import java.util.List;

public class OfferedItemsService {
    private static final OfferedItemsService instance = new OfferedItemsService();

    private final OfferedItemDao offeredItemDao;

    private OfferedItemsService(){
        this.offeredItemDao = new OfferedItemDaoImpl();
    }

    public static OfferedItemsService getInstance(){
        return instance;
    }

    public void createOfferedItem(OfferedItem offeredItem) {
        offeredItemDao.createOfferedItem(offeredItem);
    }

    public void deleteOfferedItem(int offerItemId) {
        offeredItemDao.deleteOfferedItem(offerItemId);
    }


    public OfferedItem findOfferedItemById(int offerItemId) {
        return offeredItemDao.findOfferedItemById(offerItemId);
    }

    public List<OfferedItem> findOfferedItemsByOfferIdAndInsertionID(int offerId, int insertionID) {
        return offeredItemDao.findOfferedItemsByOfferIdAndInsertionID(offerId, insertionID);
    }

    public List<OfferedItem> findOfferedItemsByOfferId(int offerId) {
        return offeredItemDao.findOfferedItemsByOfferId(offerId);
    }

    public List<OfferedItem> findOfferedItemsForInsertionID(int insertionID) {
        return offeredItemDao.findOfferedItemsForInsertionID(insertionID);
    }
}
