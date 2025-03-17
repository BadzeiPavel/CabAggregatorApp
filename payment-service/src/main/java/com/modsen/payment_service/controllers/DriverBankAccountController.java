package com.modsen.payment_service.controllers;

import com.modsen.payment_service.models.dtos.ChangeBalanceRequestDTO;
import com.modsen.payment_service.models.dtos.DriverBankAccountDTO;
import com.modsen.payment_service.services.DriverBankAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Driver Bank Account Controller", description = "CRUD API for driver bank account")
@RestController
@RequestMapping("/api/v1/bank-accounts/drivers")
@RequiredArgsConstructor
public class DriverBankAccountController {

    private final DriverBankAccountService service;

    @Operation(summary = "Create driver bank account")
    @PostMapping
    public ResponseEntity<DriverBankAccountDTO> createBankAccount(
            @Valid @RequestBody DriverBankAccountDTO bankAccountDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createBankAccount(bankAccountDTO));
    }

    @Operation(summary = "Get driver bank account balance by id")
    @GetMapping("{id}")
    public ResponseEntity<DriverBankAccountDTO> getBalance(@PathVariable String id) {
        return ResponseEntity.ok(service.getBankAccount(id));
    }

    @Operation(summary = "Top-up driver bank account balance by id")
    @PutMapping("{id}/top-up")
    public ResponseEntity<DriverBankAccountDTO> topUpBalance(
            @PathVariable String id,
            @Valid @RequestBody ChangeBalanceRequestDTO changeBalanceRequestDTO
    ) {
        return ResponseEntity.ok(service.topUpBalance(id, changeBalanceRequestDTO.getAmount()));
    }

    @Operation(summary = "Deduct driver bank account balance by id")
    @PutMapping("{id}/deduct")
    public ResponseEntity<DriverBankAccountDTO> deductBalance(
            @PathVariable String id,
            @Valid @RequestBody ChangeBalanceRequestDTO changeBalanceRequestDTO
    ) {
        return ResponseEntity.ok(service.deductBalance(id, changeBalanceRequestDTO.getAmount()));
    }
}
