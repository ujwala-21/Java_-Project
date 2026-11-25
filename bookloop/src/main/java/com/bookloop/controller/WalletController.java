package com.bookloop.controller;

import com.bookloop.exception.DatabaseException;
import com.bookloop.exception.InsufficientCreditsException;
import com.bookloop.exception.ValidationException;
import com.bookloop.model.CreditTransaction;
import com.bookloop.model.CreditWallet;
import com.bookloop.service.CreditService;
import com.bookloop.util.SessionManager;

import java.math.BigDecimal;
import java.util.List;

public class WalletController {
    private final CreditService creditService;

    public WalletController() {
        this.creditService = new CreditService();
    }

    public BigDecimal getMyBalance() throws DatabaseException {
        int studentId = SessionManager.getInstance().getCurrentUserId();
        return creditService.getBalance(studentId);
    }

    public CreditWallet getMyWallet() throws DatabaseException {
        int studentId = SessionManager.getInstance().getCurrentUserId();
        return creditService.getWallet(studentId);
    }

    public List<CreditTransaction> getMyTransactions() throws DatabaseException {
        int studentId = SessionManager.getInstance().getCurrentUserId();
        return creditService.getHistory(studentId);
    }

    public boolean addCredits(BigDecimal amount, String reason) throws ValidationException, DatabaseException {
        int studentId = SessionManager.getInstance().getCurrentUserId();
        return creditService.addCredits(studentId, amount, reason);
    }

    public boolean claimDailyBonus() throws ValidationException, DatabaseException {
        int studentId = SessionManager.getInstance().getCurrentUserId();
        return creditService.claimDailyBonus(studentId);
    }

    public boolean hasClaimedDailyBonusToday() throws DatabaseException {
        int studentId = SessionManager.getInstance().getCurrentUserId();
        return creditService.hasClaimedDailyBonusToday(studentId);
    }

    public boolean spendCredits(BigDecimal amount, String reason)
            throws InsufficientCreditsException, ValidationException, DatabaseException {
        int studentId = SessionManager.getInstance().getCurrentUserId();
        return creditService.spendCredits(studentId, amount, reason);
    }
}
