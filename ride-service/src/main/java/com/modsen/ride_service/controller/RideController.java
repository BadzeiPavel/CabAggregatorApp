package com.modsen.ride_service.controller;

import com.modsen.ride_service.enums.RideStatus;
import com.modsen.ride_service.models.dtos.RideDTO;
import com.modsen.ride_service.models.dtos.RidePatchDTO;
import com.modsen.ride_service.services.RideService;
import enums.CarCategory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import models.dtos.responses.GetAllPaginatedResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService service;

    @PostMapping
    public ResponseEntity<RideDTO> createRide(@Valid @RequestBody RideDTO rideDTO) {
        RideDTO createdRideDTO = service.createRide(rideDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRideDTO);
    }

    @PutMapping("/{rideId}/accept")
    public ResponseEntity<RideDTO> approveDriverRideResponse(
            @PathVariable UUID rideId,
            @RequestParam("driver-id") UUID driverId
    ) {
        RideDTO rideDTO = service.approveDriverRequestByRideIdAndDriverId(rideId, driverId);
        return ResponseEntity.ok(rideDTO);
    }

    @PutMapping("/{rideId}/reject")
    public ResponseEntity<RideDTO> rejectDriverRideResponse(
            @PathVariable UUID rideId,
            @RequestParam("driver-id") UUID driverId
    ) {
        RideDTO rideDTO = service.rejectDriverRequestByRideId(rideId, driverId);
        return ResponseEntity.ok(rideDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideDTO> getRide(@PathVariable UUID id) {
        RideDTO rideDTO = service.getRideById(id);
        return ResponseEntity.ok(rideDTO);
    }

    @GetMapping("/passengers/{passengerId}")
    public ResponseEntity<GetAllPaginatedResponse<RideDTO>> getPaginatedRidesByPassengerId(
            @PathVariable UUID passengerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GetAllPaginatedResponse<RideDTO> responseDTO =
                service.getPaginatedRidesByPassengerId(passengerId, PageRequest.of(page, size));
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/passengers/{passengerId}/date-range")
    public ResponseEntity<GetAllPaginatedResponse<RideDTO>> getPaginatedPassengerRidesInDateRange(
            @PathVariable UUID passengerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GetAllPaginatedResponse<RideDTO> responseDTO =
                service.getPaginatedPassengerRidesInDateRange(passengerId, from, to, PageRequest.of(page, size));
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/drivers/{driverId}")
    public ResponseEntity<GetAllPaginatedResponse<RideDTO>> getPaginatedRidesByDriverId(
            @PathVariable UUID driverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GetAllPaginatedResponse<RideDTO> responseDTO =
                service.getPaginatedRidesByDriverId(driverId, PageRequest.of(page, size));
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/drivers/{driverId}/date-range")
    public ResponseEntity<GetAllPaginatedResponse<RideDTO>> getPaginatedDriverRidesInDateRange(
            @PathVariable UUID driverId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GetAllPaginatedResponse<RideDTO> responseDTO =
                service.getPaginatedDriverRidesInDateRange(driverId, from, to, PageRequest.of(page, size));
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RideDTO> updateRide(@PathVariable UUID id, @Valid @RequestBody RideDTO rideDTO) {
        RideDTO updatedRideDTO = service.updateRide(id, rideDTO);
        return ResponseEntity.ok(updatedRideDTO);
    }

    @PutMapping("/{id}/status/in-ride")
    public ResponseEntity<RideDTO> changeRideStatusOnInRide(@PathVariable UUID id) {
        RideDTO rideDTO = service.changeRideStatus(id, RideStatus.IN_RIDE);
        return ResponseEntity.ok(rideDTO);
    }

    @PutMapping("/{id}/status/completed")
    public ResponseEntity<RideDTO> changeRideStatusOnCompleted(@PathVariable UUID id) {
        RideDTO rideDTO = service.changeRideStatus(id, RideStatus.COMPLETED);
        return ResponseEntity.ok(rideDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RideDTO> patchRide(@PathVariable UUID id, @Valid @RequestBody RidePatchDTO ridePatchDTO) {
        RideDTO updatedRideDTO = service.patchRide(id, ridePatchDTO);
        return ResponseEntity.ok(updatedRideDTO);
    }
}
