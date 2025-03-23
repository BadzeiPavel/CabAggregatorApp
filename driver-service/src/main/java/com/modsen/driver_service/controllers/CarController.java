package com.modsen.driver_service.controllers;

import com.modsen.driver_service.models.dtos.CarDTO;
import com.modsen.driver_service.models.dtos.CarPatchDTO;
import com.modsen.driver_service.services.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import models.dtos.GetAllPaginatedResponseDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService service;

    @PostMapping
    public ResponseEntity<CarDTO> createCar(@Valid @RequestBody CarDTO carDTO) {
        CarDTO createdCarDTO = service.createCar(carDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCarDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarDTO> getCar(@PathVariable UUID id) {
        CarDTO carDTO = service.getCarDTO(id);
        return ResponseEntity.ok(carDTO);
    }

    @GetMapping
    public ResponseEntity<GetAllPaginatedResponseDTO<CarDTO>> getPaginatedCars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GetAllPaginatedResponseDTO<CarDTO> cars = service.getPaginatedCars(PageRequest.of(page, size));
        return ResponseEntity.ok(cars);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarDTO> updateCar(@PathVariable UUID id, @Valid @RequestBody CarDTO carDTO) {
        CarDTO updatedCarDTO = service.updateCar(id, carDTO);
        return ResponseEntity.ok(updatedCarDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CarDTO> patchCar(@PathVariable UUID id, @Valid @RequestBody CarPatchDTO carPatchDTO) {
        CarDTO patchedCarDTO = service.patchCar(id, carPatchDTO);
        return ResponseEntity.ok(patchedCarDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CarDTO> softDeleteCar(@PathVariable UUID id) {
        CarDTO carDTO = service.softDeleteCar(id);
        return ResponseEntity.ok(carDTO);
    }
}
