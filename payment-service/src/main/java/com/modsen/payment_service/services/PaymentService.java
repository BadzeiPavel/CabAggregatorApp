package com.modsen.payment_service.services;

import com.modsen.payment_service.enums.PaymentStatus;
import com.modsen.payment_service.exceptions.CannotProceedPayment;
import com.modsen.payment_service.exceptions.RecordNotFound;
import com.modsen.payment_service.mappers.DtoMapper;
import com.modsen.payment_service.mappers.EntityMapper;
import com.modsen.payment_service.models.dtos.PaymentDTO;
import com.modsen.payment_service.models.RideInfo;
import com.modsen.payment_service.models.enitties.Payment;
import com.modsen.payment_service.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
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

        if (!isRideCanBePaid(payment)) {
            throw new CannotProceedPayment("Insufficient passenger balance!");
        }

        return entityMapper.toPaymentDTO(repository.save(payment));
    }

    public PaymentDTO getPayment(String id) {
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new RecordNotFound("Payment with id='%s' not found".formatted(id)));
        return entityMapper.toPaymentDTO(payment);
    }

    public List<PaymentDTO> getPaymentsByPassengerId(String passengerId) {
        return repository.findAllByPassengerId(passengerId).stream()
                .map(entityMapper::toPaymentDTO)
                .toList();
    }

    public List<PaymentDTO> getPaymentsByDriverId(String driverId) {
        return repository.findAllByDriverId(driverId).stream()
                .map(entityMapper::toPaymentDTO)
                .toList();
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
                .orElseThrow(() -> new RecordNotFound("Payment with ride_id='%s' not found".formatted(rideId)))
                .getCost();
    }

    private static void fillInPaymentOnCreation(Payment payment, RideInfo rideInfo) {
        payment.setCost(calculateCost(rideInfo));
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
    private static BigDecimal calculateCost(RideInfo rideInfo) {
        BigDecimal cost = generateRandomBigDecimal();
        BigDecimal discount = BigDecimal.valueOf((1 - getPromoCodeDiscount(rideInfo.getPromoCode())));
        return cost.multiply(discount);
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
        return switch (promoCode) {
            case "QWERTY1",
                 "HELL000",
                 "CAR1000" -> 0.2f;
            case "PROMO00",
                 "SPRING5" -> 0.1f;
            default -> 0;
        };
    }

}
