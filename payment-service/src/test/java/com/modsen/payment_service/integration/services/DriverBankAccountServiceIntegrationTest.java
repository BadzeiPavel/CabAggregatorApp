package com.modsen.payment_service.integration.services;

import com.modsen.payment_service.exceptions.RecordNotFoundException;
import com.modsen.payment_service.models.dtos.DriverBankAccountDTO;
import com.modsen.payment_service.models.enitties.DriverBankAccount;
import com.modsen.payment_service.repositories.DriverBankAccountRepository;
import com.modsen.payment_service.services.DriverBankAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class DriverBankAccountServiceIntegrationTest {

    @Autowired
    private DriverBankAccountService service;

    @Autowired
    private DriverBankAccountRepository repository;

    private final String DRIVER_ID = "driver-123";

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        DriverBankAccount account = DriverBankAccount.builder()
                .driverId(DRIVER_ID)
                .balance(BigDecimal.TEN)
                .build();
        repository.save(account);
    }

    @Test
    void createBankAccount_ShouldSetBalanceToZero() {
        DriverBankAccountDTO dto = DriverBankAccountDTO.builder()
                .driverId("new-driver")
                .build();

        DriverBankAccountDTO result = service.createBankAccount(dto);

        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(repository.findByDriverId("new-driver")).isPresent();
    }

    @Test
    void getBankAccount_WhenExists_ShouldReturnAccount() {
        DriverBankAccountDTO result = service.getBankAccount(DRIVER_ID);

        assertThat(result.getDriverId()).isEqualTo(DRIVER_ID);
        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.TEN);
    }

    @Test
    void topUpBalance_ShouldIncreaseBalance() {
        BigDecimal deposit = BigDecimal.valueOf(20);
        DriverBankAccountDTO result = service.topUpBalance(DRIVER_ID, deposit);

        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(30));
        assertThat(repository.findByDriverId(DRIVER_ID).get().getBalance())
                .isEqualByComparingTo(BigDecimal.valueOf(30));
    }

    @Test
    void deductBalance_ShouldDecreaseBalance() {
        BigDecimal deduct = BigDecimal.valueOf(5);
        DriverBankAccountDTO result = service.deductBalance(DRIVER_ID, deduct);

        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(5));
        assertThat(repository.findByDriverId(DRIVER_ID).get().getBalance())
                .isEqualByComparingTo(BigDecimal.valueOf(5));
    }

    @Test
    void deductBalance_NonExistentDriver_ShouldThrow() {
        String invalidId = "invalid-id";
        assertThatThrownBy(() -> service.deductBalance(invalidId, BigDecimal.TEN))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessageContaining(invalidId);
    }
}