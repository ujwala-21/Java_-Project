package com.bookloop.exception;

public class BookLoopException extends Exception {
    private final String userMessage;

    public BookLoopException(String message) {
        super(message);
        this.userMessage = message;
    }

    public BookLoopException(String message, Throwable cause) {
        super(message, cause);
        this.userMessage = message;
    }

    public BookLoopException(String userMessage, String technicalMessage, Throwable cause) {
        super(technicalMessage, cause);
        this.userMessage = userMessage;
    }

    public String getUserMessage() { return userMessage; }
}
