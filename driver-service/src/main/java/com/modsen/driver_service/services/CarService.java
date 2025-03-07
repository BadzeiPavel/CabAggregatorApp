package com.modsen.driver_service.services;

import com.modsen.driver_service.mappers.car_mapper.CarDTOMapper;
import com.modsen.driver_service.mappers.car_mapper.CarMapper;
import com.modsen.driver_service.models.dtos.CarDTO;
import com.modsen.driver_service.models.dtos.CarPatchDTO;
import com.modsen.driver_service.models.entities.Car;
import com.modsen.driver_service.repositories.CarRepository;
import lombok.RequiredArgsConstructor;
import models.dtos.GetAllPaginatedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utils.PatchUtil;

import java.time.LocalDateTime;
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
    public CarDTO getCarById(UUID id) {
        Car car = repository.getCarById(id);
        return carMapper.toCarDTO(car);
    }

    @Transactional(readOnly = true)
    public GetAllPaginatedResponse<CarDTO> getPaginatedCars(PageRequest pageRequest) {
        Page<Car> carPage = repository.findByIsDeletedFalse(pageRequest);

        List<CarDTO> carDTOs = carPage.stream()
                .map(carMapper::toCarDTO)
                .toList();

        return new GetAllPaginatedResponse<>(
                carDTOs,
                carPage.getTotalPages(),
                carPage.getTotalElements()
        );
    }

    @Transactional
    public CarDTO updateCar(UUID id, CarDTO carDTO) {
        Car car = repository.getCarById(id);
        fillInCarOnUpdate(car, carDTO);

        return carMapper.toCarDTO(repository.save(car));
    }

    @Transactional
    public CarDTO patchCar(UUID id, CarPatchDTO carPatchDTO) {
        Car car = repository.getCarById(id);
        fillInCarOnPatch(car, carPatchDTO);

        return carMapper.toCarDTO(repository.save(car));
    }

    @Transactional
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

    private static void fillInCarOnPatch(Car car, CarPatchDTO carPatchDTO) {
        PatchUtil.patchIfNotNull(carPatchDTO.getNumber(), car::setNumber);
        PatchUtil.patchIfNotNull(carPatchDTO.getSeatsCount(), car::setSeatsCount);
        PatchUtil.patchIfNotNull(carPatchDTO.getColor(), car::setColor);
        PatchUtil.patchIfNotNull(carPatchDTO.getBrand(), car::setBrand);
        PatchUtil.patchIfNotNull(carPatchDTO.getModel(), car::setModel);
        PatchUtil.patchIfNotNull(carPatchDTO.getCarCategory(), car::setCarCategory);
        car.setLastUpdateAt(LocalDateTime.now());
    }
}
