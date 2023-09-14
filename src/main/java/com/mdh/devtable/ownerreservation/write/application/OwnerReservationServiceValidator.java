package com.mdh.devtable.ownerreservation.write.application;

import com.mdh.devtable.reservation.infra.persistence.ShopReservationDateTimeSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class OwnerReservationServiceValidator {

    private final ShopReservationDateTimeSeatRepository shopReservationDateTimeSeatRepository;

    @Transactional(readOnly = true)
    public void validateCreateShopReservationDateTimeSeat(Long shopReservationDateTimeId, Long seatId) {
        shopReservationDateTimeSeatRepository.findByShopReservationDateTimeIdAndSeatId(shopReservationDateTimeId, seatId)
                .ifPresent(seat -> {
                    throw new IllegalArgumentException(String.format("해당 날짜의 해당 좌석은 이미 예약 되었습니다. 날짜 id: %d, 좌석 id: %d", shopReservationDateTimeId, seatId));
                });
    }
}