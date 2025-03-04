package com.modsen.driver_service.controllers;

import com.modsen.driver_service.enums.DriverStatus;
import com.modsen.driver_service.models.dtos.DriverDTO;
import com.modsen.driver_service.services.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import models.dtos.GetAllPaginatedResponseDTO;
import models.dtos.UserPatchDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService service;

    @PostMapping
    public ResponseEntity<DriverDTO> createDriver(@Valid @RequestBody DriverDTO driverDTO) {
        DriverDTO createdDriverDTO = service.createDriver(driverDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDriverDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverDTO> getDriver(@PathVariable UUID id) {
        DriverDTO driverDTO = service.getDriver(id);
        return ResponseEntity.ok(driverDTO);
    }

    @GetMapping
    public ResponseEntity<GetAllPaginatedResponseDTO<DriverDTO>> getPaginatedDrivers(
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        GetAllPaginatedResponseDTO<DriverDTO> drivers = service.getPaginatedDrivers(PageRequest.of(page, size));
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/free")
    public ResponseEntity<GetAllPaginatedResponseDTO<DriverDTO>> getPaginatedFreeDrivers(
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        GetAllPaginatedResponseDTO<DriverDTO> freeDrivers =
                service.getPaginatedDriversByStatus(DriverStatus.FREE, PageRequest.of(page, size));
        return ResponseEntity.ok(freeDrivers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverDTO> updateDriver(@PathVariable UUID id, @Valid @RequestBody DriverDTO driverDTO) {
        DriverDTO updatedDriverDTO = service.updateDriver(id, driverDTO);
        return ResponseEntity.ok(updatedDriverDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DriverDTO> patchDriver(@PathVariable UUID id,
                                                 @Valid @RequestBody UserPatchDTO userPatchDTO) {
        DriverDTO driverDTO = service.patchDriver(id, userPatchDTO);
        return ResponseEntity.ok(driverDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DriverDTO> softDeleteDriver(@PathVariable UUID id) {
        DriverDTO driverDTO = service.softDeleteDriver(id);
        return ResponseEntity.ok(driverDTO);
    }
}
