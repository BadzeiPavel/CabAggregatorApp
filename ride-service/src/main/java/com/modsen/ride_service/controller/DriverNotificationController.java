package com.modsen.ride_service.controller;

import com.modsen.ride_service.models.dtos.DriverNotificationDTO;
import com.modsen.ride_service.services.DriverNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import models.dtos.responses.GetAllPaginatedResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Driver Notification Controller", description = "CRUD API for driver notification")
@RestController
@RequestMapping("/api/v1/driver-notifications")
@RequiredArgsConstructor
public class DriverNotificationController {

    private final DriverNotificationService service;

    @Operation(summary = "Create driver notification")
    @PostMapping
    public ResponseEntity<DriverNotificationDTO> createDriverNotification(
            @Valid @RequestBody DriverNotificationDTO notificationDTO
    ) {
        DriverNotificationDTO createdNotificationDTO = service.createDriverNotification(notificationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotificationDTO);
    }

    @Operation(summary = "Get paginated driver notification by driver_id")
    @GetMapping("/{driverId}")
    public ResponseEntity<GetAllPaginatedResponse<DriverNotificationDTO>> getPaginatedDriverNotificationsByDriverId(
            @PathVariable UUID driverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GetAllPaginatedResponse<DriverNotificationDTO> responseDTO =
                service.getPaginatedDriverNotificationsByDriverId(driverId, PageRequest.of(page, size));
        return ResponseEntity.ok(responseDTO);
    }
}
