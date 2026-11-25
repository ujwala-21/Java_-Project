package com.bookloop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreditWallet {
    private int id;
    private int studentId;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CreditWallet() {}

    public CreditWallet(int studentId, BigDecimal balance) {
        this.studentId = studentId;
        this.balance = balance;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "CreditWallet{studentId=" + studentId + ", balance=" + balance + "}";
    }
}
