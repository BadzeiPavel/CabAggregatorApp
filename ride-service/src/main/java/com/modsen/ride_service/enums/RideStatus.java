package com.modsen.ride_service.enums;

import java.util.List;

public enum RideStatus {
    REQUESTED,
    ACCEPTED,
    IN_RIDE,
    COMPLETED,
    REJECTED;

    public List<RideStatus> canBeObtainedFrom() {
        return switch (this) {
            case REQUESTED,
                 IN_RIDE -> List.of(ACCEPTED);
            case ACCEPTED -> List.of(REQUESTED);
            case COMPLETED -> List.of(IN_RIDE);
            case REJECTED -> List.of(REQUESTED, ACCEPTED);
        };
    }
}
