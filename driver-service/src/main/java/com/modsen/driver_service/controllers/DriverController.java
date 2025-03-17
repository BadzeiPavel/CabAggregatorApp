package com.modsen.driver_service.controllers;

import com.modsen.driver_service.services.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import models.dtos.DriverDTO;
import models.dtos.GetFreeDriverNotInListRequest;
import models.dtos.UserPatchDTO;
import models.dtos.requests.ChangeDriverStatusRequest;
import models.dtos.responses.FreeDriver;
import models.dtos.responses.GetAllPaginatedResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Driver Controller", description = "CRUD API for driver")
@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService service;

    @Operation(summary = "Create driver")
    @PostMapping
    public ResponseEntity<DriverDTO> createDriver(@Valid @RequestBody DriverDTO driverDTO) {
        DriverDTO createdDriverDTO = service.createDriver(driverDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDriverDTO);
    }

    @Operation(summary = "Get first driver with status 'FREE'", description = "Usage in ride service")
    @PostMapping("/free")
    public ResponseEntity<FreeDriver> getFreeDriverNotInList(
            @RequestBody GetFreeDriverNotInListRequest getFreeDriverNotInListRequest
    ) {
        FreeDriver freeDriver =
                service.getFreeDriverNotInList(getFreeDriverNotInListRequest);
        return ResponseEntity.ok(freeDriver);
    }

    @Operation(summary = "Get driver by id")
    @GetMapping("/{id}")
    public ResponseEntity<DriverDTO> getDriver(@PathVariable UUID id) {
        DriverDTO driverDTO = service.getDriver(id);
        return ResponseEntity.ok(driverDTO);
    }

    @Operation(summary = "Get paginated drivers")
    @GetMapping
    public ResponseEntity<GetAllPaginatedResponse<DriverDTO>> getPaginatedDrivers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GetAllPaginatedResponse<DriverDTO> drivers = service.getPaginatedDrivers(PageRequest.of(page, size));
        return ResponseEntity.ok(drivers);
    }

    @Operation(summary = "Update driver by id")
    @PutMapping("/{id}")
    public ResponseEntity<DriverDTO> updateDriver(@PathVariable UUID id, @Valid @RequestBody DriverDTO driverDTO) {
        DriverDTO updatedDriverDTO = service.updateDriver(id, driverDTO);
        return ResponseEntity.ok(updatedDriverDTO);
    }

    @Operation(summary = "Change driver status by id")
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> patchDriverStatus(@PathVariable UUID id, @RequestBody ChangeDriverStatusRequest requestDTO) {
        service.patchDriverStatus(id, requestDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Patch driver by id")
    @PatchMapping("/{id}")
    public ResponseEntity<DriverDTO> patchDriver(@PathVariable UUID id, @Valid @RequestBody UserPatchDTO userPatchDTO) {
        DriverDTO driverDTO = service.patchDriver(id, userPatchDTO);
        return ResponseEntity.ok(driverDTO);
    }

    @Operation(summary = "Soft delete driver by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<DriverDTO> softDeleteDriver(@PathVariable UUID id) {
        DriverDTO driverDTO = service.softDeleteDriver(id);
        return ResponseEntity.ok(driverDTO);
    }
}
