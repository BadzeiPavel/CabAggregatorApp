package com.modsen.payment_service.services;

import com.modsen.payment_service.exceptions.InvalidAmountValueException;
import com.modsen.payment_service.exceptions.RecordNotFoundException;
import com.modsen.payment_service.mappers.DtoMapper;
import com.modsen.payment_service.mappers.EntityMapper;
import com.modsen.payment_service.models.dtos.DriverBankAccountDTO;
import com.modsen.payment_service.models.enitties.DriverBankAccount;
import com.modsen.payment_service.repositories.DriverBankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DriverBankAccountService {

    private final DtoMapper dtoMapper;
    private final EntityMapper entityMapper;
    private final DriverBankAccountRepository repository;

    public DriverBankAccountDTO createBankAccount(DriverBankAccountDTO bankAccountDTO) {
        bankAccountDTO.setBalance(BigDecimal.ZERO);
        DriverBankAccount bankAccount = dtoMapper.toDriverbankAccount(bankAccountDTO);
        return entityMapper.toDriverBankAccountDTO(repository.save(bankAccount));
    }

    public DriverBankAccountDTO getBankAccount(String driverId) {
        DriverBankAccount bankAccount = repository.findByDriverId(driverId)
                .orElseThrow(() -> new RecordNotFoundException("Driver bank account with id='%s' not found".formatted(driverId)));
        return entityMapper.toDriverBankAccountDTO(bankAccount);
    }

    @Transactional
    public DriverBankAccountDTO topUpBalance(String driverId, BigDecimal depositAmount) {
        if (depositAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountValueException("Top up amount must be greater than zero");
        }

        DriverBankAccount bankAccount = repository.findByDriverId(driverId)
                .orElseThrow(() -> new RecordNotFoundException("Driver bank account with id='%s' not found".formatted(driverId)));
        bankAccount.setBalance(bankAccount.getBalance().add(depositAmount));

        return entityMapper.toDriverBankAccountDTO(repository.save(bankAccount));
    }

    @Transactional
    public DriverBankAccountDTO deductBalance(String driverId, BigDecimal deductAmount) {
        if (deductAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountValueException("Top up amount must be greater than zero");
        }

        DriverBankAccount bankAccount = repository.findByDriverId(driverId)
                .orElseThrow(() -> new RecordNotFoundException("Driver bank account with id='%s' not found".formatted(driverId)));
        bankAccount.setBalance(bankAccount.getBalance().subtract(deductAmount));

        return entityMapper.toDriverBankAccountDTO(repository.save(bankAccount));
    }
}
