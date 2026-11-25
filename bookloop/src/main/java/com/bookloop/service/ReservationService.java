package com.bookloop.service;

import com.bookloop.dao.ReservationDAO;
import com.bookloop.exception.DatabaseException;
import com.bookloop.exception.ExchangeException;
import com.bookloop.model.Reservation;

import java.util.List;

public class ReservationService {
    private final ReservationDAO reservationDAO;
    private final ExchangeService exchangeService;

    public ReservationService() {
        this.reservationDAO = new ReservationDAO();
        this.exchangeService = new ExchangeService();
    }

    public List<Reservation> getMyReservations(int studentId) throws DatabaseException {
        return reservationDAO.getStudentReservations(studentId);
    }

    public boolean confirmPickup(int reservationId, int studentId) throws ExchangeException, DatabaseException {
        Reservation res = reservationDAO.findById(reservationId);
        if (res == null) throw new ExchangeException("Reservation not found.");

        boolean updated;
        if (res.getRequesterId() == studentId) {
            updated = reservationDAO.confirmByRequester(reservationId);
        } else if (res.getOwnerId() == studentId) {
            updated = reservationDAO.confirmByOwner(reservationId);
        } else {
            throw new ExchangeException("You are not part of this reservation.");
        }

        // Re-check if both confirmed -> auto complete exchange!
        Reservation refreshed = reservationDAO.findById(reservationId);
        if (refreshed.getStatus() == Reservation.ReservationStatus.COMPLETED) {
            exchangeService.completeExchange(reservationId, studentId);
        }

        return updated;
    }

    public boolean cancelReservation(int reservationId, int studentId) throws ExchangeException, DatabaseException {
        Reservation res = reservationDAO.findById(reservationId);
        if (res == null) throw new ExchangeException("Reservation not found.");
        if (res.getRequesterId() != studentId && res.getOwnerId() != studentId) {
            throw new ExchangeException("You are not authorized to cancel this reservation.");
        }
        return reservationDAO.cancelReservation(reservationId);
    }
}
