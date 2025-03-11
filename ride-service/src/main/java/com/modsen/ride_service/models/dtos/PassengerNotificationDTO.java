package com.modsen.ride_service.models.dtos;

import com.modsen.ride_service.enums.NotificationStatus;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerNotificationDTO {

    private UUID id;

    @NotNull(message = "Passenger ID cannot be null")
    private UUID passengerId;

    @NotBlank(message = "Message cannot be empty")
    @Size(max = 100, message = "Message must not exceed 100 characters")
    private String message;

    @NotNull(message = "Status cannot be null")
    private NotificationStatus status;

    private LocalDateTime createdAt;
}
