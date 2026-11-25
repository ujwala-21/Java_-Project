package com.bookloop.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");

    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DISPLAY_FORMATTER);
    }

    public static String formatWithTime(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(TIME_FORMATTER);
    }

    public static String getRelativeTime(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        Duration duration = Duration.between(dateTime, LocalDateTime.now());
        long seconds = duration.getSeconds();
        if (seconds < 60) return "Just now";
        long minutes = seconds / 60;
        if (minutes < 60) return minutes + "m ago";
        long hours = minutes / 60;
        if (hours < 24) return hours + "h ago";
        long days = hours / 24;
        if (days < 30) return days + "d ago";
        return format(dateTime);
    }

    public static String getRemainingDays(LocalDateTime expiry) {
        if (expiry == null) return "No expiry";
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(expiry)) return "Expired";
        long days = Duration.between(now, expiry).toDays();
        long hours = Duration.between(now, expiry).toHours() % 24;
        if (days == 0) return hours + " hours left";
        return days + " days left";
    }
}
