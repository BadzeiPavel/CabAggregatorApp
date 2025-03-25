package com.modsen.payment_service.integration.services;

import com.modsen.payment_service.exceptions.RecordNotFoundException;
import com.modsen.payment_service.models.enitties.PassengerBankAccount;
import com.modsen.payment_service.repositories.PassengerBankAccountRepository;
import com.modsen.payment_service.services.PassengerBankAccountService;
import models.dtos.PassengerBankAccountDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class PassengerBankAccountServiceIntegrationTest {

    @Autowired
    private PassengerBankAccountService service;

    @Autowired
    private PassengerBankAccountRepository repository;

    private final String PASSENGER_ID = "passenger-123";

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        PassengerBankAccount account = PassengerBankAccount.builder()
                .passengerId(PASSENGER_ID)
                .balance(BigDecimal.TEN)
                .build();
        repository.save(account);
    }

    @Test
    void createBankAccount_ShouldSetBalanceToZero() {
        PassengerBankAccountDTO dto = PassengerBankAccountDTO.builder()
                .passengerId("new-passenger")
                .build();

        PassengerBankAccountDTO result = service.createBankAccount(dto);

        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(repository.findByPassengerId("new-passenger")).isPresent();
    }

    @Test
    void getBankAccount_WhenExists_ShouldReturnAccount() {
        PassengerBankAccountDTO result = service.getBankAccount(PASSENGER_ID);

        assertThat(result.getPassengerId()).isEqualTo(PASSENGER_ID);
        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.TEN);
    }

    @Test
    void topUpBalance_ShouldIncreaseBalance() {
        BigDecimal deposit = BigDecimal.valueOf(20);
        PassengerBankAccountDTO result = service.topUpBalance(PASSENGER_ID, deposit);

        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(30));
        assertThat(repository.findByPassengerId(PASSENGER_ID).get().getBalance())
                .isEqualByComparingTo(BigDecimal.valueOf(30));
    }

    @Test
    void deductBalance_ShouldDecreaseBalance() {
        BigDecimal deduct = BigDecimal.valueOf(5);
        PassengerBankAccountDTO result = service.deductBalance(PASSENGER_ID, deduct);

        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(5));
        assertThat(repository.findByPassengerId(PASSENGER_ID).get().getBalance())
                .isEqualByComparingTo(BigDecimal.valueOf(5));
    }

    @Test
    void deductBalance_NonExistentPassenger_ShouldThrow() {
        String invalidId = "invalid-id";
        assertThatThrownBy(() -> service.deductBalance(invalidId, BigDecimal.TEN))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessageContaining(invalidId);
    }
}
