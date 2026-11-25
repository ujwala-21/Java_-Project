package com.bookloop.controller;

import com.bookloop.dao.BookDAO;
import com.bookloop.dao.ListingDAO;
import com.bookloop.dao.UserDAO;
import com.bookloop.exception.DatabaseException;
import com.bookloop.model.Book;
import com.bookloop.model.BookListing;
import com.bookloop.model.Student;
import com.bookloop.service.AnalyticsService;
import com.bookloop.service.NotificationService;

import java.util.List;
import java.util.Map;

public class AdminController {
    private final UserDAO userDAO;
    private final BookDAO bookDAO;
    private final ListingDAO listingDAO;
    private final AnalyticsService analyticsService;
    private final NotificationService notificationService;

    public AdminController() {
        this.userDAO = new UserDAO();
        this.bookDAO = new BookDAO();
        this.listingDAO = new ListingDAO();
        this.analyticsService = new AnalyticsService();
        this.notificationService = new NotificationService();
    }

    public List<Student> getAllStudents() throws DatabaseException {
        return userDAO.getAllStudents();
    }

    public boolean toggleStudentStatus(int studentId, boolean currentActive) throws DatabaseException {
        return userDAO.setUserActiveStatus(studentId, !currentActive);
    }

    public List<Book> getAllCatalogBooks() throws DatabaseException {
        return bookDAO.findAll();
    }

    public List<BookListing> getAllListings() throws DatabaseException {
        return listingDAO.findAll();
    }

    public boolean removeInappropriateListing(int listingId) throws DatabaseException {
        return listingDAO.updateStatus(listingId, "CANCELLED");
    }

    public Map<String, Object> getDashboardStats() throws DatabaseException {
        return analyticsService.getSystemStatistics();
    }

    public Map<String, Integer> getListingsByType() throws DatabaseException {
        return analyticsService.getListingsByType();
    }

    public Map<String, Integer> getListingsByCondition() throws DatabaseException {
        return analyticsService.getListingsByCondition();
    }

    public Map<String, Integer> getMostWantedBooks() throws DatabaseException {
        return analyticsService.getMostWantedBooks();
    }
}
