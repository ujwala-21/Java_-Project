package com.bookloop.controller;

import com.bookloop.exception.DatabaseException;
import com.bookloop.exception.ExchangeException;
import com.bookloop.exception.ValidationException;
import com.bookloop.model.ExchangeRequest;
import com.bookloop.model.ExchangeTransaction;
import com.bookloop.model.Reservation;
import com.bookloop.service.ExchangeService;
import com.bookloop.service.ReservationService;
import com.bookloop.service.ReviewService;
import com.bookloop.util.SessionManager;

import java.util.List;

public class ExchangeController {
    private final ExchangeService exchangeService;
    private final ReservationService reservationService;
    private final ReviewService reviewService;

    public ExchangeController() {
        this.exchangeService = new ExchangeService();
        this.reservationService = new ReservationService();
        this.reviewService = new ReviewService();
    }

    public int sendRequest(int listingId, Integer offeredListingId, String message, String requestType)
            throws ExchangeException, DatabaseException {
        int studentId = SessionManager.getInstance().getCurrentUserId();
        return exchangeService.createRequest(studentId, listingId, offeredListingId, message, requestType);
    }

    public Reservation acceptRequest(int requestId) throws ExchangeException, DatabaseException {
        int studentId = SessionManager.getInstance().getCurrentUserId();
        return exchangeService.acceptRequest(requestId, studentId);
    }

    public boolean rejectRequest(int requestId) throws ExchangeException, DatabaseException {
        int studentId = SessionManager.getInstance().getCurrentUserId();
        return exchangeService.rejectRequest(requestId, studentId);
    }

    public boolean cancelRequest(int requestId) throws ExchangeException, DatabaseException {
        int studentId = SessionManager.getInstance().getCurrentUserId();
        return exchangeService.cancelRequest(requestId, studentId);
    }

    public List<ExchangeRequest> getIncomingRequests() throws DatabaseException {
        int studentId = SessionManager.getInstance().getCurrentUserId();
        return exchangeService.getIncomingRequests(studentId);
    }

    public List<ExchangeRequest> getOutgoingRequests() throws DatabaseException {
        int studentId = SessionManager.getInstance().getCurrentUserId();
        return exchangeService.getOutgoingRequests(studentId);
    }

    public List<Reservation> getMyReservations() throws DatabaseException {
        int studentId = SessionManager.getInstance().getCurrentUserId();
        return reservationService.getMyReservations(studentId);
    }

    public boolean confirmPickup(int reservationId) throws ExchangeException, DatabaseException {
        int studentId = SessionManager.getInstance().getCurrentUserId();
        return reservationService.confirmPickup(reservationId, studentId);
    }

    public boolean cancelReservation(int reservationId) throws ExchangeException, DatabaseException {
        int studentId = SessionManager.getInstance().getCurrentUserId();
        return reservationService.cancelReservation(reservationId, studentId);
    }

    public List<ExchangeTransaction> getMyTransactions() throws DatabaseException {
        int studentId = SessionManager.getInstance().getCurrentUserId();
        return exchangeService.getStudentTransactions(studentId);
    }

    public int submitReview(int reviewedUserId, Integer transactionId, int rating, String comment)
            throws ValidationException, DatabaseException {
        int studentId = SessionManager.getInstance().getCurrentUserId();
        return reviewService.submitReview(studentId, reviewedUserId, transactionId, rating, comment);
    }
}
