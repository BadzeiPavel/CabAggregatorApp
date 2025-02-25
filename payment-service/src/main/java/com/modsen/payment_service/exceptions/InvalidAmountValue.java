package com.modsen.payment_service.exceptions;

public class InvalidAmountValue extends RuntimeException {
    public InvalidAmountValue(String message) {
        super(message);
    }
}
