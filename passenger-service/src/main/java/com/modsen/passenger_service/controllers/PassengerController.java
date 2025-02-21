package com.modsen.passenger_service.controllers;

import com.modsen.passenger_service.models.dtos.PassengerDTO;
import com.modsen.passenger_service.services.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/passengers")
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService passengerService;

    @PostMapping
    public ResponseEntity<PassengerDTO> updatePassenger(@RequestBody PassengerDTO passengerDTO) {
        PassengerDTO savedPassengerDTO = passengerService.savePassenger(passengerDTO);
        return ResponseEntity.ok(savedPassengerDTO);
    }

}
