package com.modsen.payment_service.services;

import com.modsen.payment_service.enums.PaymentStatus;
import com.modsen.payment_service.exceptions.CannotProceedPaymentException;
import com.modsen.payment_service.exceptions.RecordNotFoundException;
import com.modsen.payment_service.mappers.DtoMapper;
import com.modsen.payment_service.mappers.EntityMapper;
import com.modsen.payment_service.models.RideInfo;
import com.modsen.payment_service.models.dtos.PaymentDTO;
import com.modsen.payment_service.models.enitties.Payment;
import com.modsen.payment_service.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import models.dtos.GetAllPaginatedResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final DtoMapper dtoMapper;
    private final EntityMapper entityMapper;
    private final PaymentRepository repository;
    private final DriverBankAccountService driverBankAccountService;
    private final PassengerBankAccountService passengerBankAccountService;

    @Transactional
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        Payment payment = dtoMapper.toPayment(paymentDTO);
        fillInPaymentOnCreation(payment, paymentDTO.getRideInfo());

        if(!isRideCanBePaid(payment)) {
            throw new CannotProceedPaymentException("Insufficient passenger balance!");
        }

        return entityMapper.toPaymentDTO(repository.save(payment));
    }

    public PaymentDTO getPayment(String id) {
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Payment with id='%s' not found".formatted(id)));
        return entityMapper.toPaymentDTO(payment);
    }

    public GetAllPaginatedResponseDTO<PaymentDTO> getPaginatedPaymentsByPassengerId(
            String passengerId,
            PageRequest pageRequest
    ) {
        Page<Payment> paymentPage = repository.findAllByPassengerId(passengerId, pageRequest);

        return getAllPaginatedResponseDTO(paymentPage);
    }

    public GetAllPaginatedResponseDTO<PaymentDTO> getPaginatedPaymentsByPassengerIdInDateRange(
            String passengerId,
            LocalDateTime from,
            LocalDateTime to,
            PageRequest pageRequest
    ) {
        Page<Payment> paymentPage = repository.findByPassengerIdAndCreatedAtIsBetween(passengerId, from, to, pageRequest);

        return getAllPaginatedResponseDTO(paymentPage);
    }

    public GetAllPaginatedResponseDTO<PaymentDTO> getPaginatedPaymentsByDriverId(
            String driverId,
            PageRequest pageRequest
    ) {
        Page<Payment> paymentPage = repository.findAllByDriverId(driverId, pageRequest);

        return getAllPaginatedResponseDTO(paymentPage);
    }

    public GetAllPaginatedResponseDTO<PaymentDTO> getPaginatedPaymentsByDriverIdInDateRange(
            String driverId,
            LocalDateTime from,
            LocalDateTime to,
            PageRequest pageRequest
    ) {
        Page<Payment> paymentPage = repository.findByDriverIdAndCreatedAtIsBetween(driverId, from, to, pageRequest);

        return getAllPaginatedResponseDTO(paymentPage);
    }

    @Transactional
    public PaymentDTO makePaymentOnCompletedRide(String paymentId) {
        Payment payment = dtoMapper.toPayment(getPayment(paymentId));
        fillInPaymentOnClosing(payment);

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

    private GetAllPaginatedResponseDTO<PaymentDTO> getAllPaginatedResponseDTO(Page<Payment> paymentPage) {
        List<PaymentDTO> paymentDTOs = paymentPage.stream()
                .map(entityMapper::toPaymentDTO)
                .toList();

        return new GetAllPaginatedResponseDTO<>(
                paymentDTOs,
                paymentPage.getTotalPages(),
                paymentPage.getTotalElements()
        );
    }

    private static void fillInPaymentOnCreation(Payment payment, RideInfo rideInfo) {
        payment.setCost(calculateCost(payment.getPromoCode(), rideInfo));
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
    }

    private static void fillInPaymentOnClosing(Payment payment) {
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
    }

    /**
     * Assuming we have some service to calculate ride cost
     * using distance between pickup and destination address
     *
     * @param rideInfo DTO that stores promoCode, pickup and destination address as Strings
     */
    private static BigDecimal calculateCost(String promoCode, RideInfo rideInfo) {
        BigDecimal cost = calculateRideDistance(rideInfo);
        BigDecimal discount = BigDecimal.valueOf((1 - getPromoCodeDiscount(promoCode)));
        return cost.multiply(discount);
    }

    private static BigDecimal calculateRideDistance(RideInfo rideInfo) {
        // *calling* service to calculate distance from pick-up to destination address
        return generateRandomBigDecimal();
    }

    private static BigDecimal generateRandomBigDecimal() {
        BigDecimal random = BigDecimal.valueOf(getRandomNumber());
        return random.setScale(2, RoundingMode.HALF_UP);
    }

    private static double getRandomNumber() {
        int min = 5, max = 30;
        return ((Math.random() * (max - min)) + min);
    }

    private static float getPromoCodeDiscount(String promoCode) {
        return switch(promoCode) {
            case "QWERTY1",
                 "HELL000",
                 "CAR1000" -> 0.2f;
            case "PROMO00",
                 "SPRING5" -> 0.1f;
            default -> 0;
        };
    }

}
