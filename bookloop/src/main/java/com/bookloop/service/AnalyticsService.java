package com.bookloop.service;

import com.bookloop.dao.AnalyticsDAO;
import com.bookloop.exception.DatabaseException;

import java.util.Map;

public class AnalyticsService {
    private final AnalyticsDAO analyticsDAO;

    public AnalyticsService() {
        this.analyticsDAO = new AnalyticsDAO();
    }

    public Map<String, Object> getSystemStatistics() throws DatabaseException {
        return analyticsDAO.getSystemStatistics();
    }

    public Map<String, Integer> getListingsByType() throws DatabaseException {
        return analyticsDAO.getListingsByType();
    }

    public Map<String, Integer> getListingsByCondition() throws DatabaseException {
        return analyticsDAO.getListingsByCondition();
    }

    public Map<String, Integer> getMostWantedBooks() throws DatabaseException {
        return analyticsDAO.getMostWantedBooks();
    }
}
