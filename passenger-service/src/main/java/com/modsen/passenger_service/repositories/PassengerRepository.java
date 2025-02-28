package com.modsen.passenger_service.repositories;

import com.modsen.passenger_service.exceptions.PassengerNotFoundException;
import com.modsen.passenger_service.models.entities.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PassengerRepository extends JpaRepository<Passenger, UUID> {

    default Passenger getPassengerById(UUID id) {
        return findById(id)
                .orElseThrow(() -> new PassengerNotFoundException("Passenger with id='%s' not found".formatted(id)));
    }
}
