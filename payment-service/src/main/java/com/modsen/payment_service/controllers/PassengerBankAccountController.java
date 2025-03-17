package com.modsen.payment_service.controllers;

import com.modsen.payment_service.models.dtos.ChangeBalanceRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import models.dtos.PassengerBankAccountDTO;
import com.modsen.payment_service.services.PassengerBankAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Passenger Bank Account Controller", description = "CRUD API for passenger bank account")
@RestController
@RequestMapping("/api/v1/bank-accounts/passengers")
@RequiredArgsConstructor
public class PassengerBankAccountController {

    private final PassengerBankAccountService service;

    @Operation(summary = "Create passenger bank account")
    @PostMapping
    public ResponseEntity<PassengerBankAccountDTO> createBankAccount(
            @Valid @RequestBody PassengerBankAccountDTO bankAccountDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createBankAccount(bankAccountDTO));
    }

    @Operation(summary = "Get passenger bank account balance by id")
    @GetMapping("{id}")
    public ResponseEntity<PassengerBankAccountDTO> getBalance(@PathVariable String id) {
        return ResponseEntity.ok(service.getBankAccount(id));
    }

    @Operation(summary = "Top-up passenger bank account balance by id")
    @PutMapping("{id}/top-up")
    public ResponseEntity<PassengerBankAccountDTO> topUpBalance(
            @PathVariable String id,
            @Valid @RequestBody ChangeBalanceRequestDTO changeBalanceRequestDTO
    ) {
        return ResponseEntity.ok(service.topUpBalance(id, changeBalanceRequestDTO.getAmount()));
    }

    @Operation(summary = "Deduct passenger bank account balance by id")
    @PutMapping("{id}/deduct")
    public ResponseEntity<PassengerBankAccountDTO> deductBalance(
            @PathVariable String id,
            @Valid @RequestBody ChangeBalanceRequestDTO changeBalanceRequestDTO
    ) {
        return ResponseEntity.ok(service.deductBalance(id, changeBalanceRequestDTO.getAmount()));
    }
}
