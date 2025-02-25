package com.modsen.ride_service.exceptions;

public class DriverIsNotSetUpException extends RuntimeException {
    public DriverIsNotSetUpException(String message) {
        super(message);
    }
}
