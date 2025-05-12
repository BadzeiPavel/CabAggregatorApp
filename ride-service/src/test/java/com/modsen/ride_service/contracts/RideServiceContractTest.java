package com.modsen.ride_service.contracts;

import com.modsen.ride_service.controller.RideController;
import com.modsen.ride_service.enums.RideStatus;
import com.modsen.ride_service.mappers.ride_mappers.RideDTOMapper;
import com.modsen.ride_service.mappers.ride_mappers.RideMapper;
import com.modsen.ride_service.models.dtos.RideDTO;
import com.modsen.ride_service.services.RideService;
import enums.CarCategory;
import enums.PaymentMethod;
import models.dtos.responses.GetAllPaginatedResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureStubRunner(ids = "com.modsen:ride-service:0.0.1-SNAPSHOT:stubs",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL,
        repositoryRoot = "file:///D:/work/ModsenInternship/CarAggregatorApp/ride-service/src/test/resources/contracts")
public class RideServiceContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RideService rideService;

    @MockitoBean
    private RideMapper rideMapper;

    @MockitoBean
    private RideDTOMapper rideDTOMapper;

    @Test
    void getPaginatedRidesByPassengerId_ShouldReturn200() throws Exception {
        UUID passengerId = UUID.randomUUID();
        PageRequest pageRequest = PageRequest.of(0, 10);
        GetAllPaginatedResponse<RideDTO> mockResponse =
                new GetAllPaginatedResponse<>(List.of(new RideDTO()), 1, 1L);

        // Mock service response
        when(rideService.getPaginatedRidesByPassengerId(eq(passengerId), eq(pageRequest)))
                .thenReturn(mockResponse);

        // Test GET /api/v1/rides/passengers/{passengerId}
        mockMvc.perform(get("/api/v1/rides/passengers/{passengerId}", passengerId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void createRide_WithValidData_ShouldReturn201() throws Exception {
        // Mock service response
        RideDTO mockResponse = RideDTO.builder()
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
                .promoCode("TEST123")
                .createdAt(LocalDateTime.now())
                .build();

        when(rideService.createRide(any(RideDTO.class))).thenReturn(mockResponse);

        // Create request matching contract
        mockMvc.perform(post("/api/v1/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "passengerId": "7f000101-8b5a-1e5f-818b-5a1e5ff00000",
                        "originLatitude": 40.7128,
                        "originLongitude": -74.0060,
                        "destinationLatitude": 34.0522,
                        "destinationLongitude": -118.2437,
                        "originAddress": "some address",
                        "destinationAddress": "some address",
                        "distance": 100,
                        "carCategory": "ECONOMY",
                        "seatsCount": 2,
                        "paymentMethod": "CARD",
                        "promoCode": "TEST123"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("REQUESTED"));
    }

    @Test
    void getRideById_WhenExists_ShouldReturn200() throws Exception {
        UUID rideId = UUID.randomUUID();
        RideDTO mockResponse = RideDTO.builder()
                .id(rideId)
                .passengerId(UUID.randomUUID())
                .originAddress("Valid Origin")
                .destinationAddress("Valid Destination")
                .status(RideStatus.REQUESTED)
                .cost(new BigDecimal("29.99"))
                .distance(15.5)
                .carCategory(CarCategory.ECONOMY)
                .seatsCount((short) 2)
                .paymentMethod(PaymentMethod.CARD)
                .promoCode("TEST123")
                .createdAt(LocalDateTime.now())
                .build();

        when(rideService.getRideById(rideId)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/rides/{id}", rideId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(rideId.toString()))
                .andExpect(jsonPath("$.status").value("REQUESTED"));
    }

    @Test
    void createBasicRide_ShouldReturn500() throws Exception {
        RideDTO mockResponse = RideDTO.builder()
                .id(UUID.randomUUID())
                .status(RideStatus.REQUESTED)
                .build();

        when(rideService.createRide(any(RideDTO.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "driverId": "7f000101-8b5a-1e5f-818b-5a1e5ff00000",
                        "pickup": "Main Street"
                    }
                    """))
                .andExpect(status().is5xxServerError());
    }
}