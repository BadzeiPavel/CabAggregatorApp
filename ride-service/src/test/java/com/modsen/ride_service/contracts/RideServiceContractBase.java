package com.modsen.ride_service.contracts;

import com.modsen.ride_service.enums.RideStatus;
import com.modsen.ride_service.feign_clients.DriverFeignClient;
import com.modsen.ride_service.feign_clients.PassengerBankAccountFeignClient;
import com.modsen.ride_service.feign_clients.PaymentFeignClient;
import com.modsen.ride_service.models.dtos.RideDTO;
import com.modsen.ride_service.models.entitties.Ride;
import com.modsen.ride_service.repositories.RideRepository;
import com.modsen.ride_service.services.BingMapsService;
import enums.CarCategory;
import enums.PaymentMethod;
import models.dtos.PassengerBankAccountDTO;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureStubRunner
public abstract class RideServiceContractBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RideRepository rideRepository;

    @MockitoBean
    private BingMapsService bingMapsService;

    @MockitoBean
    private PassengerBankAccountFeignClient passengerBankAccountFeignClient;

    @MockitoBean
    private DriverFeignClient driverFeignClient;

    @MockitoBean
    private PaymentFeignClient paymentFeignClient;

    @BeforeEach
    void setup() {
        rideRepository.deleteAll();
        setupMocks();
    }

    private void setupMocks() {
        // Common mocks can be set up here
    }

    public void setupPassengerHasSufficientBalance() {
        when(bingMapsService.getRideDetails(any(RideDTO.class)))
                .thenReturn(Map.of(
                        "distance", "15.5",
                        "originAddress", "Valid Origin",
                        "destinationAddress", "Valid Destination"
                ));
        when(passengerBankAccountFeignClient.getBalance(anyString()))
                .thenReturn(ResponseEntity.ok(PassengerBankAccountDTO.builder()
                        .balance(BigDecimal.valueOf(1000))
                        .build()));
    }

    public void setupRideExists() {
        Ride ride = createValidRide();
        rideRepository.save(ride);
    }

    public void setupRideExistsWithStatusRequested() {
        Ride ride = createValidRide();
        ride.setStatus(RideStatus.REQUESTED);
        rideRepository.save(ride);
    }

    private Ride createValidRide() {
        return Ride.builder()
                .id(UUID.randomUUID())
                .passengerId(UUID.randomUUID())
                .originAddress("Valid Origin")
                .destinationAddress("Valid Destination")
                .status(RideStatus.REQUESTED)
                .cost(new BigDecimal("29.99"))
                .distance(15.5)
                .carCategory(CarCategory.ECONOMY)
                .seatsCount((short) 2)
                .paymentMethod(PaymentMethod.CARD)
                .createdAt(LocalDateTime.now())
                .build();
    }
}