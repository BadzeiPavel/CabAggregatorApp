package com.modsen.payment_service.repositories;

import com.modsen.payment_service.models.enitties.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    Optional<Payment> findByRideId(String id);

    Page<Payment> findAllByPassengerId(String passengerId, Pageable pageable);
    Page<Payment> findByPassengerIdAndCreatedAtIsBetween(
            String passengerId,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );
    Page<Payment> findAllByDriverId(String driverId, Pageable pageable);
    Page<Payment> findByDriverIdAndCreatedAtIsBetween(
            String passengerId,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );
}
