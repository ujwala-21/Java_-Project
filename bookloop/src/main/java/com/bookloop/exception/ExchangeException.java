package com.bookloop.exception;

public class ExchangeException extends BookLoopException {
    public ExchangeException(String message) {
        super(message);
    }

    public ExchangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExchangeException(String userMessage, String technicalMessage, Throwable cause) {
        super(userMessage, technicalMessage, cause);
    }
}
