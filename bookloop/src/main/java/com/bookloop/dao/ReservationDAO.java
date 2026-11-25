package com.bookloop.dao;

import com.bookloop.config.DatabaseConnection;
import com.bookloop.exception.DatabaseException;
import com.bookloop.model.Reservation;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public int createReservation(Reservation res) throws DatabaseException {
        String sql = "INSERT INTO reservations (request_id, requester_id, owner_id, listing_id, status, expires_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, res.getRequestId());
            stmt.setInt(2, res.getRequesterId());
            stmt.setInt(3, res.getOwnerId());
            stmt.setInt(4, res.getListingId());
            stmt.setString(5, res.getStatus().name());
            stmt.setTimestamp(6, Timestamp.valueOf(res.getExpiresAt()));

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    res.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create reservation: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return -1;
    }

    public Reservation findById(int id) throws DatabaseException {
        String sql = "SELECT r.*, ur.name as requester_name, uo.name as owner_name, " +
                     "b.title as book_title, bl.image_path " +
                     "FROM reservations r " +
                     "JOIN users ur ON r.requester_id = ur.id " +
                     "JOIN users uo ON r.owner_id = uo.id " +
                     "JOIN book_listings bl ON r.listing_id = bl.id " +
                     "JOIN books b ON bl.book_id = b.id " +
                     "WHERE r.id = ?";

        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapReservationFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find reservation by ID: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return null;
    }

    public List<Reservation> getStudentReservations(int studentId) throws DatabaseException {
        String sql = "SELECT r.*, ur.name as requester_name, uo.name as owner_name, " +
                     "b.title as book_title, bl.image_path " +
                     "FROM reservations r " +
                     "JOIN users ur ON r.requester_id = ur.id " +
                     "JOIN users uo ON r.owner_id = uo.id " +
                     "JOIN book_listings bl ON r.listing_id = bl.id " +
                     "JOIN books b ON bl.book_id = b.id " +
                     "WHERE r.requester_id = ? OR r.owner_id = ? " +
                     "ORDER BY r.created_at DESC";

        List<Reservation> list = new ArrayList<>();
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapReservationFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load student reservations: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return list;
    }

    public boolean confirmByRequester(int reservationId) throws DatabaseException {
        String sql = "UPDATE reservations SET requester_confirmed_at = NOW(), " +
                     "status = CASE WHEN owner_confirmed_at IS NOT NULL THEN 'COMPLETED' ELSE 'REQUESTER_CONFIRMED' END " +
                     "WHERE id = ?";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to confirm reservation: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
    }

    public boolean confirmByOwner(int reservationId) throws DatabaseException {
        String sql = "UPDATE reservations SET owner_confirmed_at = NOW(), " +
                     "status = CASE WHEN requester_confirmed_at IS NOT NULL THEN 'COMPLETED' ELSE 'OWNER_CONFIRMED' END " +
                     "WHERE id = ?";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to confirm reservation: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
    }

    public boolean cancelReservation(int reservationId) throws DatabaseException {
        String sql = "UPDATE reservations SET status = 'CANCELLED', cancelled_at = NOW() WHERE id = ?";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to cancel reservation: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
    }

    public int getActiveReservationCount() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM reservations WHERE status IN ('ACTIVE', 'REQUESTER_CONFIRMED', 'OWNER_CONFIRMED')";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count active reservations: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return 0;
    }

    private Reservation mapReservationFromResultSet(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setId(rs.getInt("id"));
        r.setRequestId(rs.getInt("request_id"));
        r.setRequesterId(rs.getInt("requester_id"));
        r.setRequesterName(rs.getString("requester_name"));
        r.setOwnerId(rs.getInt("owner_id"));
        r.setOwnerName(rs.getString("owner_name"));
        r.setListingId(rs.getInt("listing_id"));
        r.setBookTitle(rs.getString("book_title"));
        r.setImagePath(rs.getString("image_path"));
        r.setStatus(Reservation.ReservationStatus.valueOf(rs.getString("status")));

        Timestamp exp = rs.getTimestamp("expires_at");
        if (exp != null) r.setExpiresAt(exp.toLocalDateTime());
        Timestamp rc = rs.getTimestamp("requester_confirmed_at");
        if (rc != null) r.setRequesterConfirmedAt(rc.toLocalDateTime());
        Timestamp oc = rs.getTimestamp("owner_confirmed_at");
        if (oc != null) r.setOwnerConfirmedAt(oc.toLocalDateTime());
        Timestamp comp = rs.getTimestamp("completed_at");
        if (comp != null) r.setCompletedAt(comp.toLocalDateTime());
        Timestamp canc = rs.getTimestamp("cancelled_at");
        if (canc != null) r.setCancelledAt(canc.toLocalDateTime());
        Timestamp cAt = rs.getTimestamp("created_at");
        if (cAt != null) r.setCreatedAt(cAt.toLocalDateTime());
        return r;
    }
}
