package com.uninaswap.services;

import com.uninaswap.dao.OfferDao;
import com.uninaswap.dao.OfferDaoImpl;
import com.uninaswap.model.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;


public class OfferService {
    private static final OfferService instance = new OfferService();
    private final OfferDao offerDao;
    private final UserSession userSession;

    private OfferService() {
        this.offerDao = new OfferDaoImpl();
        this.userSession = UserSession.getInstance();
    }

    public static OfferService getInstance() {
        return instance;
    }

    public int createOffer(Offer offer) throws Exception {
        User currentUser = userSession.getCurrentUser();
        if (InsertionService.getInstance().getInsertionByID(offer.getInsertionID()).getUserId() == currentUser.getId()) {
            ValidationService.getInstance().showWrongOfferError();
            return -1;
        }
        offerDao.createOffer(offer);
        return offer.getOfferID();
    }

    public void deleteOffer(int offerId) throws Exception {
        for(OfferedItem item : OfferedItemsService.getInstance().findOfferedItemsByOfferId(offerId)) {
            InsertionService.getInstance().updateInsertionStatus(item.getInsertionId(), InsertionStatus.AVAILABLE);
            OfferedItemsService.getInstance().deleteOfferedItem(item.getOfferedItemId());
        }
        offerDao.deleteOffer(offerId);
    }

    public List<Offer> getOffersForInsertion(int insertionID) throws Exception {
        return offerDao.findOffersForInsertion(insertionID);
    }

    public List<Offer> getOffersMadeByCurrentUser() throws SQLException {
        return offerDao.findOfferMadeByCurrentUserID();
    }
    public List<Offer> getOffersReceivedByCurrentUser() throws SQLException {
        return offerDao.findOffersToCurrentUser();
    }


    public void acceptOffer(int offerId) {
        try {
            Offer offer = offerDao.findOfferById(offerId);
            if (offer == null) {
                return;
            }
            offerDao.updateOfferStatus(offerId, InsertionStatus.ACCEPTED);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void rejectOffer(int offerId) {
        try {
            Offer offer = offerDao.findOfferById(offerId);
            if (offer == null) {
                return;
            }
            List<OfferedItem> offeredItems = OfferedItemsService.getInstance().findOfferedItemsByOfferId(offerId);
            for (OfferedItem item : offeredItems) {
                Insertion insertion = InsertionService.getInstance().getInsertionByID(item.getInsertionId());
                insertion.setStatus(InsertionStatus.AVAILABLE);
                InsertionService.getInstance().updateInsertion(insertion);
            }
            offerDao.updateOfferStatus(offerId, InsertionStatus.REJECTED);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public List<Offer> getInsertionOffers(int insertionID) throws Exception {
        return offerDao.findOffersForInsertion(insertionID);
    }

    public Offer getOfferById(int offerId) throws Exception {
        return offerDao.findOfferById(offerId);
    }
    public List<Offer> getOffersToCurrentUser() throws Exception {
        return offerDao.findOffersToCurrentUser();
    }
    public List<Offer> getRejectedOffersForCurrentUser() throws Exception {
        return offerDao.findRejectedOffersForCurrentUser();
    }
    public Map<String, Double> getAcceptedSaleOfferStatistics() throws SQLException {
        return offerDao.getAcceptedSaleOfferStatistics();
    }
    public List<Offer> getPendingOffersByUser() throws Exception {
        return offerDao.getPendingOffersByUser();
    }
    public List<Offer> getCompletedOffersByUser(int userId) throws Exception {
        return offerDao.getCompletedOffersByUser(userId);
    }
    public List<Offer> getDirectPurchaseByUser(int userId) throws Exception {
        return offerDao.getDirectPurchaseByUser(userId);
    }
    public List<Offer> getAllCompletedOperationsByUser(int userId) throws Exception {
        return offerDao.getAllCompletedOperationsByUser(userId);
    }
    public Insertion getInsertionByOfferId(int offerId) throws Exception {
        Offer offer = offerDao.findOfferById(offerId);
        if (offer != null) {
            return InsertionService.getInstance().getInsertionByID(offer.getInsertionID());
        }
        return null;
    }
    public void updateOffer(Offer o ) throws Exception {
        offerDao.updateOffer(o);
    }
    public void updateOfferStatus(int offerId, InsertionStatus status) throws Exception {
        offerDao.updateOfferStatus(offerId, status);
    }
}
