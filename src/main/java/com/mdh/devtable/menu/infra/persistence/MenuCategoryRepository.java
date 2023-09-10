package com.mdh.devtable.menu.infra.persistence;

import com.mdh.devtable.menu.domain.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {
}