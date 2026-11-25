package com.bookloop.dao;

import com.bookloop.config.DatabaseConnection;
import com.bookloop.exception.DatabaseException;
import com.bookloop.model.ExchangeRequest;
import com.bookloop.model.ExchangeTransaction;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeDAO {

    public int createRequest(ExchangeRequest req) throws DatabaseException {
        String sql = "INSERT INTO exchange_requests (requester_id, listing_id, offered_listing_id, message, status, request_type) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, req.getRequesterId());
            stmt.setInt(2, req.getListingId());
            if (req.getOfferedListingId() != null) stmt.setInt(3, req.getOfferedListingId());
            else stmt.setNull(3, Types.INTEGER);
            stmt.setString(4, req.getMessage());
            stmt.setString(5, req.getStatus() != null ? req.getStatus().name() : "PENDING");
            String reqTypeStr = "EXCHANGE";
            if (req.getRequestType() != null) {
                reqTypeStr = (req.getRequestType() == ExchangeRequest.RequestType.SELL) ? "BUY" : req.getRequestType().name();
            }
            stmt.setString(6, reqTypeStr);

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    req.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create exchange request: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return -1;
    }

    public ExchangeRequest findRequestById(int id) throws DatabaseException {
        String sql = "SELECT er.*, u.name as requester_name, bl.owner_id, " +
                     "uo.name as owner_name, b.title as listing_title, " +
                     "ob.title as offered_listing_title " +
                     "FROM exchange_requests er " +
                     "JOIN users u ON er.requester_id = u.id " +
                     "JOIN book_listings bl ON er.listing_id = bl.id " +
                     "JOIN users uo ON bl.owner_id = uo.id " +
                     "JOIN books b ON bl.book_id = b.id " +
                     "LEFT JOIN book_listings obl ON er.offered_listing_id = obl.id " +
                     "LEFT JOIN books ob ON obl.book_id = ob.id " +
                     "WHERE er.id = ?";

        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRequestFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find request by ID: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return null;
    }

    public List<ExchangeRequest> getIncomingRequests(int ownerId) throws DatabaseException {
        String sql = "SELECT er.*, u.name as requester_name, bl.owner_id, " +
                     "uo.name as owner_name, b.title as listing_title, " +
                     "ob.title as offered_listing_title " +
                     "FROM exchange_requests er " +
                     "JOIN users u ON er.requester_id = u.id " +
                     "JOIN book_listings bl ON er.listing_id = bl.id " +
                     "JOIN users uo ON bl.owner_id = uo.id " +
                     "JOIN books b ON bl.book_id = b.id " +
                     "LEFT JOIN book_listings obl ON er.offered_listing_id = obl.id " +
                     "LEFT JOIN books ob ON obl.book_id = ob.id " +
                     "WHERE bl.owner_id = ? ORDER BY er.created_at DESC";

        List<ExchangeRequest> list = new ArrayList<>();
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ownerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRequestFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch incoming requests: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return list;
    }

    public List<ExchangeRequest> getOutgoingRequests(int requesterId) throws DatabaseException {
        String sql = "SELECT er.*, u.name as requester_name, bl.owner_id, " +
                     "uo.name as owner_name, b.title as listing_title, " +
                     "ob.title as offered_listing_title " +
                     "FROM exchange_requests er " +
                     "JOIN users u ON er.requester_id = u.id " +
                     "JOIN book_listings bl ON er.listing_id = bl.id " +
                     "JOIN users uo ON bl.owner_id = uo.id " +
                     "JOIN books b ON bl.book_id = b.id " +
                     "LEFT JOIN book_listings obl ON er.offered_listing_id = obl.id " +
                     "LEFT JOIN books ob ON obl.book_id = ob.id " +
                     "WHERE er.requester_id = ? ORDER BY er.created_at DESC";

        List<ExchangeRequest> list = new ArrayList<>();
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, requesterId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRequestFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch outgoing requests: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return list;
    }

    public boolean updateRequestStatus(int requestId, String status) throws DatabaseException {
        String sql = "UPDATE exchange_requests SET status = ? WHERE id = ?";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, requestId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update request status: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
    }

    public int createTransaction(ExchangeTransaction tx) throws DatabaseException {
        String sql = "INSERT INTO exchange_transactions (reservation_id, giver_id, receiver_id, listing_id, " +
                     "transaction_type, credits_transferred, due_date, status, completed_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (tx.getReservationId() != null) stmt.setInt(1, tx.getReservationId());
            else stmt.setNull(1, Types.INTEGER);
            stmt.setInt(2, tx.getGiverId());
            stmt.setInt(3, tx.getReceiverId());
            stmt.setInt(4, tx.getListingId());
            stmt.setString(5, tx.getTransactionType().name());
            stmt.setBigDecimal(6, tx.getCreditsTransferred() != null ? tx.getCreditsTransferred() : BigDecimal.ZERO);
            if (tx.getDueDate() != null) stmt.setTimestamp(7, Timestamp.valueOf(tx.getDueDate()));
            else stmt.setNull(7, Types.TIMESTAMP);
            stmt.setString(8, tx.getStatus().name());
            if (tx.getCompletedAt() != null) stmt.setTimestamp(9, Timestamp.valueOf(tx.getCompletedAt()));
            else stmt.setNull(9, Types.TIMESTAMP);

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    tx.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create exchange transaction: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return -1;
    }

    public List<ExchangeTransaction> getStudentTransactions(int studentId) throws DatabaseException {
        String sql = "SELECT t.*, ug.name as giver_name, ur.name as receiver_name, b.title as book_title " +
                     "FROM exchange_transactions t " +
                     "JOIN users ug ON t.giver_id = ug.id " +
                     "JOIN users ur ON t.receiver_id = ur.id " +
                     "JOIN book_listings bl ON t.listing_id = bl.id " +
                     "JOIN books b ON bl.book_id = b.id " +
                     "WHERE t.giver_id = ? OR t.receiver_id = ? " +
                     "ORDER BY t.created_at DESC";

        List<ExchangeTransaction> list = new ArrayList<>();
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTransactionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load student transactions: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return list;
    }

    public int getCompletedExchangeCount() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM exchange_transactions WHERE status = 'COMPLETED'";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count completed exchanges: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return 0;
    }

    private ExchangeRequest mapRequestFromResultSet(ResultSet rs) throws SQLException {
        ExchangeRequest r = new ExchangeRequest();
        r.setId(rs.getInt("id"));
        r.setRequesterId(rs.getInt("requester_id"));
        r.setRequesterName(rs.getString("requester_name"));
        r.setListingId(rs.getInt("listing_id"));
        r.setListingTitle(rs.getString("listing_title"));
        r.setOwnerId(rs.getInt("owner_id"));
        r.setOwnerName(rs.getString("owner_name"));
        int offId = rs.getInt("offered_listing_id");
        if (!rs.wasNull()) {
            r.setOfferedListingId(offId);
            r.setOfferedListingTitle(rs.getString("offered_listing_title"));
        }
        r.setMessage(rs.getString("message"));
        r.setStatus(ExchangeRequest.RequestStatus.valueOf(rs.getString("status")));
        r.setRequestType(ExchangeRequest.RequestType.valueOf(rs.getString("request_type")));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) r.setCreatedAt(ts.toLocalDateTime());
        return r;
    }

    private ExchangeTransaction mapTransactionFromResultSet(ResultSet rs) throws SQLException {
        ExchangeTransaction t = new ExchangeTransaction();
        t.setId(rs.getInt("id"));
        int resId = rs.getInt("reservation_id");
        if (!rs.wasNull()) t.setReservationId(resId);
        t.setGiverId(rs.getInt("giver_id"));
        t.setGiverName(rs.getString("giver_name"));
        t.setReceiverId(rs.getInt("receiver_id"));
        t.setReceiverName(rs.getString("receiver_name"));
        t.setListingId(rs.getInt("listing_id"));
        t.setBookTitle(rs.getString("book_title"));
        t.setTransactionType(ExchangeTransaction.TransactionType.valueOf(rs.getString("transaction_type")));
        t.setCreditsTransferred(rs.getBigDecimal("credits_transferred"));
        t.setStatus(ExchangeTransaction.TransactionStatus.valueOf(rs.getString("status")));
        Timestamp cAt = rs.getTimestamp("created_at");
        if (cAt != null) t.setCreatedAt(cAt.toLocalDateTime());
        Timestamp compAt = rs.getTimestamp("completed_at");
        if (compAt != null) t.setCompletedAt(compAt.toLocalDateTime());
        return t;
    }
}
