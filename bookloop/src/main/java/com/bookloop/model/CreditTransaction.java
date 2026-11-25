package com.bookloop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreditTransaction {
    public enum TransactionType { CREDIT, DEBIT }

    private int id;
    private int walletId;
    private BigDecimal amount;
    private TransactionType transactionType;
    private String reason;
    private Integer referenceId;
    private String referenceType;
    private BigDecimal balanceAfter;
    private LocalDateTime createdAt;

    public CreditTransaction() {}

    public CreditTransaction(int walletId, BigDecimal amount, TransactionType transactionType, String reason) {
        this.walletId = walletId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.reason = reason;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getWalletId() { return walletId; }
    public void setWalletId(int walletId) { this.walletId = walletId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Integer getReferenceId() { return referenceId; }
    public void setReferenceId(Integer referenceId) { this.referenceId = referenceId; }
    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "CreditTransaction{id=" + id + ", type=" + transactionType + ", amount=" + amount + "}";
    }
}
