package com.mdh.common.shop.persistence;

import com.mdh.common.shop.persistence.dto.ReservationShopSearchQueryDto;
import com.mdh.common.shop.persistence.dto.ShopQueryDto;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ShopRepositoryCustom {

    List<ShopQueryDto> searchShopCondition(MultiValueMap<String, String> cond, Pageable pageable);

    JPAQuery<Long> searchShopConditionCount(MultiValueMap<String, String> cond);

    public Page<ReservationShopSearchQueryDto> searchReservationShopByFilter(Pageable pageable,
                                                                             @NonNull LocalDate reservationDate,
                                                                             @NonNull LocalTime reservationTime,
                                                                             Integer personCount,
                                                                             String regionName,
                                                                             Integer minPrice,
                                                                             Integer maxPrice);
}
