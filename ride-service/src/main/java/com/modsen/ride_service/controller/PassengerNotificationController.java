package com.modsen.ride_service.controller;

import com.modsen.ride_service.models.dtos.PassengerNotificationDTO;
import com.modsen.ride_service.services.PassengerNotificationService;
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

@Tag(name = "Passenger Notification Controller", description = "CRUD API for passenger notification")
@RestController
@RequestMapping("/api/v1/passenger-notifications")
@RequiredArgsConstructor
public class PassengerNotificationController {

    private final PassengerNotificationService service;

    @Operation(summary = "Create passenger notification")
    @PostMapping
    public ResponseEntity<PassengerNotificationDTO> createPassengerNotification(
            @Valid @RequestBody PassengerNotificationDTO notificationDTO
    ) {
        PassengerNotificationDTO createdNotificationDTO = service.createPassengerNotification(notificationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotificationDTO);
    }

    @Operation(summary = "Get paginated passenger notification by passenger_id")
    @GetMapping("/{passengerId}")
    public ResponseEntity<GetAllPaginatedResponse<PassengerNotificationDTO>> getPaginatedPassengerNotificationsByPassengerId(
            @PathVariable UUID passengerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GetAllPaginatedResponse<PassengerNotificationDTO> responseDTO =
                service.getPaginatedPassengerNotificationsByPassengerId(passengerId, PageRequest.of(page, size));
        return ResponseEntity.ok(responseDTO);
    }
}
