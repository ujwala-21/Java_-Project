package com.bookloop.model;

import java.time.LocalDateTime;

public class ExchangeRequest {
    public enum RequestStatus { PENDING, ACCEPTED, REJECTED, CANCELLED, EXPIRED }
    public enum RequestType { BUY, SELL, EXCHANGE, RENT, DONATE }

    private int id;
    private int requesterId;
    private String requesterName;
    private int listingId;
    private String listingTitle;
    private int ownerId;
    private String ownerName;
    private Integer offeredListingId;
    private String offeredListingTitle;
    private String message;
    private RequestStatus status;
    private RequestType requestType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ExchangeRequest() {
        this.status = RequestStatus.PENDING;
    }

    public ExchangeRequest(int requesterId, int listingId, RequestType requestType) {
        this.requesterId = requesterId;
        this.listingId = listingId;
        this.requestType = requestType;
        this.status = RequestStatus.PENDING;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRequesterId() { return requesterId; }
    public void setRequesterId(int requesterId) { this.requesterId = requesterId; }

    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }

    public int getListingId() { return listingId; }
    public void setListingId(int listingId) { this.listingId = listingId; }

    public String getListingTitle() { return listingTitle; }
    public void setListingTitle(String listingTitle) { this.listingTitle = listingTitle; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public Integer getOfferedListingId() { return offeredListingId; }
    public void setOfferedListingId(Integer offeredListingId) { this.offeredListingId = offeredListingId; }

    public String getOfferedListingTitle() { return offeredListingTitle; }
    public void setOfferedListingTitle(String offeredListingTitle) { this.offeredListingTitle = offeredListingTitle; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public RequestType getRequestType() { return requestType; }
    public void setRequestType(RequestType requestType) { this.requestType = requestType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
