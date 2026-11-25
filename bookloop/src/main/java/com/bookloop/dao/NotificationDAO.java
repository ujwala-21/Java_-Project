package com.bookloop.dao;

import com.bookloop.config.DatabaseConnection;
import com.bookloop.exception.DatabaseException;
import com.bookloop.model.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public int createNotification(Notification n) throws DatabaseException {
        String sql = "INSERT INTO notifications (user_id, title, message, notification_type, reference_id, reference_type, is_read) " +
                     "VALUES (?, ?, ?, ?, ?, ?, FALSE)";

        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, n.getUserId());
            stmt.setString(2, n.getTitle());
            stmt.setString(3, n.getMessage());
            stmt.setString(4, n.getNotificationType());
            if (n.getReferenceId() != null) stmt.setInt(5, n.getReferenceId());
            else stmt.setNull(5, Types.INTEGER);
            stmt.setString(6, n.getReferenceType());

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    n.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create notification: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return -1;
    }

    public List<Notification> getNotificationsForUser(int userId) throws DatabaseException {
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC LIMIT 50";
        List<Notification> list = new ArrayList<>();
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Notification n = new Notification();
                    n.setId(rs.getInt("id"));
                    n.setUserId(rs.getInt("user_id"));
                    n.setTitle(rs.getString("title"));
                    n.setMessage(rs.getString("message"));
                    n.setNotificationType(rs.getString("notification_type"));
                    int refId = rs.getInt("reference_id");
                    if (!rs.wasNull()) n.setReferenceId(refId);
                    n.setReferenceType(rs.getString("reference_type"));
                    n.setRead(rs.getBoolean("is_read"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) n.setCreatedAt(ts.toLocalDateTime());
                    list.add(n);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch notifications: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return list;
    }

    public int getUnreadCount(int userId) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = FALSE";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count unread notifications: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return 0;
    }

    public boolean markAllAsRead(int userId) throws DatabaseException {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE user_id = ?";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to mark notifications as read: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
    }
}
