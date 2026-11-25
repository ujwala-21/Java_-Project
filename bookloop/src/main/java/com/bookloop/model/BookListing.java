package com.bookloop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BookListing {
    public enum ListingType { SELL, EXCHANGE, RENT, DONATE }
    public enum ConditionType { NEW, EXCELLENT, GOOD, ACCEPTABLE }
    public enum ListingStatus {
        AVAILABLE, RESERVED, REQUESTED, COMPLETED, SOLD, DONATED, RENTED, UNAVAILABLE, CANCELLED
    }

    private int id;
    private int bookId;
    private Book book;
    private int ownerId;
    private String ownerName;
    private ListingType listingType;
    private ConditionType conditionType;
    private BigDecimal originalPrice;
    private BigDecimal askingPrice;
    private BigDecimal creditValue;
    private String description;
    private String conditionDetails;
    private boolean hasHighlights;
    private boolean hasAnnotations;
    private boolean hasMissingPages;
    private boolean hasDamagedCover;
    private boolean hasTornPages;
    private boolean hasWaterDamage;
    private Integer rentalDurationDays;
    private BigDecimal rentalCredits;
    private String imagePath;
    private ListingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BookListing() {}

    public BookListing(int bookId, int ownerId, ListingType listingType, ConditionType conditionType) {
        this.bookId = bookId;
        this.ownerId = ownerId;
        this.listingType = listingType;
        this.conditionType = conditionType;
        this.status = ListingStatus.AVAILABLE;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public ListingType getListingType() { return listingType; }
    public void setListingType(ListingType listingType) { this.listingType = listingType; }
    public ConditionType getConditionType() { return conditionType; }
    public void setConditionType(ConditionType conditionType) { this.conditionType = conditionType; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public BigDecimal getAskingPrice() { return askingPrice; }
    public void setAskingPrice(BigDecimal askingPrice) { this.askingPrice = askingPrice; }
    public BigDecimal getCreditValue() { return creditValue; }
    public void setCreditValue(BigDecimal creditValue) { this.creditValue = creditValue; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getConditionDetails() { return conditionDetails; }
    public void setConditionDetails(String conditionDetails) { this.conditionDetails = conditionDetails; }
    public boolean isHasHighlights() { return hasHighlights; }
    public void setHasHighlights(boolean hasHighlights) { this.hasHighlights = hasHighlights; }
    public boolean isHasAnnotations() { return hasAnnotations; }
    public void setHasAnnotations(boolean hasAnnotations) { this.hasAnnotations = hasAnnotations; }
    public boolean isHasMissingPages() { return hasMissingPages; }
    public void setHasMissingPages(boolean hasMissingPages) { this.hasMissingPages = hasMissingPages; }
    public boolean isHasDamagedCover() { return hasDamagedCover; }
    public void setHasDamagedCover(boolean hasDamagedCover) { this.hasDamagedCover = hasDamagedCover; }
    public boolean isHasTornPages() { return hasTornPages; }
    public void setHasTornPages(boolean hasTornPages) { this.hasTornPages = hasTornPages; }
    public boolean isHasWaterDamage() { return hasWaterDamage; }
    public void setHasWaterDamage(boolean hasWaterDamage) { this.hasWaterDamage = hasWaterDamage; }
    public Integer getRentalDurationDays() { return rentalDurationDays; }
    public void setRentalDurationDays(Integer rentalDurationDays) { this.rentalDurationDays = rentalDurationDays; }
    public BigDecimal getRentalCredits() { return rentalCredits; }
    public void setRentalCredits(BigDecimal rentalCredits) { this.rentalCredits = rentalCredits; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public ListingStatus getStatus() { return status; }
    public void setStatus(ListingStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getBookTitle() { return book != null ? book.getTitle() : ""; }
    public String getBookAuthor() { return book != null ? book.getAuthor() : ""; }

    @Override
    public String toString() {
        return "BookListing{id=" + id + ", bookId=" + bookId + ", type=" + listingType + ", status=" + status + "}";
    }
}
