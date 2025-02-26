package com.modsen.driver_service.controllers;

import com.modsen.driver_service.models.dtos.CarDTO;
import com.modsen.driver_service.services.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService service;

    @PostMapping
    public ResponseEntity<CarDTO> createCar(@RequestBody CarDTO carDTO) {
        CarDTO createdCarDTO = service.createCar(carDTO);
        return ResponseEntity.ok(createdCarDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarDTO> getCar(@PathVariable UUID id) {
        CarDTO carDTO = service.getCarDTO(id);
        return ResponseEntity.ok(carDTO);
    }

    @GetMapping
    public ResponseEntity<List<CarDTO>> getCars() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarDTO> updateCar(@PathVariable UUID id, @RequestBody CarDTO carDTO) {
        CarDTO updatedCarDTO = service.updateCar(id, carDTO);
        return ResponseEntity.ok(updatedCarDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CarDTO> softDeleteCar(@PathVariable UUID id) {
        CarDTO carDTO = service.softDeleteCar(id);
        return ResponseEntity.ok(carDTO);
    }
}
