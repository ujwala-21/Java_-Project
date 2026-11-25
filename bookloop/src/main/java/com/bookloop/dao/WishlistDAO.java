package com.bookloop.dao;

import com.bookloop.config.DatabaseConnection;
import com.bookloop.exception.DatabaseException;
import com.bookloop.model.WishlistItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WishlistDAO {

    public boolean addToWishlist(int studentId, int bookId) throws DatabaseException {
        String sql = "INSERT IGNORE INTO wishlist_items (student_id, book_id, book_title, author, subject, department_id) " +
                     "SELECT ?, b.id, b.title, b.author, b.subject, b.department_id FROM books b WHERE b.id = ?";

        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, bookId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to add book to wishlist: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
    }

    public boolean removeFromWishlist(int studentId, int bookId) throws DatabaseException {
        String sql = "DELETE FROM wishlist_items WHERE student_id = ? AND (book_id = ? OR id = ?)";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, bookId);
            stmt.setInt(3, bookId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to remove item from wishlist: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
    }

    public boolean isInWishlist(int studentId, int bookId) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM wishlist_items WHERE student_id = ? AND book_id = ?";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check wishlist: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return false;
    }

    public List<WishlistItem> getStudentWishlist(int studentId) throws DatabaseException {
        String sql = "SELECT w.*, d.name as department_name, " +
                     "(SELECT COUNT(*) FROM book_listings bl WHERE bl.book_id = w.book_id AND bl.status = 'AVAILABLE') as available_count " +
                     "FROM wishlist_items w " +
                     "LEFT JOIN departments d ON w.department_id = d.id " +
                     "WHERE w.student_id = ? ORDER BY w.created_at DESC";

        List<WishlistItem> list = new ArrayList<>();
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    WishlistItem item = new WishlistItem();
                    item.setId(rs.getInt("id"));
                    item.setStudentId(rs.getInt("student_id"));
                    int bId = rs.getInt("book_id");
                    if (!rs.wasNull()) item.setBookId(bId);
                    item.setBookTitle(rs.getString("book_title"));
                    item.setAuthor(rs.getString("author"));
                    item.setSubject(rs.getString("subject"));
                    int dId = rs.getInt("department_id");
                    if (!rs.wasNull()) item.setDepartmentId(dId);
                    item.setDepartmentName(rs.getString("department_name"));
                    item.setAvailableListingsCount(rs.getInt("available_count"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) item.setCreatedAt(ts.toLocalDateTime());
                    list.add(item);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch wishlist: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return list;
    }

    public int getWishlistCount(int studentId) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM wishlist_items WHERE student_id = ?";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count wishlist items: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return 0;
    }
}
