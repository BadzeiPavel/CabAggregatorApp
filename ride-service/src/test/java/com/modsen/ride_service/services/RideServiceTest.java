package com.modsen.ride_service.services;

import com.modsen.ride_service.enums.RideStatus;
import com.modsen.ride_service.exceptions.ErrorServiceResponseException;
import com.modsen.ride_service.exceptions.InvalidRideStatusException;
import com.modsen.ride_service.feign_clients.*;
import com.modsen.ride_service.mappers.ride_mappers.RideDTOMapper;
import com.modsen.ride_service.mappers.ride_mappers.RideMapper;
import com.modsen.ride_service.models.dtos.DriverNotificationDTO;
import com.modsen.ride_service.models.dtos.RideDTO;
import com.modsen.ride_service.models.entitties.Ride;
import com.modsen.ride_service.repositories.RideRepository;
import constants.KafkaConstants;
import enums.CarCategory;
import models.dtos.PassengerBankAccountDTO;
import models.dtos.RatingStatisticResponseDTO;
import models.dtos.events.ChangeDriverStatusEvent;
import models.dtos.events.MakePaymentOnCompleteEvent;
import models.dtos.responses.FreeDriver;
import models.dtos.responses.GetAllPaginatedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import utils.CalculationUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RideServiceTest {

    @Mock
    private BingMapsService mapsService;

    @Mock
    private DriverNotificationService driverNotificationService;

    @Mock
    private PassengerNotificationService passengerNotificationService;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private DriverFeignClient driverFeignClient;

    @Mock
    private PaymentFeignClient paymentFeignClient;

    @Mock
    private DriverRatingFeignClient driverRatingFeignClient;

    @Mock
    private PassengerRatingFeignClient passengerRatingFeignClient;

    @Mock
    private PassengerBankAccountFeignClient passengerBankAccountFeignClient;

    @Mock
    private RideMapper rideMapper;

    @Mock
    private RideDTOMapper rideDTOMapper;

    @Mock
    private RideRepository repository;

    @InjectMocks
    private RideService rideService;

    @Captor
    ArgumentCaptor<ChangeDriverStatusEvent> statusEventCaptor;

    @Captor
    ArgumentCaptor<MakePaymentOnCompleteEvent> paymentEventCaptor;

    private final UUID rideId = UUID.randomUUID();
    private final UUID driverId = UUID.randomUUID();
    private final UUID passengerId = UUID.randomUUID();
    private RideDTO rideDTO;
    private Ride rideEntity;

    @BeforeEach
    void setUp() {
        rideDTO = new RideDTO();
        rideDTO.setId(rideId);
        rideDTO.setPassengerId(passengerId);
        rideDTO.setDriverId(driverId);
        rideDTO.setCarCategory(CarCategory.ECONOMY);
        rideDTO.setSeatsCount((short) 4);
        rideDTO.setPromoCode("TEST123");

        rideEntity = new Ride();
        rideEntity.setId(rideId);
        rideEntity.setPassengerId(passengerId);
        rideEntity.setDriverId(driverId);
        rideEntity.setStatus(RideStatus.REQUESTED);
        rideEntity.setSeatsCount((short) 4);
    }

    @Test
    void createRide_Success() {
        // Setup
        Map<String, String> rideDetails = new HashMap<>();
        rideDetails.put("distance", "10.5");
        rideDetails.put("originAddress", "Origin");
        rideDetails.put("destinationAddress", "Destination");

        when(mapsService.getRideDetails(rideDTO)).thenReturn(rideDetails);
        when(passengerBankAccountFeignClient.getBalance(any()))
                .thenReturn(ResponseEntity.ok(new PassengerBankAccountDTO("acc-123", passengerId.toString(), BigDecimal.valueOf(100))));
        when(rideDTOMapper.toRide(rideDTO)).thenReturn(rideEntity);
        when(repository.save(rideEntity)).thenReturn(rideEntity);
        when(rideMapper.toRideDTO(rideEntity)).thenReturn(rideDTO);
        when(driverFeignClient.getFreeDriverNotInList(any()))
                .thenReturn(ResponseEntity.ok(new FreeDriver(UUID.randomUUID())));
        when(driverNotificationService.createDriverNotification(any()))
                .thenReturn(new DriverNotificationDTO());
        when(passengerRatingFeignClient.getPassengerRatingStatistic(passengerId.toString())).thenReturn(ResponseEntity.ok(new RatingStatisticResponseDTO(4, 1)));

        // Execute
        RideDTO result = rideService.createRide(rideDTO);

        // Verify
        assertThat(result.getCost()).isEqualTo(CalculationUtil.calculateRideCostByDistanceAndCarCategoryAndPromoCode(
                10.5, CarCategory.ECONOMY, "TEST123"
        ));
        verify(driverNotificationService).createDriverNotification(any());
    }

    @Test
    void getRideById_Success() {
        when(repository.findById(rideId)).thenReturn(Optional.of(rideEntity));
        when(rideMapper.toRideDTO(rideEntity)).thenReturn(rideDTO);

        RideDTO result = rideService.getRideById(rideId);

        assertThat(result).isEqualTo(rideDTO);
    }

    @Test
    void changeRideStatus_ToCompleted_SendsKafkaEvents() {
        rideEntity.setStatus(RideStatus.IN_RIDE);
        rideEntity.setDriverId(driverId);
        when(repository.findById(rideId)).thenReturn(Optional.of(rideEntity));

        rideService.changeRideStatus(rideId, RideStatus.COMPLETED);

        verify(kafkaTemplate).send(eq(KafkaConstants.RIDE_COMPLETED_EVENT), statusEventCaptor.capture());
        verify(kafkaTemplate).send(eq(KafkaConstants.RIDE_PAYMENT_ON_COMPLETE_EVENT), paymentEventCaptor.capture());
        assertThat(statusEventCaptor.getValue().getDriverId()).isEqualTo(driverId);
    }

    @Test
    void approveDriverRequest_Success() {
        // Setup
        rideEntity.setId(rideId);
        rideEntity.setPassengerId(passengerId);
        rideEntity.setStatus(RideStatus.REQUESTED);

        // Mock repository responses
        when(repository.findById(rideId)).thenReturn(Optional.of(rideEntity));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Mock Feign client responses
        when(driverFeignClient.changeDriverStatus(any(), any()))
                .thenReturn(ResponseEntity.ok().build());
        when(driverRatingFeignClient.getDriverRatingStatistic(any()))
                .thenReturn(ResponseEntity.ok(new RatingStatisticResponseDTO(4.5, 10)));
        when(paymentFeignClient.createPayment(any()))
                .thenReturn(ResponseEntity.ok().build());

        // Mock mapper
        when(rideMapper.toRideDTO(any())).thenReturn(rideDTO);

        // Execute
        RideDTO result = rideService.approveDriverRequestByRideIdAndDriverId(rideId, driverId);

        // Verify
        verify(repository).findById(rideId);
        verify(driverFeignClient).changeDriverStatus(eq(driverId), any());
        verify(passengerNotificationService).createPassengerNotification(any());
        assertThat(result).isEqualTo(rideDTO);
    }

    @Test
    void rejectDriverRequest_Success() {
        // Setup
        UUID newDriverId = UUID.randomUUID();

        // Initialize ride with all required fields
        rideEntity.setId(rideId);
        rideEntity.setPassengerId(passengerId);
        rideEntity.setDriverId(driverId);
        rideEntity.setSeatsCount((short) 4);
        rideEntity.setCarCategory(CarCategory.ECONOMY);
        rideEntity.setStatus(RideStatus.REQUESTED);

        // Mock dependencies
        when(repository.findByRideId(rideId)).thenReturn(rideEntity);
        when(paymentFeignClient.deletePayment(rideId.toString()))
                .thenReturn(ResponseEntity.ok().build());
        when(driverFeignClient.changeDriverStatus(eq(driverId), any()))
                .thenReturn(ResponseEntity.ok().build());
        when(driverFeignClient.getFreeDriverNotInList(any()))
                .thenReturn(ResponseEntity.ok(new FreeDriver(newDriverId)));
        when(passengerRatingFeignClient.getPassengerRatingStatistic(passengerId.toString()))
                .thenReturn(ResponseEntity.ok(new RatingStatisticResponseDTO(4.5, 10)));
        doNothing().when(driverNotificationService)
                .changeStatusOnReadByRideIdAndDriverId(rideId, driverId);
        when(repository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));
        when(rideMapper.toRideDTO(any())).thenReturn(rideDTO);

        // Execute
        RideDTO result = rideService.rejectDriverRequestByRideId(rideId, driverId);

        // Verify
        verify(driverFeignClient).changeDriverStatus(eq(driverId), any());
        verify(driverNotificationService).changeStatusOnReadByRideIdAndDriverId(rideId, driverId);
        verify(driverNotificationService).createDriverNotification(any());
        assertThat(rideEntity.getDriverId()).isNull();
    }

    @Test
    void getPaginatedRides_ReturnsCorrectStructure() {
        Page<Ride> page = new PageImpl<>(List.of(rideEntity));
        when(repository.findByPassengerId(passengerId, PageRequest.of(0, 10))).thenReturn(page);
        when(rideMapper.toRideDTO(rideEntity)).thenReturn(rideDTO);

        GetAllPaginatedResponse<RideDTO> response =
                rideService.getPaginatedRidesByPassengerId(passengerId, PageRequest.of(0, 10));

        assertThat(response.getContent()).containsExactly(rideDTO);
    }

    @Test
    void invalidStatusTransition_ThrowsException() {
        rideEntity.setStatus(RideStatus.COMPLETED);
        when(repository.findById(rideId)).thenReturn(Optional.of(rideEntity));

        assertThatThrownBy(() -> rideService.changeRideStatus(rideId, RideStatus.REQUESTED))
                .isInstanceOf(InvalidRideStatusException.class);
    }

    @Test
    void recoverRide_Success() {
        rideEntity.setStatus(RideStatus.COMPLETED);
        rideEntity.setEndTime(LocalDateTime.now());
        when(repository.findById(rideId)).thenReturn(Optional.of(rideEntity));

        rideService.recoverRide(rideId);

        assertThat(rideEntity.getStatus()).isEqualTo(RideStatus.IN_RIDE);
        assertThat(rideEntity.getEndTime()).isNull();
    }

    @Test
    void createRide_InsufficientBalance_ThrowsException() {
        // Setup
        Map<String, String> rideDetails = new HashMap<>();
        rideDetails.put("distance", "20.0"); // Increased distance to ensure higher cost
        rideDetails.put("originAddress", "Origin");
        rideDetails.put("destinationAddress", "Destination");

        when(mapsService.getRideDetails(rideDTO)).thenReturn(rideDetails);
        when(passengerBankAccountFeignClient.getBalance(any()))
                .thenReturn(ResponseEntity.ok(new PassengerBankAccountDTO("acc-123", passengerId.toString(), BigDecimal.valueOf(5))));

        // Execute & Verify
        assertThatThrownBy(() -> rideService.createRide(rideDTO))
                .isInstanceOf(ErrorServiceResponseException.class)
                .hasMessageContaining("Insufficient passenger balance");
    }
}