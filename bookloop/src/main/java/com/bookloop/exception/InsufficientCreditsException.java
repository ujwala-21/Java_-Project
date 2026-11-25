package com.bookloop.exception;

public class InsufficientCreditsException extends BookLoopException {
    private double required;
    private double available;

    public InsufficientCreditsException(String message) {
        super(message);
    }

    public InsufficientCreditsException(double required, double available) {
        super(String.format("Insufficient credits. Required: %.2f, Available: %.2f", required, available));
        this.required = required;
        this.available = available;
    }

    public double getRequired() { return required; }
    public double getAvailable() { return available; }
}
