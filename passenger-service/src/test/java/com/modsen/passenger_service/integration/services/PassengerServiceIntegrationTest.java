package com.modsen.passenger_service.integration.services;

import com.modsen.passenger_service.feign_clients.AuthFeignClient;
import com.modsen.passenger_service.models.entities.Passenger;
import com.modsen.passenger_service.repositories.PassengerRepository;
import com.modsen.passenger_service.services.PassengerService;
import models.dtos.PassengerDTO;
import models.dtos.UserPatchDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@SpringBootTest
//@ActiveProfiles("integrationtest")
public class PassengerServiceIntegrationTest {

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private PassengerRepository passengerRepository;

    @MockitoBean
    private AuthFeignClient authFeignClient;

    private UUID passengerId;

    @BeforeEach
    void setUp() {
        Passenger passenger = new Passenger();
        passengerId = UUID.randomUUID();
        passenger.setId(passengerId);
        passenger.setUsername("Username");
        passenger.setFirstName("firstName");
        passenger.setLastName("lastName");
        passenger.setEmail("user@example.com");
        passenger.setPhone("+1234567890");
        passenger.setBirthDate(LocalDate.now());
        passenger.setCreatedAt(LocalDateTime.now());
        passenger.setDeleted(false);
        passengerRepository.save(passenger);
    }

    @Test
    void createPassenger_ShouldSavePassengerWithCorrectFields() {
        UUID id = UUID.randomUUID();
        PassengerDTO dto = PassengerDTO.builder()
                .id(id)
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .phone("+1234567890")
                .birthDate(LocalDate.now())
                .build();

        PassengerDTO result = passengerService.createPassenger(dto);

        assertThat(result.getId()).isEqualTo(id);
        Passenger savedPassenger = passengerRepository.findById(id).orElseThrow();
        assertThat(savedPassenger.isDeleted()).isFalse();
        assertThat(savedPassenger.getCreatedAt()).isNotNull();
    }

    @Test
    void getPassenger_ShouldReturnSavedPassenger() {
        Passenger passenger = new Passenger();
        passenger.setId(passengerId);
        passenger.setUsername("testuser");
        passenger.setFirstName("Test");
        passenger.setLastName("User");
        passenger.setEmail("test@example.com");
        passenger.setPhone("1234567890");
        passenger.setBirthDate(LocalDate.now());
        passenger.setCreatedAt(LocalDateTime.now());
        passenger.setDeleted(false);
        passengerRepository.save(passenger);

        PassengerDTO result = passengerService.getPassenger(passengerId);

        assertThat(result.getId()).isEqualTo(passengerId);
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getFirstName()).isEqualTo("Test");
    }

    @Test
    void updatePassenger_ShouldUpdateAllFieldsAndCallAuthService() {
        PassengerDTO updateDTO = PassengerDTO.builder()
                .id(passengerId)
                .username("newuser")
                .firstName("New")
                .lastName("User")
                .email("new@email.com")
                .phone("9876543210")
                .birthDate(LocalDate.of(1990, 5, 15))
                .build();

        passengerService.updatePassenger(passengerId, updateDTO);

        Passenger updated = passengerRepository.findById(passengerId).orElseThrow();
        assertThat(updated.getUsername()).isEqualTo("newuser");
        assertThat(updated.getFirstName()).isEqualTo("New");
        assertThat(updated.getLastName()).isEqualTo("User");
        assertThat(updated.getEmail()).isEqualTo("new@email.com");
        assertThat(updated.getPhone()).isEqualTo("9876543210");
        assertThat(updated.getBirthDate()).isEqualTo(LocalDate.of(1990, 5, 15));

        UserPatchDTO expectedPatch = new UserPatchDTO(
                "newuser", "New", "User", "new@email.com", "9876543210", LocalDate.of(1990, 5, 15)
        );
        verify(authFeignClient).patch(passengerId.toString(), expectedPatch);
    }

    @Test
    void patchPassenger_ShouldUpdatePartialFieldsAndCallAuthService() {
        UserPatchDTO patchDTO = new UserPatchDTO();
        patchDTO.setEmail("new@email.com");
        patchDTO.setPhone("+9998887777");

        passengerService.patchPassenger(passengerId, patchDTO);

        Passenger patched = passengerRepository.findById(passengerId).orElseThrow();
        assertThat(patched.getEmail()).isEqualTo("new@email.com");
        assertThat(patched.getPhone()).isEqualTo("+9998887777");
        assertThat(patched.getUsername()).isEqualTo("Username"); // unchanged

        ArgumentCaptor<UserPatchDTO> captor = ArgumentCaptor.forClass(UserPatchDTO.class);
        verify(authFeignClient).patch(eq(passengerId.toString()), captor.capture());
        UserPatchDTO sentPatch = captor.getValue();
        assertThat(sentPatch.getEmail()).isEqualTo("new@email.com");
        assertThat(sentPatch.getPhone()).isEqualTo("+9998887777");
        assertThat(sentPatch.getUsername()).isNull();
        assertThat(sentPatch.getFirstName()).isNull();
    }

    @Test
    void softDeletePassenger_ShouldMarkDeletedAndCallAuthService() {
        passengerService.softDeletePassenger(passengerId);

        Passenger deleted = passengerRepository.findById(passengerId).orElseThrow();
        assertThat(deleted.isDeleted()).isTrue();
        verify(authFeignClient).delete(passengerId.toString());
    }
}