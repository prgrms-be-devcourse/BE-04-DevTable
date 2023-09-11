package com.mdh.devtable.reservation.domain;

public enum SeatStatus {
    AVAILABLE,
    UNAVAILABLE;

    public boolean isAvailable() {
        return this == AVAILABLE;
    }

    public boolean isUnavailable() {
        return this == UNAVAILABLE;
    }
}