package com.modsen.payment_service.unit.services;

import com.modsen.payment_service.exceptions.CannotProceedPaymentException;
import com.modsen.payment_service.exceptions.RecordNotFoundException;
import com.modsen.payment_service.mappers.DtoMapper;
import com.modsen.payment_service.mappers.EntityMapper;
import com.modsen.payment_service.models.enitties.Payment;
import com.modsen.payment_service.repositories.PaymentRepository;
import com.modsen.payment_service.services.DriverBankAccountService;
import com.modsen.payment_service.services.PassengerBankAccountService;
import com.modsen.payment_service.services.PaymentService;
import constants.KafkaConstants;
import enums.PaymentMethod;
import enums.PaymentStatus;
import models.dtos.PaymentDTO;
import models.dtos.RideInfo;
import models.dtos.events.MakePaymentOnCompleteEvent;
import models.dtos.responses.GetAllPaginatedResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository repository;

    @Mock
    private DtoMapper dtoMapper;

    @Mock
    private EntityMapper entityMapper;

    @Mock
    private DriverBankAccountService driverBankAccountService;

    @Mock
    private PassengerBankAccountService passengerBankAccountService;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @InjectMocks
    private PaymentService service;

    @Test
    void createPayment_Successful() {
        // Given
        PaymentDTO dto = new PaymentDTO();
        dto.setCost(BigDecimal.TEN);
        dto.setPassengerId("passenger1");
        
        Payment payment = new Payment();
        payment.setPassengerId("passenger1");
        payment.setCost(BigDecimal.TEN);
        
        when(dtoMapper.toPayment(dto)).thenReturn(payment);
        when(passengerBankAccountService.getBalance("passenger1")).thenReturn(BigDecimal.TEN);
        when(repository.save(payment)).thenReturn(payment);
        when(entityMapper.toPaymentDTO(payment)).thenReturn(dto);

        // When
        PaymentDTO result = service.createPayment(dto);

        // Then
        assertThat(result).isEqualTo(dto);
        verify(repository).save(payment);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getCreatedAt()).isNotNull();
    }

    @Test
    void createPayment_InsufficientBalance_ThrowsException() {
        // Given
        PaymentDTO dto = new PaymentDTO();
        dto.setCost(BigDecimal.TEN);
        dto.setPassengerId("passenger1");
        
        Payment payment = new Payment();
        payment.setPassengerId("passenger1");
        payment.setCost(BigDecimal.TEN);
        
        when(dtoMapper.toPayment(dto)).thenReturn(payment);
        when(passengerBankAccountService.getBalance("passenger1")).thenReturn(BigDecimal.valueOf(9.99));

        // When & Then
        assertThatThrownBy(() -> service.createPayment(dto))
                .isInstanceOf(CannotProceedPaymentException.class)
                .hasMessage("Insufficient passenger balance!");
    }

    @Test
    void deletePayment_Success() {
        // Given
        String rideId = "ride1";
        Payment payment = new Payment();
        PaymentDTO dto = new PaymentDTO();
        
        when(repository.findByRideId(rideId)).thenReturn(Optional.of(payment));
        when(entityMapper.toPaymentDTO(payment)).thenReturn(dto);

        // When
        PaymentDTO result = service.deletePayment(rideId);

        // Then
        assertThat(result).isEqualTo(dto);
        verify(repository).deleteById(payment.getId());
    }

    @Test
    void deletePayment_NotFound_ThrowsException() {
        // Given
        String rideId = "ride1";
        when(repository.findByRideId(rideId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.deletePayment(rideId))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessage("Payment with ride_id='ride1' not found");
    }

    @Test
    void getPayment_Success() {
        // Given
        String id = "payment1";
        Payment payment = new Payment();
        PaymentDTO dto = new PaymentDTO();
        
        when(repository.findById(id)).thenReturn(Optional.of(payment));
        when(entityMapper.toPaymentDTO(payment)).thenReturn(dto);

        // When
        PaymentDTO result = service.getPayment(id);

        // Then
        assertThat(result).isEqualTo(dto);
    }

    @Test
    void getPaginatedPaymentsByPassengerId_ReturnsCorrectResponse() {
        // Given
        String passengerId = "passenger1";
        PageRequest pageRequest = PageRequest.of(0, 10);
        Payment payment = new Payment();
        PaymentDTO dto = new PaymentDTO();
        Page<Payment> page = new PageImpl<>(List.of(payment));
        
        when(repository.findAllByPassengerId(passengerId, pageRequest)).thenReturn(page);
        when(entityMapper.toPaymentDTO(payment)).thenReturn(dto);

        // When
        GetAllPaginatedResponse<PaymentDTO> response = service.getPaginatedPaymentsByPassengerId(passengerId, pageRequest);

        // Then
        assertThat(response.getContent()).containsExactly(dto);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

    @Test
    void makePaymentOnCompletedRide_PaymentNotFound_SendsKafkaEvent() {
        // Given
        String rideId = "ride1";
        when(repository.findByRideId(rideId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.makePaymentOnCompletedRide(rideId))
                .isInstanceOf(RecordNotFoundException.class);
        
        ArgumentCaptor<MakePaymentOnCompleteEvent> eventCaptor = ArgumentCaptor.forClass(MakePaymentOnCompleteEvent.class);
        verify(kafkaTemplate).send(eq(KafkaConstants.RIDE_PAYMENT_ON_COMPLETE_RECOVERY_EVENT), eventCaptor.capture());
        
        assertThat(eventCaptor.getValue().getRideId()).isEqualTo(rideId);
    }

    @Test
    void makePaymentOnCompletedRide_AlreadyPaid_ThrowsException() {
        // Given
        String rideId = "ride1";
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PAID);
        
        when(repository.findByRideId(rideId)).thenReturn(Optional.of(payment));

        // When & Then
        assertThatThrownBy(() -> service.makePaymentOnCompletedRide(rideId))
                .isInstanceOf(CannotProceedPaymentException.class)
                .hasMessage("Ride already paid!");
    }

    @Test
    void makePaymentOnCompletedRide_CashPayment_UpdatesStatusOnly() {
        // Given
        String rideId = "ride1";
        Payment payment = new Payment();
        payment.setRideId(rideId);
        payment.setStatus(PaymentStatus.PENDING);
        RideInfo rideInfo = new RideInfo();
        rideInfo.setPaymentMethod(PaymentMethod.CASH);
        payment.setRideInfo(rideInfo);
        
        when(repository.findByRideId(rideId)).thenReturn(Optional.of(payment));
        when(repository.save(payment)).thenReturn(payment);

        // When
        service.makePaymentOnCompletedRide(rideId);

        // Then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(payment.getPaidAt()).isNotNull();
        verify(driverBankAccountService, never()).topUpBalance(any(), any());
        verify(passengerBankAccountService, never()).deductBalance(any(), any());
    }

    @Test
    void makePaymentOnCompletedRide_NonCashPayment_UpdatesBalances() {
        // Given
        String rideId = "ride1";
        BigDecimal amount = BigDecimal.TEN;
        
        Payment payment = new Payment();
        payment.setRideId(rideId);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCost(amount);
        payment.setPassengerId("passenger1");
        payment.setDriverId("driver1");
        
        RideInfo rideInfo = new RideInfo();
        rideInfo.setPaymentMethod(PaymentMethod.CARD);
        payment.setRideInfo(rideInfo);
        
        when(repository.findByRideId(rideId)).thenReturn(Optional.of(payment));
        when(repository.findByRideId(rideId)).thenReturn(Optional.of(payment)); // For getPaymentCost
        when(repository.save(payment)).thenReturn(payment);

        // When
        service.makePaymentOnCompletedRide(rideId);

        // Then
        verify(passengerBankAccountService).deductBalance("passenger1", amount);
        verify(driverBankAccountService).topUpBalance("driver1", amount);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PAID);
    }
}