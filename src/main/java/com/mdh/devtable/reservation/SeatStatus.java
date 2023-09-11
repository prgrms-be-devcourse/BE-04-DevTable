package com.mdh.devtable.reservation;

public enum SeatStatus {
    AVAILABLE,
    UNAVAILABLE;

    public boolean isSameStatus(SeatStatus seatStatus) {
        return this == seatStatus;
    }
}