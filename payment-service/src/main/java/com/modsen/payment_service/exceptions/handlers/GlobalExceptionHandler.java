package com.modsen.payment_service.exceptions.handlers;

import com.modsen.payment_service.exceptions.CannotProceedPaymentException;
import com.modsen.payment_service.exceptions.InvalidAmountValueException;
import com.modsen.payment_service.exceptions.RecordNotFoundException;
import models.dtos.ErrorResponse;
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

    @ExceptionHandler(CannotProceedPaymentException.class)
    public ResponseEntity<ErrorResponse> handleCannotProceedPayment(CannotProceedPaymentException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                UUID.randomUUID(),
                HttpStatus.BAD_REQUEST.value(),
                "Cannot Proceed Payment",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InvalidAmountValueException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAmountValue(InvalidAmountValueException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                UUID.randomUUID(),
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Amount Value",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRecordNotFound(RecordNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                UUID.randomUUID(),
                HttpStatus.BAD_REQUEST.value(),
                "Record Not Found",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}

