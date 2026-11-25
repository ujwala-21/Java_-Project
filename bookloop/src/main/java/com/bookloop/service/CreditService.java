package com.bookloop.service;

import com.bookloop.dao.CreditDAO;
import com.bookloop.exception.DatabaseException;
import com.bookloop.exception.InsufficientCreditsException;
import com.bookloop.exception.ValidationException;
import com.bookloop.model.CreditTransaction;
import com.bookloop.model.CreditWallet;

import java.math.BigDecimal;
import java.util.List;

public class CreditService {
    private final CreditDAO creditDAO;

    public CreditService() {
        this.creditDAO = new CreditDAO();
    }

    public CreditWallet getWallet(int studentId) throws DatabaseException {
        return creditDAO.getWallet(studentId);
    }

    public BigDecimal getBalance(int studentId) throws DatabaseException {
        return creditDAO.getBalance(studentId);
    }

    public boolean addCredits(int studentId, BigDecimal amount, String reason) throws ValidationException, DatabaseException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Credit amount must be positive.");
        }
        return creditDAO.adjustCredits(studentId, amount, "CREDIT", reason != null ? reason : "Deposit / Reward");
    }

    public boolean hasClaimedDailyBonusToday(int studentId) throws DatabaseException {
        return creditDAO.hasClaimedDailyBonusToday(studentId);
    }

    public boolean claimDailyBonus(int studentId) throws ValidationException, DatabaseException {
        if (creditDAO.hasClaimedDailyBonusToday(studentId)) {
            throw new ValidationException("You have already claimed your daily campus bonus for today! Please come back tomorrow.");
        }
        return creditDAO.adjustCredits(studentId, BigDecimal.valueOf(25.0), "CREDIT", "Campus Daily Participation Reward");
    }

    public boolean spendCredits(int studentId, BigDecimal amount, String reason)
            throws InsufficientCreditsException, ValidationException, DatabaseException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Debit amount must be positive.");
        }
        BigDecimal current = getBalance(studentId);
        if (current.compareTo(amount) < 0) {
            throw new InsufficientCreditsException("Insufficient balance (" + current + " credits). Needed: " + amount);
        }
        return creditDAO.adjustCredits(studentId, amount, "DEBIT", reason != null ? reason : "Redemption / Fee");
    }

    public List<CreditTransaction> getHistory(int studentId) throws DatabaseException {
        return creditDAO.getTransactionHistory(studentId);
    }
}
