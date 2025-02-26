package com.modsen.ride_service.controller;

import com.modsen.ride_service.enums.RideStatus;
import com.modsen.ride_service.exceptions.InvalidRideStatusException;
import com.modsen.ride_service.models.dtos.ChangeRideStatusRequestDTO;
import com.modsen.ride_service.models.dtos.RideDTO;
import com.modsen.ride_service.services.RideService;
import lombok.RequiredArgsConstructor;
import models.dtos.DateRangeDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService service;

    @PostMapping
    public ResponseEntity<RideDTO> saveRide(@RequestBody RideDTO rideDTO) {
        RideDTO savedRideDTO = service.saveRide(rideDTO);
        return ResponseEntity.ok(savedRideDTO);
    }

    // URL example: /rides/123456/accept?driver-id=654321
    @PostMapping("/{rideId}/accept")
    public ResponseEntity<RideDTO> approveDriverRideResponse(@PathVariable UUID rideId,
                                                             @RequestParam("driver-id") UUID driverId) {
        RideDTO rideDTO = service.approveDriverRequestByRideIdAndDriverId(rideId, driverId);
        return ResponseEntity.ok(rideDTO);
    }

    @PostMapping("/{rideId}/reject")
    public ResponseEntity<RideDTO> rejectDriverRideResponse(@PathVariable UUID rideId) {
        RideDTO rideDTO = service.rejectDriverRequestByRideId(rideId);
        return ResponseEntity.ok(rideDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideDTO> getRide(@PathVariable UUID id) {
        RideDTO rideDTO = service.getRideById(id);
        return ResponseEntity.ok(rideDTO);
    }

    @GetMapping("/passengers/{passengerId}")
    public ResponseEntity<List<RideDTO>> getRidesByPassengerId(@PathVariable UUID passengerId) {
        List<RideDTO> rides = service.getRidesByPassengerId(passengerId);
        return ResponseEntity.ok(rides);
    }

    @GetMapping("/passengers/{passengerId}/date-range")
    public ResponseEntity<List<RideDTO>> getPassengerRidesInDateRange(@PathVariable UUID passengerId,
                                                                      @RequestBody DateRangeDTO dateRangeDTO) {
        List<RideDTO> rides = service.getPassengerRidesInDateRange(passengerId, dateRangeDTO);
        return ResponseEntity.ok(rides);
    }

    @GetMapping("/drivers/{driverId}")
    public ResponseEntity<List<RideDTO>> getRidesByDriverId(@PathVariable UUID driverId) {
        List<RideDTO> rides = service.getRidesByDriverId(driverId);
        return ResponseEntity.ok(rides);
    }

    @GetMapping("/drivers/{driverId}/date-range")
    public ResponseEntity<List<RideDTO>> getDriverRidesInDateRange(@PathVariable UUID driverId,
                                                                   @RequestBody DateRangeDTO dateRangeDTO) {
        List<RideDTO> rides = service.getDriverRidesInDateRange(driverId, dateRangeDTO);
        return ResponseEntity.ok(rides);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RideDTO> updateRideById(@PathVariable UUID id, @RequestBody RideDTO rideDTO) {
        RideDTO updatedRideDTO = service.updateRideById(id, rideDTO);
        return ResponseEntity.ok(updatedRideDTO);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<RideDTO> changeRideStatusById(@PathVariable UUID id,
                                                        @RequestBody ChangeRideStatusRequestDTO requestDTO) {
        RideStatus status = requestDTO.getRideStatus();
        if(List.of(RideStatus.ACCEPTED, RideStatus.REQUESTED).contains(status)) {
            throw new InvalidRideStatusException("Status '%s' cannot be set manualy".formatted(status));
        }
        RideDTO rideDTO = service.changeRideStatusById(id, requestDTO);
        return ResponseEntity.ok(rideDTO);
    }
}
