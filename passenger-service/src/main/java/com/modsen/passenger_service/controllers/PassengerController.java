package com.modsen.passenger_service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import models.dtos.PassengerDTO;
import com.modsen.passenger_service.services.PassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import models.dtos.UserPatchDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Passenger Controller", description = "CRUD API for passenger")
@RestController
@RequestMapping("/api/v1/passengers")
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService service;

    @Operation(summary = "Create passenger")
    @PostMapping
    public ResponseEntity<PassengerDTO> createPassenger(@Valid @RequestBody PassengerDTO passengerDTO) {
        PassengerDTO createdPassengerDTO = service.createPassenger(passengerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPassengerDTO);
    }

    @Operation(summary = "Get passenger by id")
    @GetMapping("/{id}")
    public ResponseEntity<PassengerDTO> getPassenger(@PathVariable UUID id) {
        PassengerDTO passengerDTO = service.getPassenger(id);
        return ResponseEntity.ok(passengerDTO);
    }

    @Operation(summary = "Update passenger by id")
    @PutMapping("/{id}")
    public ResponseEntity<PassengerDTO> updatePassenger(
            @PathVariable UUID id,
            @Valid @RequestBody PassengerDTO passengerDTO
    ) {
        PassengerDTO updatedPassengerDTO = service.updatePassenger(id, passengerDTO);
        return ResponseEntity.ok(updatedPassengerDTO);
    }

    @Operation(summary = "Patch passenger by id")
    @PatchMapping("/{id}")
    public ResponseEntity<PassengerDTO> patchPassenger(
            @PathVariable UUID id,
            @Valid @RequestBody UserPatchDTO userPatchDTO
    ) {
        PassengerDTO passengerDTO = service.patchPassenger(id, userPatchDTO);
        return ResponseEntity.ok(passengerDTO);
    }

    @Operation(summary = "Soft delete passenger by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<PassengerDTO> softDeletePassenger(@PathVariable UUID id) {
        PassengerDTO passengerDTO = service.softDeletePassenger(id);
        return ResponseEntity.ok(passengerDTO);
    }
}
