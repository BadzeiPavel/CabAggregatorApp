package com.modsen.payment_service.models.enitties;

import com.modsen.payment_service.enums.PaymentStatus;
import com.modsen.payment_service.models.RideInfo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Document(collection = "payment")
public class Payment {
    @Id
    private String id;

    @NotBlank(message = "Ride ID cannot be empty")
    @Size(min = 1, max = 50, message = "Ride ID must be between 1 and 50 characters")
    private String rideId;

    @NotBlank(message = "Driver ID cannot be empty")
    @Size(min = 1, max = 50, message = "Driver ID must be between 1 and 50 characters")
    private String driverId;

    @NotBlank(message = "Passenger ID cannot be empty")
    @Size(min = 1, max = 50, message = "Passenger ID must be between 1 and 50 characters")
    private String passengerId;

    @NotNull(message = "Cost cannot be null")
    @Min(value = 0, message = "Cost cannot be negative")
    private BigDecimal cost;

    @NotNull(message = "Payment status cannot be null")
    private PaymentStatus status;

    @NotNull(message = "CreatedAt cannot be null")
    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    @Size(max = 20, message = "Promo code cannot exceed 20 characters")
    private String promoCode;

    @NotNull(message = "RideInfo cannot be null")
    private RideInfo rideInfo;
}
