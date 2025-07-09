package com.uninaswap.services;

import com.uninaswap.dao.OfferDao;
import com.uninaswap.dao.OfferDaoImpl;
import com.uninaswap.model.Listing;
import com.uninaswap.model.ListingStatus;
import com.uninaswap.model.Offer;
import com.uninaswap.model.User;

import java.sql.SQLException;
import java.util.List;


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
        if (ListingService.getInstance().getListingByID(offer.getListingID()).getUserId() == currentUser.getId()) {
            ValidationService.getInstance().showWrongOfferError();
            return -1;
        }
        offerDao.createOffer(offer);
        return offer.getOfferID();
    }

    public void deleteOffer(int offerId) throws Exception {
        Offer offer = offerDao.findOfferById(offerId);
        offerDao.deleteOffer(offer.getOfferID());
    }

    public List<Offer> getOffersForListing(int listingId) throws Exception {
        return offerDao.findOffersForListing(listingId);
    }

    public List<Offer> getOffersMadeByCurrentUser() throws SQLException {
        return offerDao.findOfferMadeByCurrentUserID();
    }
    public List<Offer> getOffersReceivedByCurrentUser() throws SQLException {
        return offerDao.findOffersToCurrentUser();
    }

    public boolean acceptOffer(int offerId) {
        try {
            Offer offer = offerDao.findOfferById(offerId);
            if (offer == null) {
                return false;
            }
            offerDao.updateOfferStatus(offerId, ListingStatus.ACCEPTED);
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
            offerDao.updateOfferStatus(offerId, ListingStatus.REJECTED);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public List<Offer> getListingOffers(int listingId) throws Exception {
        return offerDao.findOffersForListing(listingId);
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

    public Listing getListingByOfferId(int offerId) throws Exception {
        Offer offer = offerDao.findOfferById(offerId);
        if (offer != null) {
            return ListingService.getInstance().getListingByID(offer.getListingID());
        }
        return null;
    }

}
