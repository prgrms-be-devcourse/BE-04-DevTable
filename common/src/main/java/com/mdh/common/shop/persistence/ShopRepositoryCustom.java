package com.mdh.common.shop.persistence;

import com.mdh.common.shop.persistence.dto.ShopQueryDto;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;

import java.util.List;

public interface ShopRepositoryCustom {

    List<ShopQueryDto> searchShopCondition(MultiValueMap<String, String> cond, Pageable pageable);

    JPAQuery<Long> searchShopConditionCount(MultiValueMap<String, String> cond);
}
