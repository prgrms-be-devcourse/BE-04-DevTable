package com.mdh.common.waiting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class WaitingPeople {

    @Column(name = "adult_count")
    private int adultCount;

    @Column(name = "child_count")
    private int childCount;

    public int totalPeople() {
        return adultCount + childCount;
    }
}
