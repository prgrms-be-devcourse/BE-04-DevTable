package com.mdh.devtable.menu.application;

import com.mdh.devtable.menu.infra.persistence.MenuRepository;
import com.mdh.devtable.menu.persentation.dto.MenuCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuServiceValidator menuServiceValidator;

    @Transactional
    public void createMenu(MenuCreateRequest menuCreateRequest) {
        menuServiceValidator.validateMenuCreate(menuCreateRequest.categoryId());
        var menu = menuCreateRequest.toEntity();
        menuRepository.save(menu);
    }
}