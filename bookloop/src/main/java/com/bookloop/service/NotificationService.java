package com.bookloop.service;

import com.bookloop.dao.NotificationDAO;
import com.bookloop.exception.DatabaseException;
import com.bookloop.model.Notification;

import java.util.List;

public class NotificationService {
    private final NotificationDAO notificationDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }

    public List<Notification> getMyNotifications(int userId) throws DatabaseException {
        return notificationDAO.getNotificationsForUser(userId);
    }

    public int getUnreadCount(int userId) throws DatabaseException {
        return notificationDAO.getUnreadCount(userId);
    }

    public boolean markAllAsRead(int userId) throws DatabaseException {
        return notificationDAO.markAllAsRead(userId);
    }

    public int createNotification(int userId, String title, String message, String type) throws DatabaseException {
        Notification n = new Notification(userId, title, message, type);
        return notificationDAO.createNotification(n);
    }
}
