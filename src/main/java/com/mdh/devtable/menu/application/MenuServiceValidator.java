package com.mdh.devtable.menu.application;

import com.mdh.devtable.menu.infra.persistence.MenuCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Component
public class MenuServiceValidator {

    private final MenuCategoryRepository menuCategoryRepository;

    @Transactional(readOnly = true)
    public void validateMenuCreate(Long categoryId) {
        menuCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException("등록된 카테고리 ID가 없습니다."));
    }
}