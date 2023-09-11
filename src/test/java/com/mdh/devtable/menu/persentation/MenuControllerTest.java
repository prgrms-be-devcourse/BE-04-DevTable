package com.mdh.devtable.menu.persentation;

import com.mdh.devtable.RestDocsSupport;
import com.mdh.devtable.menu.application.MenuService;
import com.mdh.devtable.menu.domain.MealType;
import com.mdh.devtable.menu.domain.MenuType;
import com.mdh.devtable.menu.persentation.dto.MenuCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuController.class)
class MenuControllerTest extends RestDocsSupport {

    @MockBean
    private MenuService menuService;

    @Override
    protected Object initController() {
        return new MenuController(menuService);
    }

    @DisplayName("점주는 메뉴를 생성할 수 있다.")
    @Test
    void createMenu() throws Exception {
        //given
        var request = new MenuCreateRequest(
                1L,
                "Spaghetti",
                15000,
                "Delicious spaghetti with tomato sauce",
                "Popular",
                MenuType.MAIN,
                MealType.DINNER
        );

        //when & then
        mockMvc.perform(post("/api/owner/v1/shops/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value("201"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("owner-menu-create",
                        requestFields(
                                fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("카테고리 ID"),
                                fieldWithPath("menuName").type(JsonFieldType.STRING).description("메뉴 이름"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("가격"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("설명"),
                                fieldWithPath("label").type(JsonFieldType.STRING).description("라벨").optional(),
                                fieldWithPath("menuType").type(JsonFieldType.STRING).description("메뉴 타입"),
                                fieldWithPath("mealType").type(JsonFieldType.STRING).description("식사 타입")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 바디(비어 있음)"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("생성된 서버 시간")
                        )
                ));
    }

    @DisplayName("점주는 잘못된 메뉴 정보로 메뉴를 생성할 수 없다.")
    @Test
    void createMenuWithInvalidInput() throws Exception {
        // given
        var request = new MenuCreateRequest(
                1L,
                "",
                -1,
                "This description is way too long to fit into the database and should trigger a validation error",
                "This label is too long",
                null,
                null
        );

        // when & then
        mockMvc.perform(post("/api/owner/v1/shops/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("MethodArgumentNotValidException"))
                .andDo(document("owner-menu-create-invalid",
                        requestFields(
                                fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("카테고리 ID"),
                                fieldWithPath("menuName").type(JsonFieldType.STRING).description("메뉴 이름"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("가격"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("설명"),
                                fieldWithPath("label").type(JsonFieldType.STRING).description("라벨").optional(),
                                fieldWithPath("menuType").type(JsonFieldType.NULL).description("메뉴 타입"),
                                fieldWithPath("mealType").type(JsonFieldType.NULL).description("식사 타입")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data.type").type(JsonFieldType.STRING).description("타입"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("타이틀"),
                                fieldWithPath("data.status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data.detail").type(JsonFieldType.STRING).description("상세 설명"),
                                fieldWithPath("data.instance").type(JsonFieldType.STRING).description("인스턴스 URI"),
                                fieldWithPath("data.validationError[].field").type(JsonFieldType.STRING).description("유효성 검사 실패 필드"),
                                fieldWithPath("data.validationError[].message").type(JsonFieldType.STRING).description("유효성 검사 실패 메시지"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("서버 시간")
                        )
                ));
    }

    @DisplayName("점주는 잘못된 카테고리 ID로 메뉴를 생성할 수 없다.")
    @Test
    void createMenuWithInvalidCategoryId() throws Exception {
        // Given an invalid category ID
        var invalidCategoryId = -1L;  // Invalid category ID
        var request = new MenuCreateRequest(
                invalidCategoryId,
                "Spaghetti",
                15000,
                "Delicious spaghetti with tomato sauce",
                "Popular",
                MenuType.MAIN,
                MealType.DINNER
        );
        doThrow(new NoSuchElementException("등록된 카테고리 ID가 없습니다."))
                .when(menuService).createMenu(any(MenuCreateRequest.class));


        // When & Then
        mockMvc.perform(post("/api/owner/v1/shops/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("NoSuchElementException"))
                .andDo(document("owner-menu-create-invalid-category",
                                requestFields(
                                        fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("카테고리 ID"),
                                        fieldWithPath("menuName").type(JsonFieldType.STRING).description("메뉴 이름"),
                                        fieldWithPath("price").type(JsonFieldType.NUMBER).description("가격"),
                                        fieldWithPath("description").type(JsonFieldType.STRING).description("설명"),
                                        fieldWithPath("label").type(JsonFieldType.STRING).description("라벨").optional(),
                                        fieldWithPath("menuType").type(JsonFieldType.STRING).description("메뉴 타입"),
                                        fieldWithPath("mealType").type(JsonFieldType.STRING).description("식사 타입")),
                                responseFields(
                                        fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                        fieldWithPath("data.type").type(JsonFieldType.STRING).description("타입"),
                                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("타이틀"),
                                        fieldWithPath("data.status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                        fieldWithPath("data.detail").type(JsonFieldType.STRING).description("상세 설명"),
                                        fieldWithPath("data.instance").type(JsonFieldType.STRING).description("인스턴스 URI"),
                                        fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("서버 시간")
                                )
                        )
                );
    }


}