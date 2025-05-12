package com.modsen.ride_service.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;

public enum RideStatus {
    REQUESTED,
    ACCEPTED,
    IN_RIDE,
    COMPLETED,
    REJECTED;

    @JsonValue
    public String getStatus() {
        return this.name();
    }

    @JsonCreator
    public static RideStatus fromString(String status) {
        try {
            return RideStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // Handle invalid values, or return a default value like REJECTED
        }
    }

    public List<RideStatus> canBeObtainedFrom() {
        return switch(this) {
            case REQUESTED,
                 ACCEPTED -> List.of(REQUESTED);
            case IN_RIDE -> List.of(ACCEPTED);
            case COMPLETED -> List.of(IN_RIDE);
            case REJECTED -> List.of(REQUESTED, ACCEPTED);
        };
    }
}
