package com.bookloop.service;

import com.bookloop.dao.ReviewDAO;
import com.bookloop.exception.DatabaseException;
import com.bookloop.exception.ValidationException;
import com.bookloop.model.Review;
import com.bookloop.util.ValidationUtil;

import java.util.List;

public class ReviewService {
    private final ReviewDAO reviewDAO;

    public ReviewService() {
        this.reviewDAO = new ReviewDAO();
    }

    public int submitReview(int reviewerId, int reviewedUserId, Integer transactionId, int rating, String comment)
            throws ValidationException, DatabaseException {

        if (reviewerId == reviewedUserId) {
            throw new ValidationException("You cannot review yourself.");
        }
        if (rating < 1 || rating > 5) {
            throw new ValidationException("Rating must be between 1 and 5 stars.");
        }
        if (transactionId != null && reviewDAO.hasReviewed(reviewerId, transactionId)) {
            throw new ValidationException("You have already submitted a review for this transaction.");
        }

        Review review = new Review();
        review.setReviewerId(reviewerId);
        review.setReviewedUserId(reviewedUserId);
        review.setTransactionId(transactionId);
        review.setRating(rating);
        review.setComment(comment != null ? comment.trim() : "");

        return reviewDAO.createReview(review);
    }

    public List<Review> getUserReviews(int userId) throws DatabaseException {
        return reviewDAO.getReviewsForUser(userId);
    }

    public double getAverageRating(int userId) throws DatabaseException {
        return reviewDAO.getAverageRating(userId);
    }
}
