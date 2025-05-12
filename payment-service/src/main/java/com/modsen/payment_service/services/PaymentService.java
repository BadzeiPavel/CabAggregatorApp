package com.modsen.payment_service.services;

import constants.KafkaConstants;
import enums.PaymentStatus;
import com.modsen.payment_service.exceptions.CannotProceedPaymentException;
import com.modsen.payment_service.exceptions.RecordNotFoundException;
import com.modsen.payment_service.mappers.DtoMapper;
import com.modsen.payment_service.mappers.EntityMapper;
import lombok.extern.slf4j.Slf4j;
import models.dtos.PaymentDTO;
import com.modsen.payment_service.models.enitties.Payment;
import com.modsen.payment_service.repositories.PaymentRepository;
import enums.PaymentMethod;
import lombok.RequiredArgsConstructor;
import models.dtos.events.MakePaymentOnCompleteEvent;
import models.dtos.responses.GetAllPaginatedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final DtoMapper dtoMapper;
    private final EntityMapper entityMapper;
    private final PaymentRepository repository;
    private final DriverBankAccountService driverBankAccountService;
    private final PassengerBankAccountService passengerBankAccountService;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        Payment payment = dtoMapper.toPayment(paymentDTO);
        fillInPaymentOnCreation(payment, paymentDTO);

        if(!isRideCanBePaid(payment)) {
            throw new CannotProceedPaymentException("Insufficient passenger balance!");
        }

        return entityMapper.toPaymentDTO(repository.save(payment));
    }

    @Transactional
    public PaymentDTO deletePayment(String rideId) {
        Payment payment = repository.findByRideId(rideId)
                .orElseThrow(() -> new RecordNotFoundException("Payment with ride_id='%s' not found".formatted(rideId)));

        repository.deleteById(payment.getId());
        return entityMapper.toPaymentDTO(payment);
    }

    public PaymentDTO getPayment(String id) {
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Payment with id='%s' not found".formatted(id)));
        return entityMapper.toPaymentDTO(payment);
    }

    public GetAllPaginatedResponse<PaymentDTO> getPaginatedPaymentsByPassengerId(
            String passengerId,
            PageRequest pageRequest
    ) {
        Page<Payment> paymentPage = repository.findAllByPassengerId(passengerId, pageRequest);

        return getAllPaginatedResponseDTO(paymentPage);
    }

    public GetAllPaginatedResponse<PaymentDTO> getPaginatedPaymentsByPassengerIdInDateRange(
            String passengerId,
            LocalDateTime from,
            LocalDateTime to,
            PageRequest pageRequest
    ) {
        Page<Payment> paymentPage = repository.findByPassengerIdAndCreatedAtIsBetween(passengerId, from, to, pageRequest);

        return getAllPaginatedResponseDTO(paymentPage);
    }

    public GetAllPaginatedResponse<PaymentDTO> getPaginatedPaymentsByDriverId(
            String driverId,
            PageRequest pageRequest
    ) {
        Page<Payment> paymentPage = repository.findAllByDriverId(driverId, pageRequest);

        return getAllPaginatedResponseDTO(paymentPage);
    }

    public GetAllPaginatedResponse<PaymentDTO> getPaginatedPaymentsByDriverIdInDateRange(
            String driverId,
            LocalDateTime from,
            LocalDateTime to,
            PageRequest pageRequest
    ) {
        Page<Payment> paymentPage = repository.findByDriverIdAndCreatedAtIsBetween(driverId, from, to, pageRequest);

        return getAllPaginatedResponseDTO(paymentPage);
    }

    @Transactional
    public PaymentDTO makePaymentOnCompletedRide(String rideId) {
        Payment payment;
        try {
            payment = repository.findByRideId(rideId)
                    .orElseThrow(() -> new RecordNotFoundException("Payment with ride_id='%s' not found".formatted(rideId)));
        } catch(RecordNotFoundException e) {
            log.error("Sending recovery message for ride_id: {}", rideId);
            kafkaTemplate.send(
                    KafkaConstants.RIDE_PAYMENT_ON_COMPLETE_RECOVERY_EVENT,
                    new MakePaymentOnCompleteEvent(rideId)
            );
            throw new RecordNotFoundException("Payment with ride_id='%s' not found".formatted(rideId));
        }

        if(payment.getStatus().equals(PaymentStatus.PAID)) {
            throw new CannotProceedPaymentException("Ride already paid!");
        }

        fillInPaymentOnClosing(payment);

        if(payment.getRideInfo().getPaymentMethod().equals(PaymentMethod.CASH)) {
            return entityMapper.toPaymentDTO(repository.save(payment));
        }

        BigDecimal rideCost = getPaymentCost(payment.getRideId());
        passengerBankAccountService.deductBalance(payment.getPassengerId(), rideCost);
        driverBankAccountService.topUpBalance(payment.getDriverId(), rideCost);

        return entityMapper.toPaymentDTO(repository.save(payment));
    }

    private boolean isRideCanBePaid(Payment payment) {
        BigDecimal passengerBalance = passengerBankAccountService.getBalance(payment.getPassengerId());
        BigDecimal paymentCost = payment.getCost();

        return passengerBalance.compareTo(paymentCost) >= 0;
    }

    private BigDecimal getPaymentCost(String rideId) {
        return repository.findByRideId(rideId)
                .orElseThrow(() -> new RecordNotFoundException("Payment with ride_id='%s' not found".formatted(rideId)))
                .getCost();
    }

    private GetAllPaginatedResponse<PaymentDTO> getAllPaginatedResponseDTO(Page<Payment> paymentPage) {
        List<PaymentDTO> paymentDTOs = paymentPage.stream()
                .map(entityMapper::toPaymentDTO)
                .toList();

        return new GetAllPaginatedResponse<>(
                paymentDTOs,
                paymentPage.getTotalPages(),
                paymentPage.getTotalElements()
        );
    }

    private static void fillInPaymentOnCreation(Payment payment, PaymentDTO paymentDTO) {
        payment.setCost(paymentDTO.getCost());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
    }

    private static void fillInPaymentOnClosing(Payment payment) {
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
    }

    private static BigDecimal generateRandomBigDecimal() {
        BigDecimal random = BigDecimal.valueOf(getRandomNumber());
        return random.setScale(2, RoundingMode.HALF_UP);
    }

    private static double getRandomNumber() {
        int min = 5, max = 30;
        return ((Math.random() * (max - min)) + min);
    }
}
