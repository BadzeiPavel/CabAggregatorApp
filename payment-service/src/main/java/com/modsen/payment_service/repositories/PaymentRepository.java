package com.modsen.payment_service.repositories;

import com.modsen.payment_service.models.enitties.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    Optional<Payment> findByRideId(String id);
    Optional<Payment> findByPassengerId(String passengerId);
    Optional<Payment> findByDriverId(String driverId);

    List<Payment> findByPassengerIdAndCreatedAtIsBetween(String passengerId, LocalDateTime from, LocalDateTime to);
    List<Payment> findByDriverIdAndCreatedAtIsBetween(String passengerId, LocalDateTime from, LocalDateTime to);

    List<Payment> findAllByPassengerId(String passengerId);
    List<Payment> findAllByDriverId(String driverId);
}
