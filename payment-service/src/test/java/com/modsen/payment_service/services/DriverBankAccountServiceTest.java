package com.modsen.payment_service.services;

import com.modsen.payment_service.exceptions.InvalidAmountValueException;
import com.modsen.payment_service.exceptions.RecordNotFoundException;
import com.modsen.payment_service.mappers.DtoMapper;
import com.modsen.payment_service.mappers.EntityMapper;
import com.modsen.payment_service.models.dtos.DriverBankAccountDTO;
import com.modsen.payment_service.models.enitties.DriverBankAccount;
import com.modsen.payment_service.repositories.DriverBankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DriverBankAccountServiceTest {

    @Mock
    private DriverBankAccountRepository repository;
    
    @Mock
    private DtoMapper dtoMapper;
    
    @Mock
    private EntityMapper entityMapper;
    
    @InjectMocks
    private DriverBankAccountService service;

    private final String driverId = "driver-123";
    private DriverBankAccount testAccount;
    private DriverBankAccountDTO testDto;

    @BeforeEach
    void setUp() {
        testAccount = new DriverBankAccount();
        testAccount.setId("account-456");
        testAccount.setDriverId(driverId);
        testAccount.setBalance(BigDecimal.valueOf(100.00));

        testDto = new DriverBankAccountDTO();
        testDto.setId("account-456");
        testDto.setDriverId(driverId);
        testDto.setBalance(BigDecimal.valueOf(100.00));
    }

    @Test
    void createBankAccount_ValidDTO_CreatesWithZeroBalance() {
        // Arrange
        DriverBankAccountDTO inputDto = new DriverBankAccountDTO();
        inputDto.setDriverId(driverId);
        inputDto.setBalance(BigDecimal.TEN);  // Should be ignored
        
        when(dtoMapper.toDriverbankAccount(inputDto)).thenReturn(testAccount);
        when(repository.save(testAccount)).thenReturn(testAccount);
        when(entityMapper.toDriverBankAccountDTO(testAccount)).thenReturn(testDto);

        // Act
        DriverBankAccountDTO result = service.createBankAccount(inputDto);

        // Assert
        assertEquals(BigDecimal.valueOf(100.00), testAccount.getBalance());
        verify(repository).save(testAccount);
        assertEquals(testDto, result);
    }

    @Test
    void getBankAccount_ExistingId_ReturnsDTO() {
        // Arrange
        when(repository.findByDriverId(driverId)).thenReturn(Optional.of(testAccount));
        when(entityMapper.toDriverBankAccountDTO(testAccount)).thenReturn(testDto);

        // Act
        DriverBankAccountDTO result = service.getBankAccount(driverId);

        // Assert
        assertEquals(testDto, result);
        verify(repository).findByDriverId(driverId);
    }

    @Test
    void getBankAccount_NonExistingId_ThrowsException() {
        // Arrange
        when(repository.findByDriverId(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class, () -> 
            service.getBankAccount("invalid-id")
        );
    }

    @Test
    void topUpBalance_ValidAmount_UpdatesBalance() {
        // Arrange
        BigDecimal deposit = BigDecimal.valueOf(50.00);
        BigDecimal expectedBalance = BigDecimal.valueOf(150.00);

        when(repository.findByDriverId(driverId)).thenReturn(Optional.of(testAccount));
        when(repository.save(testAccount)).thenReturn(testAccount);
        when(entityMapper.toDriverBankAccountDTO(testAccount)).thenReturn(testDto);

        // Act
        DriverBankAccountDTO result = service.topUpBalance(driverId, deposit);

        // Assert
        assertEquals(expectedBalance, testAccount.getBalance());
        verify(repository).save(testAccount);
        assertEquals(testDto.getBalance(), result.getBalance());
    }

    @Test
    void topUpBalance_InvalidAmount_ThrowsException() {
        // Arrange
        BigDecimal invalidDeposit = BigDecimal.valueOf(-10.00);

        // Act & Assert
        assertThrows(InvalidAmountValueException.class, () -> 
            service.topUpBalance(driverId, invalidDeposit)
        );
    }

    @Test
    void deductBalance_ValidAmount_UpdatesBalance() {
        // Arrange
        BigDecimal deduction = BigDecimal.valueOf(30.00);
        BigDecimal expectedBalance = BigDecimal.valueOf(70.00);

        when(repository.findByDriverId(driverId)).thenReturn(Optional.of(testAccount));
        when(repository.save(testAccount)).thenReturn(testAccount);
        when(entityMapper.toDriverBankAccountDTO(testAccount)).thenReturn(testDto);

        // Act
        DriverBankAccountDTO result = service.deductBalance(driverId, deduction);

        // Assert
        assertEquals(expectedBalance, testAccount.getBalance());
        verify(repository).save(testAccount);
        assertEquals(testDto.getBalance(), result.getBalance());
    }

    @Test
    void deductBalance_InvalidAmount_ThrowsException() {
        // Arrange
        BigDecimal invalidDeduction = BigDecimal.ZERO;

        // Act & Assert
        assertThrows(InvalidAmountValueException.class, () -> 
            service.deductBalance(driverId, invalidDeduction)
        );
    }

    @Test
    void deductBalance_AccountNotFound_ThrowsException() {
        // Arrange
        when(repository.findByDriverId(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class, () -> 
            service.deductBalance("invalid-id", BigDecimal.TEN)
        );
    }
}