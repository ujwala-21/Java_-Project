package com.bookloop.service;

import com.bookloop.dao.ListingDAO;
import com.bookloop.exception.DatabaseException;
import com.bookloop.exception.ValidationException;
import com.bookloop.model.BookListing;
import com.bookloop.util.ValidationUtil;

import java.math.BigDecimal;
import java.util.List;

public class ListingService {
    private final ListingDAO listingDAO;

    public ListingService() {
        this.listingDAO = new ListingDAO();
    }

    public ListingService(ListingDAO listingDAO) {
        this.listingDAO = listingDAO;
    }

    public int createListing(BookListing listing) throws ValidationException, DatabaseException {
        validateListing(listing);

        // Auto-calculate credit value if exchange or zero
        if (listing.getCreditValue() == null || listing.getCreditValue().compareTo(BigDecimal.ZERO) <= 0) {
            listing.setCreditValue(calculateSuggestedCredits(listing));
        }

        return listingDAO.createListing(listing);
    }

    public BookListing getListingById(int id) throws DatabaseException {
        return listingDAO.findById(id);
    }

    public List<BookListing> searchListings(String query, String type, String condition,
                                           Integer deptId, Double maxPrice, String sortBy) throws DatabaseException {
        return listingDAO.searchListings(query, type, condition, deptId, maxPrice, sortBy);
    }

    public List<BookListing> getMyListings(int ownerId) throws DatabaseException {
        return listingDAO.findByOwner(ownerId);
    }

    public List<BookListing> getAvailableForExchange(int currentStudentId) throws DatabaseException {
        return listingDAO.findAvailableForExchange(currentStudentId);
    }

    public boolean updateListing(BookListing listing, int currentUserId) throws ValidationException, DatabaseException {
        validateListing(listing);
        if (listing.getOwnerId() != currentUserId) {
            throw new ValidationException("You cannot edit a listing that does not belong to you.");
        }
        return listingDAO.updateListing(listing);
    }

    public boolean updateStatus(int listingId, String newStatus, int currentUserId) throws ValidationException, DatabaseException {
        BookListing existing = listingDAO.findById(listingId);
        if (existing == null) {
            throw new ValidationException("Listing not found.");
        }
        if (existing.getOwnerId() != currentUserId) {
            throw new ValidationException("You can only modify status of your own listings.");
        }
        return listingDAO.updateStatus(listingId, newStatus);
    }

    public boolean deleteListing(int listingId, int currentUserId) throws ValidationException, DatabaseException {
        BookListing existing = listingDAO.findById(listingId);
        if (existing == null) {
            throw new ValidationException("Listing not found.");
        }
        if (existing.getOwnerId() != currentUserId) {
            throw new ValidationException("You can only delete your own listings.");
        }
        if (existing.getStatus() == BookListing.ListingStatus.RESERVED) {
            throw new ValidationException("Cannot delete a book that is currently reserved in an exchange.");
        }
        return listingDAO.deleteListing(listingId, currentUserId);
    }

    public BigDecimal calculateSuggestedCredits(BookListing listing) {
        BigDecimal base = listing.getOriginalPrice() != null && listing.getOriginalPrice().compareTo(BigDecimal.ZERO) > 0
                ? listing.getOriginalPrice().multiply(BigDecimal.valueOf(0.40))
                : BigDecimal.valueOf(60.0);

        double multiplier = switch (listing.getConditionType()) {
            case NEW -> 1.0;
            case EXCELLENT -> 0.85;
            case GOOD -> 0.70;
            case ACCEPTABLE -> 0.50;
        };

        BigDecimal result = base.multiply(BigDecimal.valueOf(multiplier));
        return result.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public BookListing getFirstListingByBookId(int bookId) throws DatabaseException {
        return listingDAO.findFirstByBookId(bookId);
    }

    private void validateListing(BookListing listing) throws ValidationException {
        if (listing.getBookId() <= 0) {
            throw new ValidationException("Book association is required.");
        }
        if (listing.getListingType() == null) {
            throw new ValidationException("Listing type (Sell, Exchange, Rent, Donate) is required.");
        }
        if (listing.getConditionType() == null) {
            throw new ValidationException("Book physical condition is required.");
        }

        if (listing.getListingType() == BookListing.ListingType.SELL) {
            if (listing.getAskingPrice() == null || listing.getAskingPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException("Selling price must be greater than zero.");
            }
        } else if (listing.getListingType() == BookListing.ListingType.RENT) {
            if (listing.getRentalDurationDays() == null || listing.getRentalDurationDays() <= 0) {
                throw new ValidationException("Rental duration must be at least 1 day.");
            }
            if (listing.getRentalCredits() == null || listing.getRentalCredits().compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException("Rental fee/credits must be specified.");
            }
        }
    }
}
