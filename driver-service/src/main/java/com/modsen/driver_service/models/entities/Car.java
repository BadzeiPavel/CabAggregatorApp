package com.modsen.driver_service.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import enums.CarCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Entity
@Table(name = "car",
       uniqueConstraints = {@UniqueConstraint(columnNames = "number")}
)
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "driver_id",
            referencedColumnName = "id",
            unique = true,
            insertable = false,
            updatable = false,
            nullable = false
    )
    @JsonIgnore
    private Driver driver;

    @Column(name = "driver_id", columnDefinition = "UUID", nullable = false)
    private UUID driverId;

    @NotBlank(message = "Car number cannot be empty")
    @Size(max = 20, message = "Car number must be at most 20 characters long")
    @Column(nullable = false, length = 20)
    private String number;

    @Min(value = 1, message = "Seats must be at least 1")
    @Max(value = 5, message = "Seats must be at most 5")
    @Column(nullable = false)
    private short seatsCount;

    @NotBlank(message = "Color cannot be empty")
    @Size(max = 20, message = "Color must be at most 20 characters long")
    @Column(nullable = false, length = 20)
    private String color;

    @NotBlank(message = "Brand cannot be empty")
    @Size(max = 50, message = "Brand must be at most 50 characters long")
    @Column(nullable = false, length = 50)
    private String brand;

    @NotBlank(message = "Model cannot be empty")
    @Size(max = 50, message = "Model must be at most 50 characters long")
    @Column(nullable = false, length = 50)
    private String model;

    @NotNull(message = "Car category cannot be empty")
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private CarCategory carCategory;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime lastUpdateAt;

    @Column(nullable = false)
    private boolean isDeleted;
}