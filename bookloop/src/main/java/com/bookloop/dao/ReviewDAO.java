package com.bookloop.dao;

import com.bookloop.config.DatabaseConnection;
import com.bookloop.exception.DatabaseException;
import com.bookloop.model.Review;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    public int createReview(Review review) throws DatabaseException {
        String sql = "INSERT INTO reviews (reviewer_id, reviewed_user_id, transaction_id, rating, comment) " +
                     "VALUES (?, ?, ?, ?, ?)";

        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, review.getReviewerId());
            stmt.setInt(2, review.getReviewedUserId());
            if (review.getTransactionId() != null) stmt.setInt(3, review.getTransactionId());
            else stmt.setNull(3, Types.INTEGER);
            stmt.setInt(4, review.getRating());
            stmt.setString(5, review.getComment());

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    review.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to submit review: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return -1;
    }

    public List<Review> getReviewsForUser(int userId) throws DatabaseException {
        String sql = "SELECT r.*, ur.name as reviewer_name, uo.name as reviewed_user_name " +
                     "FROM reviews r " +
                     "JOIN users ur ON r.reviewer_id = ur.id " +
                     "JOIN users uo ON r.reviewed_user_id = uo.id " +
                     "WHERE r.reviewed_user_id = ? ORDER BY r.created_at DESC";

        List<Review> list = new ArrayList<>();
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Review rev = new Review();
                    rev.setId(rs.getInt("id"));
                    rev.setReviewerId(rs.getInt("reviewer_id"));
                    rev.setReviewerName(rs.getString("reviewer_name"));
                    rev.setReviewedUserId(rs.getInt("reviewed_user_id"));
                    rev.setReviewedUserName(rs.getString("reviewed_user_name"));
                    int txId = rs.getInt("transaction_id");
                    if (!rs.wasNull()) rev.setTransactionId(txId);
                    rev.setRating(rs.getInt("rating"));
                    rev.setComment(rs.getString("comment"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) rev.setCreatedAt(ts.toLocalDateTime());
                    list.add(rev);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch reviews: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return list;
    }

    public double getAverageRating(int userId) throws DatabaseException {
        String sql = "SELECT AVG(rating) FROM reviews WHERE reviewed_user_id = ?";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Math.round(rs.getDouble(1) * 10.0) / 10.0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to calculate average rating: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return 5.0; // default initial score
    }

    public boolean hasReviewed(int reviewerId, int transactionId) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM reviews WHERE reviewer_id = ? AND transaction_id = ?";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reviewerId);
            stmt.setInt(2, transactionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check review status: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return false;
    }
}
