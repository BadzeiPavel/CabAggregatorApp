package com.modsen.payment_service.controllers;

import com.modsen.payment_service.models.dtos.ChangeBalanceRequestDTO;
import com.modsen.payment_service.models.dtos.PassengerBankAccountDTO;
import com.modsen.payment_service.services.PassengerBankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bank-accounts/passengers")
@RequiredArgsConstructor
public class PassengerBankAccountController {

    private final PassengerBankAccountService service;

    @PostMapping
    public ResponseEntity<PassengerBankAccountDTO> createBankAccount(@RequestBody PassengerBankAccountDTO bankAccountDTO) {
        return ResponseEntity.ok(service.createBankAccount(bankAccountDTO));
    }

    @GetMapping("{id}")
    public ResponseEntity<PassengerBankAccountDTO> getBalance(@PathVariable String id) {
        return ResponseEntity.ok(service.getBankAccount(id));
    }

    @PutMapping("{id}/top-up")
    public ResponseEntity<PassengerBankAccountDTO> topUpBalance(@PathVariable String id,
                                                                @RequestBody ChangeBalanceRequestDTO changeBalanceRequestDTO) {
        return ResponseEntity.ok(service.topUpBalance(id, changeBalanceRequestDTO.getAmount()));
    }

    @PutMapping("{id}/deduct")
    public ResponseEntity<PassengerBankAccountDTO> deductBalance(@PathVariable String id,
                                                                 @RequestBody ChangeBalanceRequestDTO changeBalanceRequestDTO) {
        return ResponseEntity.ok(service.deductBalance(id, changeBalanceRequestDTO.getAmount()));
    }
}
