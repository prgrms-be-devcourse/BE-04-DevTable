package com.mdh.devtable.menu.infra.persistence;

import com.mdh.devtable.menu.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}