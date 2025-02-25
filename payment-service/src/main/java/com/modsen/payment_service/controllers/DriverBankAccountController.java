package com.modsen.payment_service.controllers;

import com.modsen.payment_service.models.dtos.ChangeBalanceRequestDTO;
import com.modsen.payment_service.models.dtos.DriverBankAccountDTO;
import com.modsen.payment_service.services.DriverBankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bank-accounts/drivers")
@RequiredArgsConstructor
public class DriverBankAccountController {

    private final DriverBankAccountService service;

    @PostMapping
    public ResponseEntity<DriverBankAccountDTO> createBankAccount(@RequestBody DriverBankAccountDTO bankAccountDTO) {
        return ResponseEntity.ok(service.createBankAccount(bankAccountDTO));
    }

    @GetMapping("{id}")
    public ResponseEntity<DriverBankAccountDTO> getBalance(@PathVariable String id) {
        return ResponseEntity.ok(service.getBankAccount(id));
    }

    @PutMapping("{id}/top-up")
    public ResponseEntity<DriverBankAccountDTO> topUpBalance(@PathVariable String id,
                                                             @RequestBody ChangeBalanceRequestDTO changeBalanceRequestDTO) {
        return ResponseEntity.ok(service.topUpBalance(id, changeBalanceRequestDTO.getAmount()));
    }

    @PutMapping("{id}/deduct")
    public ResponseEntity<DriverBankAccountDTO> deductBalance(@PathVariable String id,
                                                              @RequestBody ChangeBalanceRequestDTO changeBalanceRequestDTO) {
        return ResponseEntity.ok(service.deductBalance(id, changeBalanceRequestDTO.getAmount()));
    }
}
