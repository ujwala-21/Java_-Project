package com.bookloop.controller;

import com.bookloop.exception.DatabaseException;
import com.bookloop.exception.ValidationException;
import com.bookloop.model.Book;
import com.bookloop.model.BookListing;
import com.bookloop.model.Course;
import com.bookloop.model.Department;
import com.bookloop.model.WishlistItem;
import com.bookloop.service.BookService;
import com.bookloop.service.ListingService;
import com.bookloop.service.WishlistService;
import com.bookloop.util.SessionManager;

import java.util.List;

public class BookController {
    private final BookService bookService;
    private final ListingService listingService;
    private final WishlistService wishlistService;

    public BookController() {
        this.bookService = new BookService();
        this.listingService = new ListingService();
        this.wishlistService = new WishlistService();
    }

    public int createBookAndListing(Book book, BookListing listing) throws ValidationException, DatabaseException {
        int bookId = bookService.createOrFindBook(book);
        listing.setBookId(bookId);
        listing.setOwnerId(SessionManager.getInstance().getCurrentUserId());
        return listingService.createListing(listing);
    }

    public List<BookListing> searchMarketplace(String query, String type, String condition,
                                               Integer deptId, Double maxPrice, String sortBy) throws DatabaseException {
        return listingService.searchListings(query, type, condition, deptId, maxPrice, sortBy);
    }

    public List<BookListing> getMyListings() throws DatabaseException {
        int currentUserId = SessionManager.getInstance().getCurrentUserId();
        return listingService.getMyListings(currentUserId);
    }

    public boolean deleteListing(int listingId) throws ValidationException, DatabaseException {
        int currentUserId = SessionManager.getInstance().getCurrentUserId();
        return listingService.deleteListing(listingId, currentUserId);
    }

    public boolean updateListingStatus(int listingId, String status) throws ValidationException, DatabaseException {
        int currentUserId = SessionManager.getInstance().getCurrentUserId();
        return listingService.updateStatus(listingId, status, currentUserId);
    }

    public boolean addToWishlist(int bookId) throws ValidationException, DatabaseException {
        int studentId = SessionManager.getInstance().getCurrentUserId();
        return wishlistService.addToWishlist(studentId, bookId);
    }

    public boolean removeFromWishlist(int bookId) throws DatabaseException {
        int studentId = SessionManager.getInstance().getCurrentUserId();
        return wishlistService.removeFromWishlist(studentId, bookId);
    }

    public List<WishlistItem> getMyWishlist() throws DatabaseException {
        int studentId = SessionManager.getInstance().getCurrentUserId();
        return wishlistService.getStudentWishlist(studentId);
    }

    public List<Department> getDepartments() throws DatabaseException {
        return bookService.getAllDepartments();
    }

    public List<Course> getCoursesByDepartment(int deptId) throws DatabaseException {
        return bookService.getCoursesByDepartment(deptId);
    }

    public List<Book> getSemesterRecommendations(int deptId, int year, int sem) throws DatabaseException {
        return bookService.getRecommendedBooks(deptId, year, sem);
    }

    public boolean updateListing(BookListing listing) throws ValidationException, DatabaseException {
        int currentUserId = SessionManager.getInstance().getCurrentUserId();
        return listingService.updateListing(listing, currentUserId);
    }

    public BookListing getListingByBookId(int bookId) throws DatabaseException {
        return listingService.getFirstListingByBookId(bookId);
    }
}
