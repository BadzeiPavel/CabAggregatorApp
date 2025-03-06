package com.modsen.ride_service.exceptions;

public class ErrorServiceResponseException extends RuntimeException {
    public ErrorServiceResponseException(String message) {
        super(message);
    }
}
