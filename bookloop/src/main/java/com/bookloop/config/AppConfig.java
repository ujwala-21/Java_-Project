package com.bookloop.config;

public class AppConfig {
    public static final String APP_NAME = "BookLoop";
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_TAGLINE = "Your old books. Someone else's next semester.";

    // Reservation settings
    public static final int RESERVATION_EXPIRY_DAYS = 7;
    public static final int MAX_CYCLE_LENGTH = 4;

    // Image settings
    public static final int MAX_IMAGE_SIZE_MB = 5;
    public static final String IMAGE_STORAGE_DIR = "book_images";

    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;

    // Credit settings
    public static final double MIN_CREDIT_VALUE = 1.0;
    public static final double MAX_CREDIT_VALUE = 500.0;

    // Notification refresh interval (ms)
    public static final int NOTIFICATION_REFRESH_INTERVAL = 30000;
}
