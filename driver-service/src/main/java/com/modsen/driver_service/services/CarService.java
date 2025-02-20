package com.modsen.driver_service.services;

import com.modsen.driver_service.exceptions.CarNotFoundException;
import com.modsen.driver_service.mappers.car_mapper.CarDTOMapper;
import com.modsen.driver_service.mappers.car_mapper.CarMapper;
import com.modsen.driver_service.models.dtos.CarDTO;
import com.modsen.driver_service.models.dtos.DriverDTO;
import com.modsen.driver_service.models.entities.Car;
import com.modsen.driver_service.models.entities.Driver;
import com.modsen.driver_service.repositories.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarMapper carMapper;
    private final CarDTOMapper carDTOMapper;
    private final CarRepository carRepository;

    @Transactional
    public CarDTO saveCar(CarDTO carDTO) {
        Car car = carDTOMapper.toCar(carDTO);
        return carMapper.toCarDTO(carRepository.save(car));
    }

    @Transactional(readOnly = true)
    public CarDTO getCarDTO(UUID id) {
        Car car = carRepository.getCarByUuid(id);
        return carMapper.toCarDTO(car);
    }

    @Transactional(readOnly = true)
    public List<CarDTO> getAll() {
        return carRepository.findByIsDeletedFalse()
                .orElseThrow(() -> new CarNotFoundException("There is no any record in 'car' table"))
                .stream()
                .map(carMapper::toCarDTO)
                .toList();
    }

    @Transactional
    public CarDTO updateCar(CarDTO carDTO) {
        carRepository.checkCarExistenceById(carDTO.getId());
        Car mappedCar = carDTOMapper.toCar(carDTO);

        return carMapper.toCarDTO(carRepository.save(mappedCar));
    }

    @Transactional
    public CarDTO softDeleteCar(UUID id) {
        Car car = carRepository.getCarByUuid(id);
        car.setDeleted(true);

        return carMapper.toCarDTO(carRepository.save(car));
    }
}
