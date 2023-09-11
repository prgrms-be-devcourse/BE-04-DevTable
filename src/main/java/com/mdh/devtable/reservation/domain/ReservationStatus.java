package com.mdh.devtable.reservation.domain;

public enum ReservationStatus {
    CREATED, CANCEL, NO_SHOW, VISITED;

    public boolean isCreated() {
        return this == ReservationStatus.CREATED;
    }
}
