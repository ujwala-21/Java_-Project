package com.bookloop.service;

import com.bookloop.dao.WishlistDAO;
import com.bookloop.exception.DatabaseException;
import com.bookloop.exception.ValidationException;
import com.bookloop.model.WishlistItem;

import java.util.List;

public class WishlistService {
    private final WishlistDAO wishlistDAO;

    public WishlistService() {
        this.wishlistDAO = new WishlistDAO();
    }

    public WishlistService(WishlistDAO wishlistDAO) {
        this.wishlistDAO = wishlistDAO;
    }

    public boolean addToWishlist(int studentId, int bookId) throws ValidationException, DatabaseException {
        if (studentId <= 0 || bookId <= 0) {
            throw new ValidationException("Invalid student or book ID.");
        }
        if (wishlistDAO.isInWishlist(studentId, bookId)) {
            throw new ValidationException("This book is already in your wishlist.");
        }
        return wishlistDAO.addToWishlist(studentId, bookId);
    }

    public boolean removeFromWishlist(int studentId, int bookId) throws DatabaseException {
        return wishlistDAO.removeFromWishlist(studentId, bookId);
    }

    public boolean isInWishlist(int studentId, int bookId) throws DatabaseException {
        return wishlistDAO.isInWishlist(studentId, bookId);
    }

    public List<WishlistItem> getStudentWishlist(int studentId) throws DatabaseException {
        return wishlistDAO.getStudentWishlist(studentId);
    }

    public int getWishlistCount(int studentId) throws DatabaseException {
        return wishlistDAO.getWishlistCount(studentId);
    }
}
