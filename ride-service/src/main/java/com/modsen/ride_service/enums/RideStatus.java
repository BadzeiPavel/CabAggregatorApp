package com.modsen.ride_service.enums;

import java.util.List;

public enum RideStatus {
    REQUESTED,
    ACCEPTED,
    IN_RIDE,
    COMPLETED,
    REJECTED;

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
