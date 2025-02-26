package com.modsen.driver_service.services;

import com.modsen.driver_service.mappers.car_mapper.CarDTOMapper;
import com.modsen.driver_service.mappers.car_mapper.CarMapper;
import com.modsen.driver_service.models.dtos.CarDTO;
import com.modsen.driver_service.models.entities.Car;
import com.modsen.driver_service.repositories.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarMapper carMapper;
    private final CarDTOMapper carDTOMapper;
    private final CarRepository carRepository;

    private final DriverService driverService;

    @Transactional
    public CarDTO saveCar(CarDTO carDTO) {
        Car car = carDTOMapper.toCar(carDTO);
        Car savedCar = carRepository.save(car);

        driverService.assignCarId(savedCar.getDriverId(), savedCar.getId());

        return carMapper.toCarDTO(savedCar);
    }

    @Transactional(readOnly = true)
    public CarDTO getCarDTO(UUID id) {
        Car car = carRepository.getCarById(id);
        return carMapper.toCarDTO(car);
    }

    public List<CarDTO> getAll() {
        return carRepository.findByIsDeletedFalse()
                .orElse(Collections.emptyList())
                .stream()
                .map(carMapper::toCarDTO)
                .toList();
    }

    public CarDTO updateCar(UUID id, CarDTO carDTO) {
        carRepository.checkCarExistenceById(id);
        Car mappedCar = carDTOMapper.toCar(carDTO);

        return carMapper.toCarDTO(carRepository.save(mappedCar));
    }

    public CarDTO softDeleteCar(UUID id) {
        Car car = carRepository.getCarById(id);
        car.setDeleted(true);

        return carMapper.toCarDTO(carRepository.save(car));
    }
}
