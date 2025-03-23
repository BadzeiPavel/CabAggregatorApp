package com.modsen.passenger_service.services;

import com.modsen.passenger_service.mappers.PassengerDTOMapper;
import com.modsen.passenger_service.mappers.PassengerMapper;
import com.modsen.passenger_service.models.dtos.PassengerDTO;
import com.modsen.passenger_service.models.entities.Passenger;
import com.modsen.passenger_service.repositories.PassengerRepository;
import lombok.RequiredArgsConstructor;
import models.dtos.UserPatchDTO;
import org.springframework.stereotype.Service;
import utils.PatchUtil;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PassengerService {

    private final PassengerRepository repository;
    private final PassengerMapper passengerMapper;
    private final PassengerDTOMapper passengerDTOMapper;

    public PassengerDTO createPassenger(PassengerDTO passengerDTO) {
        Passenger passenger = passengerDTOMapper.toPassenger(passengerDTO);
        fillInPassengerOnCreate(passenger, passengerDTO);

        return passengerMapper.toPassengerDTO(repository.save(passenger));
    }

    public PassengerDTO getPassenger(UUID id) {
        Passenger passenger = repository.getPassengerById(id);
        return passengerMapper.toPassengerDTO(passenger);
    }

    public PassengerDTO updatePassenger(UUID id, PassengerDTO passengerDTO) {
        Passenger passenger = repository.getPassengerById(id);
        fillInPassengerOnUpdate(passenger, passengerDTO);

        return passengerMapper.toPassengerDTO(repository.save(passenger));
    }

    public PassengerDTO patchPassenger(UUID id, UserPatchDTO userPatchDTO) {
        Passenger passenger = repository.getPassengerById(id);
        fillInPassengerOnPatch(passenger, userPatchDTO);

        return passengerMapper.toPassengerDTO(repository.save(passenger));
    }

    public PassengerDTO softDeletePassenger(UUID id) {
        Passenger passenger = repository.getPassengerById(id);
        passenger.setDeleted(true);

        return passengerMapper.toPassengerDTO(passenger);
    }

    private static void fillInPassengerOnCreate(Passenger passenger, PassengerDTO passengerDTO) {
        passenger.setId(passengerDTO.getId());
        passenger.setDeleted(false);
        passenger.setCreatedAt(LocalDateTime.now());
    }

    private static void fillInPassengerOnUpdate(Passenger passenger, PassengerDTO passengerDTO) {
        passenger.setUsername(passengerDTO.getUsername());
        passenger.setFirstName(passengerDTO.getFirstName());
        passenger.setLastName(passengerDTO.getLastName());
        passenger.setEmail(passengerDTO.getEmail());
        passenger.setPhone(passengerDTO.getPhone());
        passenger.setBirthDate(passengerDTO.getBirthDate());
    }

    private static void fillInPassengerOnPatch(Passenger passenger, UserPatchDTO userPatchDTO) {
        PatchUtil.patchIfNotNull(userPatchDTO.getUsername(), passenger::setUsername);
        PatchUtil.patchIfNotNull(userPatchDTO.getFirstName(), passenger::setFirstName);
        PatchUtil.patchIfNotNull(userPatchDTO.getLastName(), passenger::setLastName);
        PatchUtil.patchIfNotNull(userPatchDTO.getEmail(), passenger::setEmail);
        PatchUtil.patchIfNotNull(userPatchDTO.getPhone(), passenger::setPhone);
        PatchUtil.patchIfNotNull(userPatchDTO.getBirthDate(), passenger::setBirthDate);
    }
}
