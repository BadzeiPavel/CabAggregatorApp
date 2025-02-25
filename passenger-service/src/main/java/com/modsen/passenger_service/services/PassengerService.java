package com.modsen.passenger_service.services;

import com.modsen.passenger_service.mappers.PassengerDTOMapper;
import com.modsen.passenger_service.mappers.PassengerMapper;
import com.modsen.passenger_service.models.dtos.PassengerDTO;
import com.modsen.passenger_service.models.entities.Passenger;
import com.modsen.passenger_service.repositories.PassengerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PassengerService {

    private final PassengerMapper passengerMapper;
    private final PassengerDTOMapper passengerDTOMapper;
    private final PassengerRepository passengerRepository;

    public PassengerDTO savePassenger(PassengerDTO passengerDTO) {
        Passenger passenger = passengerDTOMapper.toPassenger(passengerDTO);

        System.out.println(passenger);

        return passengerMapper.toPassengerDTO(passengerRepository.save(passenger));
    }

}
