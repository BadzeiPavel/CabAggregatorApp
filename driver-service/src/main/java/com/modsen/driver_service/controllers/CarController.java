package com.modsen.driver_service.controllers;

import com.modsen.driver_service.models.dtos.CarDTO;
import com.modsen.driver_service.services.CarService;
import com.modsen.driver_service.services.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @PostMapping
    public ResponseEntity<CarDTO> saveCar(@RequestBody CarDTO carDTO) {
        CarDTO savedCar = carService.saveCar(carDTO);
        return ResponseEntity.ok(savedCar);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarDTO> getCar(@PathVariable UUID id) {
        CarDTO carDTO = carService.getCarDTO(id);
        return ResponseEntity.ok(carDTO);
    }

    @GetMapping
    public ResponseEntity<List<CarDTO>> getAllCars() {
        return ResponseEntity.ok(carService.getAll());
    }

    @PutMapping
    public ResponseEntity<CarDTO> updateCar(@RequestBody CarDTO carDTO) {
        CarDTO savedCarDTO = carService.updateCar(carDTO);
        return ResponseEntity.ok(savedCarDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CarDTO> deleteCar(@PathVariable UUID id) {
        CarDTO carDTO = carService.softDeleteCar(id);
        return ResponseEntity.ok(carDTO);
    }

}
