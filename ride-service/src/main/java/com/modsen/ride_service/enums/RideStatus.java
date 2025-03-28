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
        switch (this) {
            case REQUESTED:
            case ACCEPTED:
                return List.of(REQUESTED, ACCEPTED);
            case IN_RIDE:
                return List.of(ACCEPTED);
            case COMPLETED:
                return List.of(IN_RIDE);
            case REJECTED:
                return List.of(REQUESTED, ACCEPTED);
            default:
                return List.of(); // Default case if the state doesn't match
        }
    }
}
