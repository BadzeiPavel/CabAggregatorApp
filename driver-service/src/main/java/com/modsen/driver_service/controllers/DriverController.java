package com.modsen.driver_service.controllers;

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

    private final DriverService driverService;

    @PostMapping
    public ResponseEntity<DriverDTO> saveDriver(@RequestBody DriverDTO driverDTO) {
        DriverDTO savedDriverDTO = driverService.saveDriver(driverDTO);
        return ResponseEntity.ok(savedDriverDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverDTO> getDriver(@PathVariable UUID id) {
        DriverDTO driverDTO = driverService.getDriverDTO(id);
        return ResponseEntity.ok(driverDTO);
    }

    @GetMapping
    public ResponseEntity<List<DriverDTO>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAll());
    }

    @PutMapping
    public ResponseEntity<DriverDTO> updateDriver(@RequestBody DriverDTO driverDTO) {
        DriverDTO savedDriverDTO = driverService.updateDriver(driverDTO);
        return ResponseEntity.ok(savedDriverDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DriverDTO> deleteDriver(@PathVariable UUID id) {
        DriverDTO driverDTO = driverService.softDeleteDriver(id);
        return ResponseEntity.ok(driverDTO);
    }

}
