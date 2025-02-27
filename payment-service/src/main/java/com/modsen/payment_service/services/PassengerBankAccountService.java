package com.modsen.payment_service.services;

import com.modsen.payment_service.exceptions.InvalidAmountValueException;
import com.modsen.payment_service.exceptions.RecordNotFoundException;
import com.modsen.payment_service.mappers.DtoMapper;
import com.modsen.payment_service.mappers.EntityMapper;
import com.modsen.payment_service.models.dtos.PassengerBankAccountDTO;
import com.modsen.payment_service.models.enitties.PassengerBankAccount;
import com.modsen.payment_service.repositories.PassengerBankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PassengerBankAccountService {

    private final DtoMapper dtoMapper;
    private final EntityMapper entityMapper;
    private final PassengerBankAccountRepository repository;

    public PassengerBankAccountDTO createBankAccount(PassengerBankAccountDTO bankAccountDTO) {
        bankAccountDTO.setBalance(BigDecimal.ZERO);
        PassengerBankAccount bankAccount = dtoMapper.toPassengerBankAccount(bankAccountDTO);
        return entityMapper.toPassengerBankAccountDTO(repository.save(bankAccount));
    }

    public BigDecimal getBalance(String passengerId) {
        PassengerBankAccount bankAccount = repository.findByPassengerId(passengerId)
                .orElseThrow(() -> new RecordNotFoundException("Passenger bank account with passenger_id='%s' not found"
                        .formatted(passengerId)));
        return bankAccount.getBalance();
    }

    public PassengerBankAccountDTO getBankAccount(String passengerId) {
        PassengerBankAccount bankAccount = repository.findByPassengerId(passengerId)
                .orElseThrow(() -> new RecordNotFoundException("Passenger bank account with passenger_id='%s' not found"
                        .formatted(passengerId)));
        return entityMapper.toPassengerBankAccountDTO(bankAccount);
    }

    @Transactional
    public PassengerBankAccountDTO topUpBalance(String passengerId, BigDecimal depositAmount) {
        if (depositAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountValueException("Top up amount must be greater than zero");
        }

        PassengerBankAccount bankAccount = repository.findByPassengerId(passengerId)
                .orElseThrow(() -> new RecordNotFoundException("Passenger bank account with passenger_id='%s' not found"
                        .formatted(passengerId)));
        bankAccount.setBalance(bankAccount.getBalance().add(depositAmount));

        return entityMapper.toPassengerBankAccountDTO(repository.save(bankAccount));
    }

    @Transactional
    public PassengerBankAccountDTO deductBalance(String passengerId, BigDecimal deductAmount) {
        if (deductAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountValueException("Top up amount must be greater than zero");
        }

        PassengerBankAccount bankAccount = repository.findByPassengerId(passengerId)
                .orElseThrow(() -> new RecordNotFoundException("Passenger bank account with passenger_id='%s' not found"
                        .formatted(passengerId)));
        bankAccount.setBalance(bankAccount.getBalance().subtract(deductAmount));

        return entityMapper.toPassengerBankAccountDTO(repository.save(bankAccount));
    }

}
