package com.modsen.passenger_service.services;

import com.modsen.passenger_service.exceptions.PassengerNotFoundException;
import com.modsen.passenger_service.mappers.PassengerDTOMapper;
import com.modsen.passenger_service.mappers.PassengerMapper;
import com.modsen.passenger_service.models.dtos.PassengerDTO;
import com.modsen.passenger_service.models.entities.Passenger;
import com.modsen.passenger_service.repositories.PassengerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PassengerService {

    private final PassengerRepository repository;
    private final PassengerMapper passengerMapper;
    private final PassengerDTOMapper passengerDTOMapper;

    public PassengerDTO savePassenger(PassengerDTO passengerDTO) {
        Passenger passenger = passengerDTOMapper.toPassenger(passengerDTO);
        return passengerMapper.toPassengerDTO(repository.save(passenger));
    }

    public PassengerDTO getPassenger(UUID id) {
        Passenger passenger = repository.findById(id)
                .orElseThrow(() -> new PassengerNotFoundException("Passenger with id='%s' not found".formatted(id)));
        return passengerMapper.toPassengerDTO(passenger);
    }

    public PassengerDTO updatePassenger(UUID id, PassengerDTO passengerDTO) {
        Passenger passenger = repository.findById(id)
                .orElseThrow(() -> new PassengerNotFoundException("Passenger with id='%s' not found".formatted(id)));

        Passenger updatedPassenger = passengerDTOMapper.toPassenger(passengerDTO);
        updatedPassenger.setId(passenger.getId());

        return passengerMapper.toPassengerDTO(repository.save(updatedPassenger));
    }

    public PassengerDTO softDeletePassenger(UUID id) {
        Passenger passenger = repository.findById(id)
                .orElseThrow(() -> new PassengerNotFoundException("Passenger with id='%s' not found".formatted(id)));
        passenger.setDeleted(true);

        return passengerMapper.toPassengerDTO(passenger);
    }

}
