package com.mdh.common.shop.persistence;

import com.mdh.common.reservation.domain.SeatStatus;
import com.mdh.common.shop.domain.Shop;
import com.mdh.common.shop.domain.ShopType;
import com.mdh.common.shop.persistence.dto.ReservationShopSearchQueryDto;
import com.mdh.common.shop.persistence.dto.ShopQueryDto;
import com.mdh.common.shop.persistence.dto.ShopSearchCondParam;
import com.mdh.common.waiting.domain.ShopWaitingStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mdh.common.reservation.QShopReservation.shopReservation;
import static com.mdh.common.reservation.QShopReservationDateTime.shopReservationDateTime;
import static com.mdh.common.reservation.QShopReservationDateTimeSeat.shopReservationDateTimeSeat;
import static com.mdh.common.shop.domain.QRegion.region;
import static com.mdh.common.shop.domain.QShop.shop;
import static com.mdh.common.shop.persistence.dto.ShopSearchCondParam.*;
import static com.mdh.common.shop.persistence.dto.ShopSearchSortParam.PRICE_ASC;
import static com.mdh.common.waiting.domain.QShopWaiting.shopWaiting;

@Repository
@RequiredArgsConstructor
public class ShopRepositoryImpl implements ShopRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ShopQueryDto> searchShopCondition(final MultiValueMap<String, String> cond, Pageable pageable) {

        return jpaQueryFactory.select(Projections.constructor(ShopQueryDto.class,
                        shop.id,
                        shop.name,
                        shop.shopType,
                        shop.shopPrice.lunchMinPrice,
                        shop.shopPrice.lunchMaxPrice,
                        shop.shopPrice.dinnerMinPrice,
                        shop.shopPrice.dinnerMaxPrice,
                        shop.region.city,
                        shop.region.district))
                .from(shop)
                .join(shopWaiting)
                .on(shop.id.eq(shopWaiting.shopId))
                .join(shop.region, region)
                .where(shopQueryDynamicCond(cond))
                .orderBy(getOrderSpecifier(cond.getOrDefault(SORT.getParamName(), Collections.emptyList())))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public JPAQuery<Long> searchShopConditionCount(MultiValueMap<String, String> cond) {
        return jpaQueryFactory.select(shop.count())
                .from(shop)
                .join(shopWaiting)
                .on(shop.id.eq(shopWaiting.shopId))
                .join(shop.region, region)
                .where(shopQueryDynamicCond(cond));
    }

    @Override
    public Page<ReservationShopSearchQueryDto> searchReservationShopByFilter(Pageable pageable,
                                                                             @NonNull LocalDate reservationDate,
                                                                             @NonNull LocalTime reservationTime,
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
                .join(shopReservation)
                .on(shopReservation.shopId.eq(shopReservationDateTime.shopReservation.shopId))
                .join(shop)
                .on(shopReservation.shopId.eq(shop.id))
                .join(region)
                .on(shop.region.id.eq(region.id))
                .where(booleanBuilder(reservationDate, reservationTime, personCount, regionName, minPrice, maxPrice))
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
                .join(shopReservation)
                .on(shopReservation.shopId.eq(shopReservationDateTime.shopReservation.shopId))
                .join(shop)
                .on(shopReservation.shopId.eq(shop.id))
                .join(region)
                .on(shop.region.id.eq(region.id))
                .where(booleanBuilder(reservationDate, reservationTime, personCount, regionName, minPrice, maxPrice));

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

    private BooleanBuilder booleanBuilder(LocalDate reservationDate,
                                          LocalTime reservationTime,
                                          Integer personCount,
                                          String regionName,
                                          Integer minPrice,
                                          Integer maxPrice) {
        return new BooleanBuilder().and(reservationDateTimeEq(reservationDate, reservationTime))
                .and(personLoeGoe(personCount))
                .and(regionContains(regionName))
                .and(priceLoeGoe(minPrice, maxPrice));
    }

    private BooleanExpression reservationDateTimeEq(LocalDate reservationDate, LocalTime reservationTime) {
        return shopReservationDateTime.reservationDate.eq(reservationDate).and(
                shopReservationDateTime.reservationTime.eq(reservationTime)
        );
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
        return minPrice != null ? shop.shopPrice.shopMinPrice.loe(minPrice) : null;
    }

    private BooleanExpression maxPriceGoe(Integer maxPrice) {
        return maxPrice != null ? shop.shopPrice.shopMaxPrice.goe(maxPrice) : null;
    }

    private BooleanBuilder shopQueryDynamicCond(final MultiValueMap<String, String> cond) {
        var booleanBuilder = new BooleanBuilder();

        return booleanBuilder.and(shopNameContains(cond.getOrDefault(NAME.getParamName(), Collections.emptyList())))
                .and(shopTypeEq(cond.getOrDefault(SHOP_TYPE.getParamName(), Collections.emptyList())))
                .and(shopRegionContains(cond.getOrDefault(REGION.getParamName(), Collections.emptyList())))
                .and(shopPriceEq(
                        cond.getOrDefault(ShopSearchCondParam.MIN_PRICE.getParamName(), Collections.emptyList()),
                        cond.getOrDefault(ShopSearchCondParam.MAX_PRICE.getParamName(), Collections.emptyList())))
                .and(isPossibleWaiting());

    }

    private BooleanExpression shopNameContains(List<String> shopName) {
        return !shopName.isEmpty() ? shop.name.eq(shopName.get(0)) : null;
    }

    private BooleanBuilder shopTypeEq(List<String> shopTypes) {
        var booleanBuilder = new BooleanBuilder();

        for (String inputShopType : shopTypes) {
            var shopType = ShopType.valueOf(inputShopType);
            booleanBuilder.or(shop.shopType.eq(shopType));
        }

        return booleanBuilder;
    }

    private BooleanExpression shopRegionContains(List<String> regions) {
        return !regions.isEmpty() ? shop.region.city.contains(regions.get(0))
                .or(shop.region.district.contains(regions.get(0))) : null;
    }

    private BooleanExpression shopPriceEq(List<String> shopMinPrices, List<String> shopMaxPrices) {
        if (shopMinPrices.isEmpty() && shopMaxPrices.isEmpty()) {
            return null;
        }

        if (!shopMinPrices.isEmpty() && shopMaxPrices.isEmpty()) {
            return shop.shopPrice.shopMinPrice.goe(Integer.parseInt(shopMinPrices.get(0)));
        }
        if (shopMinPrices.isEmpty() && !shopMaxPrices.isEmpty()) {
            return shop.shopPrice.shopMaxPrice.loe(Integer.parseInt(shopMaxPrices.get(0)));
        }

        return shopPriceBetween(shopMinPrices, shopMaxPrices);

    }

    private BooleanExpression shopPriceBetween(List<String> shopMinPrices, List<String> shopMaxPrices) {
        Integer minPrice = Integer.parseInt(shopMinPrices.get(0));
        Integer maxPrice = Integer.parseInt(shopMaxPrices.get(0));

        return shop.shopPrice.shopMinPrice.goe(minPrice).and(shop.shopPrice.shopMaxPrice.loe(maxPrice));
    }

    private BooleanExpression isPossibleWaiting() {
        return shopWaiting.shopWaitingStatus.eq(ShopWaitingStatus.OPEN);
    }

    private OrderSpecifier<?> getOrderSpecifier(List<String> sorts) {
        if (sorts.get(0).equals(PRICE_ASC.getSortParam())) {
            return shop.shopPrice.shopMinPrice.asc();
        }

        return shop.shopPrice.shopMaxPrice.desc();
    }
}
