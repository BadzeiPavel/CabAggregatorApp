package com.modsen.ride_service.integration.services;

import com.modsen.ride_service.enums.NotificationStatus;
import com.modsen.ride_service.enums.RideStatus;
import com.modsen.ride_service.exceptions.ErrorServiceResponseException;
import com.modsen.ride_service.exceptions.InvalidRideStatusException;
import com.modsen.ride_service.feign_clients.*;
import com.modsen.ride_service.models.dtos.RideDTO;
import com.modsen.ride_service.models.entitties.DriverNotification;
import com.modsen.ride_service.models.entitties.Ride;
import com.modsen.ride_service.repositories.DriverNotificationRepository;
import com.modsen.ride_service.repositories.RideRepository;
import com.modsen.ride_service.services.BingMapsService;
import com.modsen.ride_service.services.RideService;
import enums.CarCategory;
import enums.PaymentMethod;
import models.dtos.PassengerBankAccountDTO;
import models.dtos.PaymentDTO;
import models.dtos.RatingStatisticResponseDTO;
import models.dtos.requests.ChangeDriverStatusRequest;
import models.dtos.responses.FreeDriver;
import models.dtos.responses.GetAllPaginatedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import com.modsen.ride_service.exceptions.RideNotFoundException;


@SpringBootTest
@ActiveProfiles("test")
public class RideServiceIntegrationTest {

    @Autowired
    private RideService rideService;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private DriverNotificationRepository driverNotificationRepository;

    @MockitoBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockitoBean
    private DriverFeignClient driverFeignClient;

    @MockitoBean
    private DriverRatingFeignClient driverRatingFeignClient;

    @MockitoBean
    private PassengerRatingFeignClient passengerRatingFeignClient;

    @MockitoBean
    private PassengerBankAccountFeignClient passengerBankAccountFeignClient;

    @MockitoBean
    private PaymentFeignClient paymentFeignClient;

    @MockitoBean
    private BingMapsService mapsService;

    private UUID passengerId;
    private UUID driverId;
    private Ride existingRide;

    @BeforeEach
    void setUp() {
        driverNotificationRepository.deleteAll();
        rideRepository.deleteAll();
        
        passengerId = UUID.randomUUID();
        driverId = UUID.randomUUID();

        when(mapsService.getRideDetails(any())).thenReturn(
                Map.of(
                        "distance", "15.5",
                        "originAddress", "Valid Origin",
                        "destinationAddress", "Valid Destination"
                )
        );

        existingRide = rideRepository.save(createValidRide());

        when(driverRatingFeignClient.getDriverRatingStatistic(anyString()))
                .thenReturn(ResponseEntity.ok(new RatingStatisticResponseDTO(4.5, 10)));
        when(passengerRatingFeignClient.getPassengerRatingStatistic(anyString()))
                .thenReturn(ResponseEntity.ok(new RatingStatisticResponseDTO(4.5, 10)));

        // Mock bank account response
        when(passengerBankAccountFeignClient.getBalance(anyString()))
                .thenReturn(ResponseEntity.ok(PassengerBankAccountDTO.builder()
                        .passengerId(passengerId.toString())
                        .balance(BigDecimal.valueOf(1000))
                        .build())
                );
    }

    @Test
    void createRide_ValidData_ShouldCreateRideAndSendNotification() {
        // Override balance mock to simulate insufficient funds
        when(passengerBankAccountFeignClient.getBalance(anyString()))
                .thenReturn(ResponseEntity.ok(PassengerBankAccountDTO.builder()
                        .passengerId(passengerId.toString())
                        .balance(BigDecimal.valueOf(10.00)) // Insufficient balance
                        .build()));

        RideDTO request = createValidRideDTO();

        assertThatThrownBy(() -> rideService.createRide(request))
                .isInstanceOf(ErrorServiceResponseException.class);
    }

    @Test
    void getRideById_ExistingId_ShouldReturnRide() {
        RideDTO result = rideService.getRideById(existingRide.getId());
        
        assertThat(result.getId()).isEqualTo(existingRide.getId());
        assertThat(result.getPassengerId()).isEqualTo(passengerId);
    }

    @Test
    void getPaginatedRidesByPassengerId_ShouldReturnPaginatedResults() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        GetAllPaginatedResponse<RideDTO> response = 
            rideService.getPaginatedRidesByPassengerId(passengerId, pageRequest);
        
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

