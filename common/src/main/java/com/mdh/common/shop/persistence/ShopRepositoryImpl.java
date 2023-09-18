package com.mdh.common.shop.persistence;

import com.mdh.common.shop.domain.ShopType;
import com.mdh.common.shop.persistence.dto.ShopQueryDto;
import com.mdh.common.shop.persistence.dto.ShopSearchCondParam;
import com.mdh.common.waiting.domain.ShopWaitingStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.List;

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
