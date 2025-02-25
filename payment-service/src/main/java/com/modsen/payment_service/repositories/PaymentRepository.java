package com.modsen.payment_service.repositories;

import com.modsen.payment_service.models.enitties.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    Optional<Payment> findByRideId(String id);
    Optional<Payment> findByPassengerId(String passengerId);
    Optional<Payment> findByDriverId(String driverId);

    List<Payment> findAllByPassengerId(String passengerId);
    List<Payment> findAllByDriverId(String driverId);
}
