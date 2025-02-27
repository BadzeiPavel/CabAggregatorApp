package com.modsen.payment_service.exceptions;

public class CannotProceedPaymentException extends RuntimeException {
    public CannotProceedPaymentException(String message) {
        super(message);
    }
}
