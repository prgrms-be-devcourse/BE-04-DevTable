package com.mdh.common.shop.persistence;

import com.mdh.common.shop.domain.Shop;
import com.mdh.common.shop.persistence.dto.ReservationShopSearchQueryDto;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static com.mdh.common.reservation.QShopReservation.shopReservation;
import static com.mdh.common.reservation.QShopReservationDateTime.shopReservationDateTime;
import static com.mdh.common.reservation.QShopReservationDateTimeSeat.shopReservationDateTimeSeat;
import static com.mdh.common.shop.domain.QRegion.region;
import static com.mdh.common.shop.domain.QShop.shop;

@RequiredArgsConstructor
public class ShopQueryRepositoryImpl implements ShopQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 보여줄 것? 매장 정보, 예약 날짜, 날짜의 가능한 좌석 수
     * 매장
     * join (매장 예약 날짜 join 매장 예약 좌석 where 날짜 = 날짜 & 시간 = 시간)
     * group by 매장
     * order by 매장 예약 가능한 좌석 수(1)
     */
    @Override
    public Page<ReservationShopSearchQueryDto> searchReservationShopByFilter(Pageable pageable,
                                                                             LocalDate reservationDate,
                                                                             LocalTime reservationTime,
                                                                             Integer personCount,
                                                                             String regionName,
                                                                             Integer minPrice,
                                                                             Integer maxPrice) {
        var content = jpaQueryFactory
                .select(Projections.constructor(ReservationShopSearchQueryDto.class,
                        shop.id,
                        shop.name,
                        shop.description,
                        shop.shopType,
                        region.city,
                        region.district,
                        shop.shopPrice,
                        seatStatusCaseBuilder().sum().as("availableSeatCount")
                ))
                .from(shopReservationDateTimeSeat)
                .join(shopReservationDateTime)
                .on(shopReservationDateTimeSeat.shopReservationDateTime.id.eq(shopReservationDateTime.id))
                .where(shopReservationDateTime.reservationDate.eq(reservationDate)
                        .and(shopReservationDateTime.reservationTime.eq(reservationTime)))
                .join(shopReservation)
                .on(shopReservation.shopId.eq(shopReservationDateTime.shopReservation.shopId))
                .where(personLoeGoe(personCount))
                .join(shop)
                .on(shopReservation.shopId.eq(shop.id))
                .join(region)
                .on(shop.region.id.eq(region.id))
                .where(regionContains(regionName))
                .where(priceLoeGoe(minPrice, maxPrice))
                .groupBy(shop.id) // 아이디로 group by
                .orderBy(getOrderSpecifiers(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        var totalCount = jpaQueryFactory
                .select(shop.countDistinct())
                .from(shopReservationDateTimeSeat)
                .join(shopReservationDateTime)
                .on(shopReservationDateTimeSeat.shopReservationDateTime.id.eq(shopReservationDateTime.id))
                .where(shopReservationDateTime.reservationDate.eq(reservationDate)
                        .and(shopReservationDateTime.reservationTime.eq(reservationTime)))
                .join(shopReservation)
                .on(shopReservation.shopId.eq(shopReservationDateTime.shopReservation.shopId))
                .where(personLoeGoe(personCount))
                .join(shop)
                .on(shopReservation.shopId.eq(shop.id))
                .join(region)
                .on(shop.region.id.eq(region.id))
                .where(regionContains(regionName))
                .where(priceLoeGoe(minPrice, maxPrice));

        return PageableExecutionUtils.getPage(content, pageable, totalCount::fetchOne);
    }

    private OrderSpecifier[] getOrderSpecifiers(Pageable pageable) {
        var sort = pageable.getSort();
        var orderSpecifiers = new ArrayList<>();
        orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, Expressions.stringPath("availableSeatCount")));
        var shopOrderSpecifiers = sort.get().map(o -> {
            Order order = o.isAscending() ? Order.ASC : Order.DESC;
            String property = o.getProperty();
            PathBuilder<Shop> pathBuilder = new PathBuilder<>(Shop.class, "shop");
            return new OrderSpecifier(order, pathBuilder.getString(property));
        }).toList();
        orderSpecifiers.addAll(shopOrderSpecifiers);
        return orderSpecifiers.stream().toArray(OrderSpecifier[]::new);
    }

    private NumberExpression<Integer> seatStatusCaseBuilder() {
        return new CaseBuilder()
                .when(shopReservationDateTimeSeat.seatStatus.eq(SeatStatus.AVAILABLE))
                .then(1)
                .otherwise(0);
    }

    private BooleanExpression personLoeGoe(Integer personCount) {
        return personCount != null ? shopReservation.minimumPerson.loe(personCount)
                .and(shopReservation.maximumPerson.goe(personCount)) : null;
    }

    private BooleanExpression regionContains(String regionName) {
        return regionName != null ? region.city.contains(regionName).or(region.district.contains(regionName)) : null;
    }

    private BooleanExpression priceLoeGoe(Integer minPrice, Integer maxPrice) {
        if (minPrice == null) return maxPriceGoe(maxPrice);
        return minPriceLoe(minPrice).and(maxPriceGoe(maxPrice));
    }

    private BooleanExpression minPriceLoe(Integer minPrice) {
        return minPrice != null ? shop.shopPrice.lunchMinPrice.loe(minPrice)
                .or(shop.shopPrice.dinnerMinPrice.loe(minPrice)) : null;
    }

    private BooleanExpression maxPriceGoe(Integer maxPrice) {
        return maxPrice != null ? shop.shopPrice.lunchMaxPrice.goe(maxPrice)
                .or(shop.shopPrice.dinnerMaxPrice.goe(maxPrice)) : null;
    }
}