package com.mdh.devtable.menu.application;

import com.mdh.devtable.menu.infra.persistence.MenuCategoryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class MenuServiceValidatorTest {

    @InjectMocks
    private MenuServiceValidator menuServiceValidator;

    @Mock
    private MenuCategoryRepository menuCategoryRepository;

    @DisplayName("등록되지 않은 카테고리 ID로 검증을 시도하면 예외가 발생한다.")
    @Test
    void validateMenuCreateWithInvalidCategoryId() {
        // Given
        var invalidCategoryId = -1L;
        given(menuCategoryRepository.findById(invalidCategoryId)).willReturn(Optional.empty());

        // When & Then
        Assertions.assertThatThrownBy(() -> menuServiceValidator.validateMenuCreate(invalidCategoryId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("등록된 카테고리 ID가 없습니다." + invalidCategoryId);
    }
}