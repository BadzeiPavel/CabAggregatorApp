package com.modsen.driver_service.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import enums.CarCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Entity
@Table(name = "car")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id",
                referencedColumnName = "id",
                unique = true,
                insertable = false,
                updatable = false,
                nullable = false
    )
    @JsonIgnore
    private Driver driver;

    @Column(name = "driver_id", columnDefinition = "UUID")
    private UUID driverId;

    @Column(columnDefinition = "VARCHAR(20)")
    private String number;

    @Column(columnDefinition = "SMALLINT")
    private Byte seats;

    @Column(columnDefinition = "VARCHAR(20)")
    private String color;

    @Column(columnDefinition = "VARCHAR(50)")
    private String brand;

    @Column(columnDefinition = "VARCHAR(50)")
    private String model;

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "SMALLINT")
    private CarCategory carCategory;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime lastModifiedAt;

    @Column(columnDefinition = "BOOLEAN")
    private boolean isDeleted;
}
