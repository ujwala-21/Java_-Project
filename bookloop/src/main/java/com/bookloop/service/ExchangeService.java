package com.bookloop.service;

import com.bookloop.config.AppConfig;
import com.bookloop.config.DatabaseConnection;
import com.bookloop.dao.ExchangeDAO;
import com.bookloop.dao.ListingDAO;
import com.bookloop.dao.ReservationDAO;
import com.bookloop.exception.DatabaseException;
import com.bookloop.exception.ExchangeException;
import com.bookloop.model.BookListing;
import com.bookloop.model.ExchangeRequest;
import com.bookloop.model.ExchangeTransaction;
import com.bookloop.model.Reservation;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ExchangeService {
    private final ExchangeDAO exchangeDAO;
    private final ReservationDAO reservationDAO;
    private final ListingDAO listingDAO;

    public ExchangeService() {
        this.exchangeDAO = new ExchangeDAO();
        this.reservationDAO = new ReservationDAO();
        this.listingDAO = new ListingDAO();
    }

    public int createRequest(int requesterId, int listingId, Integer offeredListingId, String message, String requestType)
            throws ExchangeException, DatabaseException {

        BookListing listing = listingDAO.findById(listingId);
        if (listing == null) {
            throw new ExchangeException("Listing not found.");
        }
        if (listing.getOwnerId() == requesterId) {
            throw new ExchangeException("You cannot request your own book.");
        }
        if (listing.getStatus() != BookListing.ListingStatus.AVAILABLE) {
            throw new ExchangeException("This book is no longer available for request.");
        }

        ExchangeRequest req = new ExchangeRequest();
        req.setRequesterId(requesterId);
        req.setListingId(listingId);
        req.setOfferedListingId(offeredListingId);
        req.setMessage(message != null ? message.trim() : "I am interested in this book!");
        req.setStatus(ExchangeRequest.RequestStatus.PENDING);

        String rt = requestType != null ? requestType.toUpperCase() : "EXCHANGE";
        if ("SELL".equals(rt)) {
            rt = "BUY";
        }
        try {
            req.setRequestType(ExchangeRequest.RequestType.valueOf(rt));
        } catch (IllegalArgumentException e) {
            req.setRequestType(ExchangeRequest.RequestType.BUY);
        }

        return exchangeDAO.createRequest(req);
    }

    public Reservation acceptRequest(int requestId, int ownerId) throws ExchangeException, DatabaseException {
        ExchangeRequest req = exchangeDAO.findRequestById(requestId);
        if (req == null) {
            throw new ExchangeException("Request not found.");
        }
        if (req.getOwnerId() != ownerId) {
            throw new ExchangeException("Only the book owner can accept this request.");
        }
        if (req.getStatus() != ExchangeRequest.RequestStatus.PENDING) {
            throw new ExchangeException("This request has already been " + req.getStatus().name().toLowerCase() + ".");
        }

        // Atomic DB transaction
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try {
            conn.setAutoCommit(false);

            // 1. Mark request accepted
            try (PreparedStatement stmt = conn.prepareStatement("UPDATE exchange_requests SET status = 'ACCEPTED' WHERE id = ?")) {
                stmt.setInt(1, requestId);
                stmt.executeUpdate();
            }

            // 2. Mark listing as RESERVED
            try (PreparedStatement stmt = conn.prepareStatement("UPDATE book_listings SET status = 'RESERVED' WHERE id = ?")) {
                stmt.setInt(1, req.getListingId());
                stmt.executeUpdate();
            }

            // 3. Create reservation (7 days expiry)
            LocalDateTime expiresAt = LocalDateTime.now().plusDays(AppConfig.RESERVATION_EXPIRY_DAYS);
            int reservationId;
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO reservations (request_id, requester_id, owner_id, listing_id, status, expires_at) VALUES (?, ?, ?, ?, 'ACTIVE', ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, requestId);
                stmt.setInt(2, req.getRequesterId());
                stmt.setInt(3, ownerId);
                stmt.setInt(4, req.getListingId());
                stmt.setTimestamp(5, java.sql.Timestamp.valueOf(expiresAt));
                stmt.executeUpdate();
                try (var rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) reservationId = rs.getInt(1);
                    else throw new SQLException("Reservation ID generation failed");
                }
            }

            // 4. Create notification for requester
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO notifications (user_id, title, message, notification_type, reference_id, reference_type) VALUES (?, ?, ?, 'REQUEST_ACCEPTED', ?, 'RESERVATION')")) {
                stmt.setInt(1, req.getRequesterId());
                stmt.setString(2, "Request Accepted! 🎉");
                stmt.setString(3, req.getOwnerName() + " accepted your request for \"" + req.getListingTitle() + "\". Please coordinate campus handover.");
                stmt.setInt(4, reservationId);
                stmt.executeUpdate();
            }

            conn.commit();

            return reservationDAO.findById(reservationId);
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            throw new DatabaseException("Transaction failed while accepting request: " + e.getMessage(), e);
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            db.releaseConnection(conn);
        }
    }

    public boolean rejectRequest(int requestId, int ownerId) throws ExchangeException, DatabaseException {
        ExchangeRequest req = exchangeDAO.findRequestById(requestId);
        if (req == null) throw new ExchangeException("Request not found.");
        if (req.getOwnerId() != ownerId) throw new ExchangeException("You are not the owner of this book.");
        return exchangeDAO.updateRequestStatus(requestId, "REJECTED");
    }

    public boolean cancelRequest(int requestId, int requesterId) throws ExchangeException, DatabaseException {
        ExchangeRequest req = exchangeDAO.findRequestById(requestId);
        if (req == null) throw new ExchangeException("Request not found.");
        if (req.getRequesterId() != requesterId) throw new ExchangeException("You are not the creator of this request.");
        return exchangeDAO.updateRequestStatus(requestId, "CANCELLED");
    }

    public ExchangeTransaction completeExchange(int reservationId, int userId) throws ExchangeException, DatabaseException {
        Reservation res = reservationDAO.findById(reservationId);
        if (res == null) throw new ExchangeException("Reservation not found.");

        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try {
            conn.setAutoCommit(false);

            // 1. Mark reservation completed
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE reservations SET status = 'COMPLETED', completed_at = NOW() WHERE id = ?")) {
                stmt.setInt(1, reservationId);
                stmt.executeUpdate();
            }

            // 2. Mark listing completed
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE book_listings SET status = 'COMPLETED' WHERE id = ?")) {
                stmt.setInt(1, res.getListingId());
                stmt.executeUpdate();
            }

            // 3. Create exchange transaction record
            int txId;
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO exchange_transactions (reservation_id, giver_id, receiver_id, listing_id, transaction_type, credits_transferred, status, completed_at) VALUES (?, ?, ?, ?, 'EXCHANGE', 50.00, 'COMPLETED', NOW())",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, reservationId);
                stmt.setInt(2, res.getOwnerId());
                stmt.setInt(3, res.getRequesterId());
                stmt.setInt(4, res.getListingId());
                stmt.executeUpdate();
                try (var rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) txId = rs.getInt(1);
                    else throw new SQLException("Transaction ID generation failed");
                }
            }

            // 4. Reward giver 50 credits in credit wallet
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE credit_wallets SET balance = balance + 50.00 WHERE student_id = ?")) {
                stmt.setInt(1, res.getOwnerId());
                stmt.executeUpdate();
            }

            // 5. Notify both parties
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO notifications (user_id, title, message, notification_type, reference_id, reference_type) VALUES (?, ?, ?, 'EXCHANGE_COMPLETED', ?, 'TRANSACTION')")) {
                stmt.setInt(1, res.getOwnerId());
                stmt.setString(2, "Exchange Completed! ⭐");
                stmt.setString(3, "Handover confirmed for \"" + res.getBookTitle() + "\". You earned 50 exchange credits!");
                stmt.setInt(4, txId);
                stmt.executeUpdate();

                stmt.setInt(1, res.getRequesterId());
                stmt.setString(2, "Book Received! ⭐");
                stmt.setString(3, "You confirmed receiving \"" + res.getBookTitle() + "\". Happy studying!");
                stmt.setInt(4, txId);
                stmt.executeUpdate();
            }

            conn.commit();

            ExchangeTransaction tx = new ExchangeTransaction();
            tx.setId(txId);
            tx.setReservationId(reservationId);
            tx.setGiverId(res.getOwnerId());
            tx.setReceiverId(res.getRequesterId());
            tx.setListingId(res.getListingId());
            tx.setBookTitle(res.getBookTitle());
            tx.setStatus(ExchangeTransaction.TransactionStatus.COMPLETED);
            return tx;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            throw new DatabaseException("Failed to complete exchange transaction: " + e.getMessage(), e);
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            db.releaseConnection(conn);
        }
    }

    public List<ExchangeRequest> getIncomingRequests(int ownerId) throws DatabaseException {
        return exchangeDAO.getIncomingRequests(ownerId);
    }

    public List<ExchangeRequest> getOutgoingRequests(int requesterId) throws DatabaseException {
        return exchangeDAO.getOutgoingRequests(requesterId);
    }

    public List<ExchangeTransaction> getStudentTransactions(int studentId) throws DatabaseException {
        return exchangeDAO.getStudentTransactions(studentId);
    }
}
