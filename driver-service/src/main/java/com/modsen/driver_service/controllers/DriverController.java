package com.modsen.driver_service.controllers;

import com.modsen.driver_service.enums.DriverStatus;
import com.modsen.driver_service.models.dtos.DriverDTO;
import com.modsen.driver_service.services.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService service;

    @PostMapping
    public ResponseEntity<DriverDTO> saveDriver(@RequestBody DriverDTO driverDTO) {
        DriverDTO savedDriverDTO = service.saveDriver(driverDTO);
        return ResponseEntity.ok(savedDriverDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverDTO> getDriver(@PathVariable UUID id) {
        DriverDTO driverDTO = service.getDriverDTO(id);
        return ResponseEntity.ok(driverDTO);
    }

    @GetMapping
    public ResponseEntity<List<DriverDTO>> getAllDrivers() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/free")
    public ResponseEntity<List<DriverDTO>> getFreeDrivers() {
        List<DriverDTO> freeDrivers = service.getDriversByStatus(DriverStatus.FREE);
        return ResponseEntity.ok(freeDrivers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverDTO> updateDriver(@PathVariable UUID id, @RequestBody DriverDTO driverDTO) {
        DriverDTO savedDriverDTO = service.updateDriver(id, driverDTO);
        return ResponseEntity.ok(savedDriverDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DriverDTO> deleteDriver(@PathVariable UUID id) {
        DriverDTO driverDTO = service.softDeleteDriver(id);
        return ResponseEntity.ok(driverDTO);
    }

}
