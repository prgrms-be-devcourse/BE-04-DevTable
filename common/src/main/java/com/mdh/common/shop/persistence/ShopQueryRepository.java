package com.mdh.common.shop.persistence;

import com.mdh.common.shop.persistence.dto.ReservationShopSearchQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ShopQueryRepository {
    Page<ReservationShopSearchQueryDto> searchReservationShopByFilter(Pageable pageable,
                                                                      LocalDate reservationDate,
                                                                      LocalTime reservationTime,
                                                                      Integer personCount,
                                                                      String regionName,
                                                                      Integer minPrice,
                                                                      Integer maxPrice);
}