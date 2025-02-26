package com.modsen.passenger_service.controllers;

import com.modsen.passenger_service.models.dtos.PassengerDTO;
import com.modsen.passenger_service.services.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/passengers")
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService service;

    @PostMapping
    public ResponseEntity<PassengerDTO> savePassenger(@RequestBody PassengerDTO passengerDTO) {
        PassengerDTO savedPassengerDTO = service.savePassenger(passengerDTO);
        return ResponseEntity.ok(savedPassengerDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerDTO> getPassenger(@PathVariable UUID id) {
        PassengerDTO passengerDTO = service.getPassenger(id);
        return ResponseEntity.ok(passengerDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PassengerDTO> updatePassenger(@PathVariable UUID id, @RequestBody PassengerDTO passengerDTO) {
        PassengerDTO savedPassengerDTO = service.updatePassenger(id, passengerDTO);
        return ResponseEntity.ok(savedPassengerDTO);
    }

    @DeleteMapping("/id")
    public ResponseEntity<PassengerDTO> softDeletePassenger(@PathVariable UUID id) {
        PassengerDTO passengerDTO = service.softDeletePassenger(id);
        return ResponseEntity.ok(passengerDTO);
    }
}
