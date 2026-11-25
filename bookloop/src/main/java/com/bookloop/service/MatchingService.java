package com.bookloop.service;

import com.bookloop.algorithm.MatchingEngine;
import com.bookloop.config.DatabaseConnection;
import com.bookloop.exception.DatabaseException;
import com.bookloop.exception.ExchangeException;
import com.bookloop.model.ExchangeCycle;
import com.bookloop.model.MatchResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MatchingService {
    private final MatchingEngine matchingEngine;

    public MatchingService() {
        this.matchingEngine = new MatchingEngine();
    }

    public List<MatchResult> getDirectMatches(int studentId) throws DatabaseException {
        return matchingEngine.findDirectMatches(studentId);
    }

    public List<ExchangeCycle> getCircularMatches(int studentId) throws DatabaseException {
        return matchingEngine.findCircularExchanges(studentId);
    }

    public int initiateCircularExchange(ExchangeCycle cycle, int initiatorId) throws ExchangeException, DatabaseException {
        if (cycle.getSteps() == null || cycle.getSteps().size() < 2) {
            throw new ExchangeException("Invalid circular exchange cycle.");
        }

        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try {
            conn.setAutoCommit(false);

            int cycleId;
            String insertCycle = "INSERT INTO circular_exchanges (cycle_hash, participant_count, status, initiated_by) " +
                                 "VALUES (?, ?, 'PROPOSED', ?) ON DUPLICATE KEY UPDATE id=LAST_INSERT_ID(id)";

            try (PreparedStatement stmt = conn.prepareStatement(insertCycle, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, cycle.getCycleHash());
                stmt.setInt(2, cycle.getSteps().size());
                stmt.setInt(3, initiatorId);
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) cycleId = rs.getInt(1);
                    else throw new SQLException("Could not retrieve circular exchange ID");
                }
            }

            // Insert each step participant
            String insertPart = "INSERT IGNORE INTO circular_exchange_participants " +
                                "(circular_exchange_id, student_id, gives_listing_id, receives_listing_id, position_order, confirmed) " +
                                "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(insertPart)) {
                for (int i = 0; i < cycle.getSteps().size(); i++) {
                    ExchangeCycle.Step s = cycle.getSteps().get(i);
                    int nextListing = (i + 1 < cycle.getSteps().size()) ? cycle.getSteps().get(i + 1).getListingId() : cycle.getSteps().get(0).getListingId();

                    stmt.setInt(1, cycleId);
                    stmt.setInt(2, s.getGiverId());
                    stmt.setInt(3, s.getListingId());
                    stmt.setInt(4, nextListing);
                    stmt.setInt(5, s.getPositionOrder());
                    stmt.setBoolean(6, s.getGiverId() == initiatorId); // Initiator automatically confirms
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            // Create notification for other circle members
            String notifySql = "INSERT INTO notifications (user_id, title, message, notification_type, reference_id, reference_type) " +
                               "VALUES (?, 'Circular Book Loop Invitation! 🔄', 'You were invited to a multi-student exchange circle! Check Circular Exchanges tab.', 'CYCLE_INVITE', ?, 'CYCLE')";

            try (PreparedStatement stmt = conn.prepareStatement(notifySql)) {
                for (ExchangeCycle.Step s : cycle.getSteps()) {
                    if (s.getGiverId() != initiatorId) {
                        stmt.setInt(1, s.getGiverId());
                        stmt.setInt(2, cycleId);
                        stmt.addBatch();
                    }
                }
                stmt.executeBatch();
            }

            conn.commit();
            return cycleId;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            throw new DatabaseException("Failed to initiate circular exchange: " + e.getMessage(), e);
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            db.releaseConnection(conn);
        }
    }
}
