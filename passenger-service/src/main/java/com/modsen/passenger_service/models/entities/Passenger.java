package com.modsen.passenger_service.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Entity
@Table(name = "passenger",
       uniqueConstraints = {@UniqueConstraint(columnNames = "email"),
                            @UniqueConstraint(columnNames = "username")}
)
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Size(min = 5, message = "Username must be at least 5 characters long")
    @NotBlank(message = "Username cannot be empty")
    @Column(nullable = false, length = 50)
    private String username;

    @Size(min = 2, message = "First name must be at least 2 characters long")
    @NotBlank(message = "First name cannot be empty")
    @Column(nullable = false, length = 50)
    private String firstName;

    @Size(min = 2, message = "Last name must be at least 2 characters long")
    @NotBlank(message = "Last name cannot be empty")
    @Column(nullable = false, length = 50)
    private String lastName;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    @Column(nullable = false, length = 50)
    private String email;

    @NotBlank(message = "Phone cannot be empty")
    @Pattern(
            regexp = "^\\+?[1-9]\\d{1,14}$",
            message = "Invalid phone number format"
    )
    @Column(nullable = false, length = 50)
    private String phone;

    @NotNull(message = "Birth date cannot be empty")
    @Column(nullable = false)
    private LocalDate birthDate;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime lastUpdateAt;

    @Column(nullable = false)
    private boolean isDeleted;
}
