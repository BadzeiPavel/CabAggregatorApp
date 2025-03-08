package com.modsen.ride_service.exceptions.handlers;

import com.modsen.ride_service.exceptions.ErrorServiceResponseException;
import com.modsen.ride_service.exceptions.InvalidRideStatusException;
import com.modsen.ride_service.exceptions.RideNotFoundException;
import models.dtos.responses.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                UUID.randomUUID(),  // Unique error ID for tracking
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(InvalidRideStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRideStatusException(InvalidRideStatusException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                UUID.randomUUID(),
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Ride Status",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(RideNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRideNotFoundException(RideNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                UUID.randomUUID(),
                HttpStatus.BAD_REQUEST.value(),
                "Ride Not Found",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ErrorServiceResponseException.class)
    public ResponseEntity<ErrorResponse> handleErrorServiceResponseException(ErrorServiceResponseException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                UUID.randomUUID(),
                HttpStatus.BAD_REQUEST.value(),
                "Error Service Response",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}

