package com.bookloop.dao;

import com.bookloop.config.DatabaseConnection;
import com.bookloop.exception.DatabaseException;
import com.bookloop.model.CreditTransaction;
import com.bookloop.model.CreditWallet;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CreditDAO {

    public CreditWallet getWallet(int studentId) throws DatabaseException {
        String sql = "SELECT * FROM credit_wallets WHERE student_id = ?";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CreditWallet w = new CreditWallet();
                    w.setId(rs.getInt("id"));
                    w.setStudentId(rs.getInt("student_id"));
                    w.setBalance(rs.getBigDecimal("balance"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) w.setCreatedAt(ts.toLocalDateTime());
                    return w;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get credit wallet: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return null;
    }

    public BigDecimal getBalance(int studentId) throws DatabaseException {
        CreditWallet w = getWallet(studentId);
        return w != null ? w.getBalance() : BigDecimal.ZERO;
    }

    public boolean hasClaimedDailyBonusToday(int studentId) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM credit_transactions ct " +
                     "JOIN credit_wallets cw ON ct.wallet_id = cw.id " +
                     "WHERE cw.student_id = ? " +
                     "AND ct.reason LIKE '%Daily%' " +
                     "AND DATE(ct.created_at) = CURRENT_DATE()";
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check daily bonus: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return false;
    }

    public boolean adjustCredits(int studentId, BigDecimal amount, String type, String reason) throws DatabaseException {
        String updateWalletSql = "CREDIT".equalsIgnoreCase(type)
            ? "UPDATE credit_wallets SET balance = balance + ? WHERE student_id = ?"
            : "UPDATE credit_wallets SET balance = balance - ? WHERE student_id = ? AND balance >= ?";

        String insertTxSql = "INSERT INTO credit_transactions (wallet_id, amount, transaction_type, reason, balance_after) " +
                             "VALUES ((SELECT id FROM credit_wallets WHERE student_id = ?), ?, ?, ?, " +
                             "(SELECT balance FROM credit_wallets WHERE student_id = ?))";

        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(updateWalletSql)) {
                stmt.setBigDecimal(1, amount);
                stmt.setInt(2, studentId);
                if (!"CREDIT".equalsIgnoreCase(type)) {
                    stmt.setBigDecimal(3, amount);
                }
                int rows = stmt.executeUpdate();
                if (rows == 0) {
                    conn.rollback();
                    return false; // Insufficient balance
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(insertTxSql)) {
                stmt.setInt(1, studentId);
                stmt.setBigDecimal(2, amount);
                stmt.setString(3, type.toUpperCase());
                stmt.setString(4, reason);
                stmt.setInt(5, studentId);
                stmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            throw new DatabaseException("Credit adjustment transaction failed: " + e.getMessage(), e);
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            db.releaseConnection(conn);
        }
    }

    public List<CreditTransaction> getTransactionHistory(int studentId) throws DatabaseException {
        String sql = "SELECT ct.* FROM credit_transactions ct " +
                     "JOIN credit_wallets cw ON ct.wallet_id = cw.id " +
                     "WHERE cw.student_id = ? ORDER BY ct.created_at DESC";

        List<CreditTransaction> list = new ArrayList<>();
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CreditTransaction ct = new CreditTransaction();
                    ct.setId(rs.getInt("id"));
                    ct.setWalletId(rs.getInt("wallet_id"));
                    ct.setAmount(rs.getBigDecimal("amount"));
                    ct.setTransactionType(CreditTransaction.TransactionType.valueOf(rs.getString("transaction_type")));
                    ct.setReason(rs.getString("reason"));
                    ct.setBalanceAfter(rs.getBigDecimal("balance_after"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) ct.setCreatedAt(ts.toLocalDateTime());
                    list.add(ct);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get credit history: " + e.getMessage(), e);
        } finally {
            db.releaseConnection(conn);
        }
        return list;
    }
}
