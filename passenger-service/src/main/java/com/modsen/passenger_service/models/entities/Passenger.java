package com.modsen.passenger_service.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString
@Entity
@Table(name = "passenger")
public class Passenger {
    @Id
    private UUID id;

    @Size(min = 5, message = "Username must be at least 5 characters long")
    @NotBlank(message = "Username cannot be empty")
    @Column(columnDefinition = "VARCHAR(50)")
    private String username;

    @Size(min = 2, message = "First name must be at least 5 characters long")
    @NotBlank(message = "First name cannot be empty")
    @Column(columnDefinition = "VARCHAR(50)")
    private String firstName;

    @Size(min = 2, message = "Last name must be at least 5 characters long")
    @NotBlank(message = "Last name cannot be empty")
    @Column(columnDefinition = "VARCHAR(50)")
    private String lastName;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    @Column(columnDefinition = "VARCHAR(50)")
    private String email;

    @NotBlank(message = "Phone cannot be empty")
    @Pattern(
            regexp = "^\\+?[1-9]\\d{1,14}$",
            message = "Invalid phone number format"
    )
    @Column(columnDefinition = "VARCHAR(50)")
    private String phone;

    @NotNull(message = "Birth date cannot be empty")
    @Column(columnDefinition = "DATE")
    private LocalDate birthDate;

    @NotNull(message = "Time of account creation cannot be empty")
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

    @NotNull(message = "Time of account last modification cannot be empty")
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime lastModificationAt;

    @NotNull(message = "Deletion status cannot be empty")
    @Column(columnDefinition = "BOOLEAN")
    private boolean isDeleted;
}
