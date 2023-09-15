package com.mdh.devtable.reservation;

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