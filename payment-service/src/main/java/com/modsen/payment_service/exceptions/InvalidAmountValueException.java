package com.modsen.payment_service.exceptions;

public class InvalidAmountValueException extends RuntimeException {
    public InvalidAmountValueException(String message) {
        super(message);
    }
}
