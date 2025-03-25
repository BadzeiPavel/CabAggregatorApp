package com.modsen.payment_service.integration.services;

import com.modsen.payment_service.exceptions.CannotProceedPaymentException;
import com.modsen.payment_service.exceptions.RecordNotFoundException;
import com.modsen.payment_service.models.enitties.DriverBankAccount;
import com.modsen.payment_service.models.enitties.PassengerBankAccount;
import com.modsen.payment_service.models.enitties.Payment;
import com.modsen.payment_service.repositories.DriverBankAccountRepository;
import com.modsen.payment_service.repositories.PassengerBankAccountRepository;
import com.modsen.payment_service.repositories.PaymentRepository;
import com.modsen.payment_service.services.PaymentService;
import constants.KafkaConstants;
import enums.PaymentMethod;
import enums.PaymentStatus;
import models.dtos.PaymentDTO;
import models.dtos.RideInfo;
import models.dtos.events.MakePaymentOnCompleteEvent;
import models.dtos.responses.GetAllPaginatedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PassengerBankAccountRepository passengerBankAccountRepository;

    @Autowired
    private DriverBankAccountRepository driverBankAccountRepository;

    @MockitoBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    private String passengerId;
    private String driverId;
    private String rideId;
    private BigDecimal initialPassengerBalance;
    private BigDecimal initialDriverBalance;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        passengerBankAccountRepository.deleteAll();
        driverBankAccountRepository.deleteAll();

        passengerId = "passenger-123";
        driverId = "driver-456";
        rideId = "ride-789";
        initialPassengerBalance = BigDecimal.valueOf(100);
        initialDriverBalance = BigDecimal.valueOf(50);

        PassengerBankAccount passengerAccount = new PassengerBankAccount();
        passengerAccount.setPassengerId(passengerId);
        passengerAccount.setBalance(initialPassengerBalance);
        passengerBankAccountRepository.save(passengerAccount);

        DriverBankAccount driverAccount = new DriverBankAccount();
        driverAccount.setDriverId(driverId);
        driverAccount.setBalance(initialDriverBalance);
        driverBankAccountRepository.save(driverAccount);
    }

    @Test
    void createPayment_WithSufficientBalance_ShouldCreatePayment() {
        PaymentDTO paymentDTO = PaymentDTO.builder()
                .passengerId(passengerId)
                .driverId(driverId)
                .rideId(rideId)
                .cost(BigDecimal.valueOf(50))
                .build();

        PaymentDTO result = paymentService.createPayment(paymentDTO);

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(paymentRepository.findByRideId(rideId)).isPresent();
    }

    @Test
    void createPayment_WithInsufficientBalance_ShouldThrowException() {
        PaymentDTO paymentDTO = PaymentDTO.builder()
                .passengerId(passengerId)
                .driverId(driverId)
                .rideId(rideId)
                .cost(BigDecimal.valueOf(150))
                .build();

        assertThatThrownBy(() -> paymentService.createPayment(paymentDTO))
                .isInstanceOf(CannotProceedPaymentException.class)
                .hasMessageContaining("Insufficient passenger balance");
    }

    @Test
    void deletePayment_ExistingRideId_ShouldDeletePayment() {
        Payment payment = Payment.builder()
                .rideId(rideId)
                .build();
        paymentRepository.save(payment);

        PaymentDTO result = paymentService.deletePayment(rideId);

        assertThat(result.getRideId()).isEqualTo(rideId);
        assertThat(paymentRepository.findByRideId(rideId)).isEmpty();
    }

    @Test
    void deletePayment_NonExistingRideId_ShouldThrowException() {
        String nonExistingRideId = "non-existing";
        assertThatThrownBy(() -> paymentService.deletePayment(nonExistingRideId))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessageContaining(nonExistingRideId);
    }

    @Test
    void getPayment_ExistingId_ShouldReturnPayment() {
        Payment payment = Payment.builder()
                .rideId(rideId)
                .build();
        paymentRepository.save(payment);
        String paymentId = payment.getId();

        PaymentDTO result = paymentService.getPayment(paymentId);

        assertThat(result.getId()).isEqualTo(paymentId);
    }

    @Test
    void getPayment_NonExistingId_ShouldThrowException() {
        String nonExistingId = "non-existing-id";
        assertThatThrownBy(() -> paymentService.getPayment(nonExistingId))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessageContaining(nonExistingId);
    }

    @Test
    void getPaginatedPaymentsByPassengerId_ShouldReturnPayments() {
        for (int i = 0; i < 5; i++) {
            Payment payment = Payment.builder()
                    .passengerId(passengerId)
                    .rideId("ride-" + i)
                    .build();
            paymentRepository.save(payment);
        }

        PageRequest pageRequest = PageRequest.of(0, 3);
        GetAllPaginatedResponse<PaymentDTO> response = paymentService.getPaginatedPaymentsByPassengerId(passengerId, pageRequest);

        assertThat(response.getTotalElements()).isEqualTo(5);
        assertThat(response.getTotalPages()).isEqualTo(2);
    }

    @Test
    void makePaymentOnCompletedRide_CardPayment_ShouldUpdateBalancesAndStatus() {
        Payment payment = Payment.builder()
                .rideId(rideId)
                .passengerId(passengerId)
                .driverId(driverId)
                .status(PaymentStatus.PENDING)
                .cost(BigDecimal.valueOf(50))
                .rideInfo(RideInfo.builder()
                        .paymentMethod(PaymentMethod.CARD)
                        .build())
                .build();
        paymentRepository.save(payment);

        PaymentDTO result = paymentService.makePaymentOnCompletedRide(rideId);

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.PAID);
        PassengerBankAccount passengerAccount = passengerBankAccountRepository.findByPassengerId(passengerId).get();
        DriverBankAccount driverAccount = driverBankAccountRepository.findByDriverId(driverId).get();
        assertThat(passengerAccount.getBalance()).isEqualByComparingTo(initialPassengerBalance.subtract(payment.getCost()));
        assertThat(driverAccount.getBalance()).isEqualByComparingTo(initialDriverBalance.add(payment.getCost()));
    }

    @Test
    void makePaymentOnCompletedRide_CashPayment_ShouldNotUpdateBalances() {
        Payment payment = Payment.builder()
                .rideId(rideId)
                .passengerId(passengerId)
                .driverId(driverId)
                .status(PaymentStatus.PENDING)
                .cost(BigDecimal.valueOf(50))
                .rideInfo(RideInfo.builder()
                        .paymentMethod(PaymentMethod.CASH)
                        .build())
                .build();
        paymentRepository.save(payment);

        PaymentDTO result = paymentService.makePaymentOnCompletedRide(rideId);

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.PAID);
        PassengerBankAccount passengerAccount = passengerBankAccountRepository.findByPassengerId(passengerId).get();
        DriverBankAccount driverAccount = driverBankAccountRepository.findByDriverId(driverId).get();
        assertThat(passengerAccount.getBalance()).isEqualByComparingTo(initialPassengerBalance);
        assertThat(driverAccount.getBalance()).isEqualByComparingTo(initialDriverBalance);
    }

    @Test
    void makePaymentOnCompletedRide_AlreadyPaid_ShouldThrowException() {
        Payment payment = Payment.builder()
                .rideId(rideId)
                .status(PaymentStatus.PAID)
                .build();
        paymentRepository.save(payment);

        assertThatThrownBy(() -> paymentService.makePaymentOnCompletedRide(rideId))
                .isInstanceOf(CannotProceedPaymentException.class)
                .hasMessageContaining("already paid");
    }

    @Test
    void makePaymentOnCompletedRide_NonExistingRideId_ShouldSendRecoveryEvent() {
        String nonExistingRideId = "non-existing-ride";

        assertThatThrownBy(() -> paymentService.makePaymentOnCompletedRide(nonExistingRideId))
                .isInstanceOf(RecordNotFoundException.class);

        ArgumentCaptor<MakePaymentOnCompleteEvent> eventCaptor = ArgumentCaptor.forClass(MakePaymentOnCompleteEvent.class);
        verify(kafkaTemplate).send(eq(KafkaConstants.RIDE_PAYMENT_ON_COMPLETE_RECOVERY_EVENT), eventCaptor.capture());
        assertThat(eventCaptor.getValue().getRideId()).isEqualTo(nonExistingRideId);
    }
}