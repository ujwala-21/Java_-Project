package com.bookloop.dao;

import com.bookloop.config.DatabaseConnection;
import com.bookloop.exception.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnalyticsDAO {

    public Map<String, Object> getSystemStatistics() throws DatabaseException {
        Map<String, Object> stats = new HashMap<>();

        String sql = "SELECT " +
                     "(SELECT COUNT(*) FROM students) as total_students, " +
                     "(SELECT COUNT(*) FROM books) as total_books, " +
                     "(SELECT COUNT(*) FROM book_listings WHERE status = 'AVAILABLE') as available_listings, " +
                     "(SELECT COUNT(*) FROM book_listings) as total_listings, " +
                     "(SELECT COUNT(*) FROM exchange_transactions WHERE status = 'COMPLETED') as completed_exchanges, " +
                     "(SELECT COUNT(*) FROM exchange_requests WHERE status = 'PENDING') as pending_requests, " +
                     "(SELECT COUNT(*) FROM reservations WHERE status = 'ACTIVE') as active_reservations, " +
                     "(SELECT COUNT(*) FROM book_listings WHERE listing_type = 'DONATE' AND status = 'COMPLETED') as books_donated, " +
                     "(SELECT COUNT(*) FROM circular_exchanges) as total_circular_exchanges";

        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                stats.put("totalStudents", rs.getInt("total_students"));
                stats.put("totalBooks", rs.getInt("total_books"));
                stats.put("availableListings", rs.getInt("available_listings"));
                stats.put("totalListings", rs.getInt("total_listings"));
                stats.put("completedExchanges", rs.getInt("completed_exchanges"));
                stats.put("pendingRequests", rs.getInt("pending_requests"));
                stats.put("activeReservations", rs.getInt("active_reservations"));
                stats.put("booksDonated", rs.getInt("books_donated"));
                stats.put("circularExchanges", rs.getInt("total_circular_exchanges"));

                int totalReqs = rs.getInt("completed_exchanges") + rs.getInt("pending_requests");
                double successRate = totalReqs > 0 ? ((double) rs.getInt("completed_exchanges") / totalReqs) * 100.0 : 100.0;
                stats.put("successRate", Math.round(successRate * 10.0) / 10.0);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load analytics: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return stats;
    }

    public Map<String, Integer> getListingsByType() throws DatabaseException {
        String sql = "SELECT listing_type, COUNT(*) FROM book_listings GROUP BY listing_type";
        Map<String, Integer> map = new LinkedHashMap<>();
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to aggregate listings by type: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return map;
    }

    public Map<String, Integer> getListingsByCondition() throws DatabaseException {
        String sql = "SELECT condition_type, COUNT(*) FROM book_listings GROUP BY condition_type";
        Map<String, Integer> map = new LinkedHashMap<>();
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to aggregate listings by condition: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return map;
    }

    public Map<String, Integer> getMostWantedBooks() throws DatabaseException {
        String sql = "SELECT book_title, COUNT(*) as demand FROM wishlist_items GROUP BY book_title ORDER BY demand DESC LIMIT 6";
        Map<String, Integer> map = new LinkedHashMap<>();
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get most wanted books: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return map;
    }
}