    @Test
    void changeRideStatus_ValidTransition_ShouldUpdateStatus() {
        RideDTO result = rideService.changeRideStatus(
            existingRide.getId(), 
            RideStatus.ACCEPTED
        );
        
        assertThat(result.getStatus()).isEqualTo(RideStatus.ACCEPTED);
        assertThat(rideRepository.findById(existingRide.getId()).get().getStatus())
            .isEqualTo(RideStatus.ACCEPTED);
    }

    @Test
    void changeRideStatus_InvalidTransition_ShouldThrowException() {
        rideService.changeRideStatus(existingRide.getId(), RideStatus.ACCEPTED);
        
        assertThatThrownBy(() -> 
            rideService.changeRideStatus(existingRide.getId(), RideStatus.REQUESTED)
        ).isInstanceOf(InvalidRideStatusException.class);
    }

    @Test
    void approveDriverRequestByRideIdAndDriverId_ValidIds_ShouldUpdateDriverAndRide() {
        // Create notification first
        DriverNotification notification = DriverNotification.builder()
                .rideId(existingRide.getId())
                .driverId(driverId)
                .passengerRating(4.5)
                .status(NotificationStatus.SEND)
                .build();
        driverNotificationRepository.save(notification);

        // Mock dependencies with proper argument matchers
        when(driverFeignClient.changeDriverStatus(eq(driverId), any(ChangeDriverStatusRequest.class)))
                .thenReturn(ResponseEntity.ok().build());
        when(paymentFeignClient.createPayment(any(PaymentDTO.class)))
                .thenReturn(ResponseEntity.ok().build());

        RideDTO result = rideService.approveDriverRequestByRideIdAndDriverId(
                existingRide.getId(),
                driverId
        );

        // Verify notification status update
        DriverNotification updatedNotification = driverNotificationRepository
                .findByRideIdAndDriverId(existingRide.getId(), driverId)
                .orElseThrow();
        assertThat(updatedNotification.getStatus()).isEqualTo(NotificationStatus.READ);

        assertThat(result.getDriverId()).isEqualTo(driverId);
        assertThat(result.getStatus()).isEqualTo(RideStatus.ACCEPTED);
        verify(kafkaTemplate, never()).send(any(), any());
    }

    @Test
    void rejectDriverRequestByRideId_ValidIds_ShouldResetDriverAndPayment() {
        // Given
        existingRide.setDriverId(driverId);
        existingRide.setStatus(RideStatus.ACCEPTED);
        Ride savedRide = rideRepository.save(existingRide);

        // Create associated notification
        DriverNotification notification = DriverNotification.builder()
                .rideId(savedRide.getId())
                .driverId(driverId)
                .passengerRating(4.5)
                .status(NotificationStatus.SEND)
                .build();
        driverNotificationRepository.save(notification);

        // Mock driver availability
        UUID newDriverId = UUID.randomUUID();
        when(driverFeignClient.getFreeDriverNotInList(any()))
                .thenReturn(ResponseEntity.ok(new FreeDriver(newDriverId)));
        when(driverFeignClient.changeDriverStatus(any(), any()))
                .thenReturn(ResponseEntity.ok().build());

        // When
        RideDTO result = rideService.rejectDriverRequestByRideId(
                savedRide.getId(),
                driverId
        );

        // Then
        assertThat(result.getDriverId()).isNull();
        verify(paymentFeignClient).deletePayment(savedRide.getId().toString());

        // Verify new notification was created
        List<DriverNotification> notifications = driverNotificationRepository.findAll();
        assertThat(notifications.get(0).getDriverId()).isEqualTo(newDriverId);
    }

    private Ride createValidRide() {
        return Ride.builder()
                .passengerId(passengerId)
                .originLatitude(40.7128)
                .originLongitude(-74.0060)
                .destinationLatitude(34.0522)
                .destinationLongitude(-118.2437)
                .originAddress("Valid Origin")
                .destinationAddress("Valid Destination")
                .distance(15.5)
                .cost(new BigDecimal("29.99"))
                .status(RideStatus.REQUESTED)
                .paymentMethod(PaymentMethod.CARD)
                .seatsCount((short) 2)
                .carCategory(CarCategory.ECONOMY)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private RideDTO createValidRideDTO() {
        return RideDTO.builder()
                .passengerId(passengerId)
                .originLatitude(40.7128)
                .originLongitude(-74.0060)
                .destinationLatitude(34.0522)
                .destinationLongitude(-118.2437)
                .carCategory(CarCategory.ECONOMY)
                .seatsCount((short) 2)
                .paymentMethod(PaymentMethod.CARD)
                .promoCode("TEST123")
                .build();
    }
}