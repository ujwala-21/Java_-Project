package com.bookloop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExchangeTransaction {
    public enum TransactionType { SELL, EXCHANGE, RENT, DONATE, CYCLE_EXCHANGE }
    public enum TransactionStatus { PENDING, COMPLETED, CANCELLED, OVERDUE }

    private int id;
    private Integer reservationId;
    private int giverId;
    private String giverName;
    private int receiverId;
    private String receiverName;
    private int listingId;
    private String bookTitle;
    private TransactionType transactionType;
    private BigDecimal creditsTransferred;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private boolean isReturned;
    private boolean isLate;
    private TransactionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public ExchangeTransaction() {
        this.status = TransactionStatus.PENDING;
        this.creditsTransferred = BigDecimal.ZERO;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getReservationId() { return reservationId; }
    public void setReservationId(Integer reservationId) { this.reservationId = reservationId; }

    public int getGiverId() { return giverId; }
    public void setGiverId(int giverId) { this.giverId = giverId; }

    public String getGiverName() { return giverName; }
    public void setGiverName(String giverName) { this.giverName = giverName; }

    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public int getListingId() { return listingId; }
    public void setListingId(int listingId) { this.listingId = listingId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }

    public BigDecimal getCreditsTransferred() { return creditsTransferred; }
    public void setCreditsTransferred(BigDecimal creditsTransferred) { this.creditsTransferred = creditsTransferred; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDateTime returnDate) { this.returnDate = returnDate; }

    public boolean isReturned() { return isReturned; }
    public void setReturned(boolean returned) { isReturned = returned; }

    public boolean isLate() { return isLate; }
    public void setLate(boolean late) { isLate = late; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
