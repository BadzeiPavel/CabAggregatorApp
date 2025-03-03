package com.modsen.driver_service.services;

import com.modsen.driver_service.mappers.car_mapper.CarDTOMapper;
import com.modsen.driver_service.mappers.car_mapper.CarMapper;
import com.modsen.driver_service.models.dtos.CarDTO;
import com.modsen.driver_service.models.entities.Car;
import com.modsen.driver_service.repositories.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CarService {

    private final DriverService driverService;

    private final CarMapper carMapper;
    private final CarDTOMapper carDTOMapper;
    private final CarRepository repository;

    @Transactional
    public CarDTO createCar(CarDTO carDTO) {
        Car car = carDTOMapper.toCar(carDTO);
        fillInCarDTOOnCreate(car);

        Car savedCar = repository.save(car);

        driverService.assignCarId(savedCar.getDriverId(), savedCar.getId());

        return carMapper.toCarDTO(savedCar);
    }

    @Transactional(readOnly = true)
    public CarDTO getCarDTO(UUID id) {
        Car car = repository.getCarById(id);
        return carMapper.toCarDTO(car);
    }

    public List<CarDTO> getCars() {
        return repository.findByIsDeletedFalse()
                .orElse(Collections.emptyList())
                .stream()
                .map(carMapper::toCarDTO)
                .toList();
    }

    public CarDTO updateCar(UUID id, CarDTO carDTO) {
        Car car = repository.getCarById(id);
        fillInCarOnUpdate(car, carDTO);

        return carMapper.toCarDTO(repository.save(car));
    }

    public CarDTO softDeleteCar(UUID id) {
        Car car = repository.getCarById(id);
        car.setDeleted(true);

        return carMapper.toCarDTO(repository.save(car));
    }

    private static void fillInCarDTOOnCreate(Car car) {
        car.setDeleted(false);
        car.setCreatedAt(LocalDateTime.now());
    }

    private static void fillInCarOnUpdate(Car car, CarDTO carDTO) {
        car.setNumber(carDTO.getNumber());
        car.setSeatsCount(carDTO.getSeatsCount());
        car.setColor(carDTO.getColor());
        car.setBrand(carDTO.getBrand());
        car.setModel(carDTO.getModel());
        car.setCarCategory(carDTO.getCarCategory());
    }
}
