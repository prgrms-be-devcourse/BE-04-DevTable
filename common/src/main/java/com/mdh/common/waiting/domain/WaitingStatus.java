package com.mdh.common.waiting.domain;

public enum WaitingStatus {

    PROGRESS,
    CANCEL,
    NO_SHOW,
    VISITED;

    public boolean isProgress() {
        return this == WaitingStatus.PROGRESS;
    }
}
