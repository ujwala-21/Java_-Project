package com.bookloop.model;

import java.time.LocalDateTime;

public class Reservation {
    public enum ReservationStatus {
        ACTIVE, REQUESTER_CONFIRMED, OWNER_CONFIRMED, COMPLETED, CANCELLED, EXPIRED
    }

    private int id;
    private int requestId;
    private int requesterId;
    private String requesterName;
    private int ownerId;
    private String ownerName;
    private int listingId;
    private String bookTitle;
    private String imagePath;
    private ReservationStatus status;
    private LocalDateTime expiresAt;
    private LocalDateTime requesterConfirmedAt;
    private LocalDateTime ownerConfirmedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime createdAt;

    public Reservation() {
        this.status = ReservationStatus.ACTIVE;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public int getRequesterId() { return requesterId; }
    public void setRequesterId(int requesterId) { this.requesterId = requesterId; }

    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public int getListingId() { return listingId; }
    public void setListingId(int listingId) { this.listingId = listingId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public LocalDateTime getRequesterConfirmedAt() { return requesterConfirmedAt; }
    public void setRequesterConfirmedAt(LocalDateTime requesterConfirmedAt) { this.requesterConfirmedAt = requesterConfirmedAt; }

    public LocalDateTime getOwnerConfirmedAt() { return ownerConfirmedAt; }
    public void setOwnerConfirmedAt(LocalDateTime ownerConfirmedAt) { this.ownerConfirmedAt = ownerConfirmedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
}
