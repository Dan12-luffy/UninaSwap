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
        //TODO metodo da migliorare, non riuscirò a leggerlo neanche io domani che mi rimetto sul codice
        if (InsertionService.getInstance().getInsertionByID(offer.getInsertionID()).getUserId() == currentUser.getId()) {
            ValidationService.getInstance().showWrongOfferError();
            return -1;
        }
        offerDao.createOffer(offer);
        return offer.getOfferID();
    }

    public void deleteOffer(int offerId) throws Exception {
        Offer offer = offerDao.findOfferById(offerId);
        offerDao.deleteOffer(offer.getOfferID());
        for(OfferedItem item : OfferedItemsService.getInstance().findOfferedItemsByOfferId(offerId)) {
            OfferedItemsService.getInstance().deleteOfferedItem(item.getOfferedItemId());
            updateOfferStatus(item.getOfferedItemId(), InsertionStatus.AVAILABLE);
        }
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

    public boolean completeOfferAcceptance(int offerId) throws Exception {
        Offer offer = offerDao.findOfferById(offerId);
        if (offer == null) return false;

        Insertion insertion = InsertionService.getInstance().getInsertionByID(offer.getInsertionID());
        User buyer = UserService.getInstance().getUserById(offer.getUserID());
        switch (insertion.getType()) {
            case SALE -> TransactionService.getInstance().recordSale(insertion, offer, buyer);
            case EXCHANGE -> TransactionService.getInstance().recordExchange(insertion, offer, buyer);
            case GIFT -> TransactionService.getInstance().recordGift(insertion, offer, buyer);
        }

        offerDao.updateOfferStatus(offerId, InsertionStatus.ACCEPTED);
        InsertionService.getInstance().updateInsertionStatus(insertion.getInsertionID(), InsertionStatus.SOLD);

        return true;
    }

    public boolean completeOfferRejection(int offerId) throws Exception {
        Offer offer = offerDao.findOfferById(offerId);
        if (offer == null) return false;

        List<OfferedItem> offeredItems = OfferedItemsService.getInstance().findOfferedItemsByOfferId(offerId);
        for (OfferedItem item : offeredItems) {
            Insertion insertion = InsertionService.getInstance().getInsertionByID(item.getInsertionId());
            insertion.setStatus(InsertionStatus.AVAILABLE);
            InsertionService.getInstance().updateInsertion(insertion);
        }

        offerDao.updateOfferStatus(offerId, InsertionStatus.REJECTED);
        return true;
    }

    public boolean acceptOffer(int offerId) {
        try {
            Offer offer = offerDao.findOfferById(offerId);
            if (offer == null) {
                return false;
            }
            offerDao.updateOfferStatus(offerId, InsertionStatus.ACCEPTED);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean rejectOffer(int offerId) {
        try {
            Offer offer = offerDao.findOfferById(offerId);
            if (offer == null) {
                return false;
            }
            offerDao.updateOfferStatus(offerId, InsertionStatus.REJECTED);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
