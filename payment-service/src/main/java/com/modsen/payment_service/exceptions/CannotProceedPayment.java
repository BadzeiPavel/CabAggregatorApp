package com.modsen.payment_service.exceptions;

public class CannotProceedPayment extends RuntimeException {
    public CannotProceedPayment(String message) {
        super(message);
    }
}
