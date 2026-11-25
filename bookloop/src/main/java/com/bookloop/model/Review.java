package com.bookloop.model;

import java.time.LocalDateTime;

public class Review {
    private int id;
    private int reviewerId;
    private String reviewerName;
    private int reviewedUserId;
    private String reviewedUserName;
    private Integer transactionId;
    private String bookTitle;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public Review() {}

    public Review(int reviewerId, int reviewedUserId, Integer transactionId, int rating, String comment) {
        this.reviewerId = reviewerId;
        this.reviewedUserId = reviewedUserId;
        this.transactionId = transactionId;
        this.rating = rating;
        this.comment = comment;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getReviewerId() { return reviewerId; }
    public void setReviewerId(int reviewerId) { this.reviewerId = reviewerId; }

    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }

    public int getReviewedUserId() { return reviewedUserId; }
    public void setReviewedUserId(int reviewedUserId) { this.reviewedUserId = reviewedUserId; }

    public String getReviewedUserName() { return reviewedUserName; }
    public void setReviewedUserName(String reviewedUserName) { this.reviewedUserName = reviewedUserName; }

    public Integer getTransactionId() { return transactionId; }
    public void setTransactionId(Integer transactionId) { this.transactionId = transactionId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
