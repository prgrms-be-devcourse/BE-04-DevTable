package com.mdh.devtable.menu.application;

import com.mdh.devtable.DataInitializerFactory;
import com.mdh.devtable.menu.domain.MenuCategory;
import com.mdh.devtable.menu.infra.persistence.MenuCategoryRepository;
import com.mdh.devtable.menu.persentation.dto.MenuCategoryCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MenuCategoryServiceTest {

    @InjectMocks
    private MenuCategoryService menuCategoryService;

    @Mock
    private MenuCategoryRepository menuCategoryRepository;

    @DisplayName("점주는 메뉴 카테고리를 저장할 수 있다.")
    @Test
    void createMenuCategory() {
        //given
        var shopId = 1L;
        var menuCategory = DataInitializerFactory.menuCategory(shopId);
        var request = new MenuCategoryCreateRequest("Main Course", "Delicious main courses");
        given(menuCategoryRepository.save(any(MenuCategory.class))).willReturn(menuCategory);

        //when
        menuCategoryService.createMenuCategory(shopId, request);

        //then
        verify(menuCategoryRepository, times(1)).save(any(MenuCategory.class));
    }
}