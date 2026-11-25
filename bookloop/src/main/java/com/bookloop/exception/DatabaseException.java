package com.bookloop.exception;

public class DatabaseException extends BookLoopException {
    public DatabaseException(String message) {
        super("A database error occurred.", message, null);
    }

    public DatabaseException(String message, Throwable cause) {
        super("A database error occurred.", message, cause);
    }
}
