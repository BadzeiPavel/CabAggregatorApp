package com.modsen.ride_service.controller;

import com.modsen.ride_service.models.dtos.DriverNotificationDTO;
import com.modsen.ride_service.services.DriverNotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/driver-notifications")
@RequiredArgsConstructor
public class DriverNotificationController {

    private final DriverNotificationService service;

    @PostMapping
    public ResponseEntity<DriverNotificationDTO> createDriverNotification(@Valid
                                                                          @RequestBody
                                                                          DriverNotificationDTO notificationDTO) {
        DriverNotificationDTO createdNotificationDTO = service.createDriverNotification(notificationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotificationDTO);
    }

    @GetMapping("/{driverId}")
    public ResponseEntity<List<DriverNotificationDTO>> getDriverNotificationsByDriverId(@PathVariable UUID driverId) {
        List<DriverNotificationDTO> notificationDTOs = service.getDriverNotificationsByDriverId(driverId);
        return ResponseEntity.ok(notificationDTOs);
    }
}
