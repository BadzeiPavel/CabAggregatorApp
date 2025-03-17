package com.modsen.driver_service.controllers;

import com.modsen.driver_service.models.dtos.CarDTO;
import com.modsen.driver_service.models.dtos.CarPatchDTO;
import com.modsen.driver_service.services.CarService;
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

@Tag(name = "Car Controller", description = "CRUD API for Car")
@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService service;

    @Operation(summary = "Create car", description = "Create car with provided driver_id which should be in driver DB")
    @PostMapping
    public ResponseEntity<CarDTO> createCar(@Valid @RequestBody CarDTO carDTO) {
        CarDTO createdCarDTO = service.createCar(carDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCarDTO);
    }

    @Operation(summary = "Get car by id")
    @GetMapping("/{id}")
    public ResponseEntity<CarDTO> getCar(@PathVariable UUID id) {
        CarDTO carDTO = service.getCarById(id);
        return ResponseEntity.ok(carDTO);
    }

    @Operation(summary = "Get paginated cars")
    @GetMapping
    public ResponseEntity<GetAllPaginatedResponse<CarDTO>> getPaginatedCars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GetAllPaginatedResponse<CarDTO> cars = service.getPaginatedCars(PageRequest.of(page, size));
        return ResponseEntity.ok(cars);
    }

    @Operation(summary = "Update car by id")
    @PutMapping("/{id}")
    public ResponseEntity<CarDTO> updateCar(@PathVariable UUID id, @Valid @RequestBody CarDTO carDTO) {
        CarDTO updatedCarDTO = service.updateCar(id, carDTO);
        return ResponseEntity.ok(updatedCarDTO);
    }

    @Operation(summary = "Patch car by id")
    @PatchMapping("/{id}")
    public ResponseEntity<CarDTO> patchCar(@PathVariable UUID id, @Valid @RequestBody CarPatchDTO carPatchDTO) {
        CarDTO patchedCarDTO = service.patchCar(id, carPatchDTO);
        return ResponseEntity.ok(patchedCarDTO);
    }

    @Operation(summary = "Soft delete car by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<CarDTO> softDeleteCar(@PathVariable UUID id) {
        CarDTO carDTO = service.softDeleteCar(id);
        return ResponseEntity.ok(carDTO);
    }
}
