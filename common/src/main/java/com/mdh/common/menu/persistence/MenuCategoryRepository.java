package com.mdh.common.menu.persistence;

import com.mdh.common.menu.domain.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {

    @Query("SELECT distinct mc FROM MenuCategory mc LEFT JOIN FETCH mc.menus WHERE mc.shopId = :shopId")
    List<MenuCategory> findAllByShopIdWithFetch(Long shopId);
}