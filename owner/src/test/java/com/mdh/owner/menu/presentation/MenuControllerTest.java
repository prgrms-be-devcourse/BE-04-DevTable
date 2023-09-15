package com.mdh.owner.menu.presentation;

import com.mdh.owner.RestDocsSupport;
import com.mdh.owner.menu.application.MenuService;
import com.mdh.common.menu.domain.MealType;
import com.mdh.common.menu.domain.MenuType;
import com.mdh.owner.menu.presentation.dto.MenuCategoryCreateRequest;
import com.mdh.owner.menu.presentation.dto.MenuCategoryUpdateRequest;
import com.mdh.owner.menu.presentation.dto.MenuCreateRequest;
import com.mdh.owner.menu.presentation.dto.MenuUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuController.class)
class MenuControllerTest extends RestDocsSupport {

    @MockBean
    private MenuService menuService;

    @Override
    protected Object initController() {
        return new MenuController(menuService);
    }


    @DisplayName("점주는 메뉴 카테고리를 생성할 수 있다.")
    @Test
    void createMenuCategory() throws Exception {
        var shopId = 1L;
        var request = new MenuCategoryCreateRequest("testName", "testDescription");
        given(menuService.createMenuCategory(any(Long.class), any(MenuCategoryCreateRequest.class))).willReturn(1L);
        var menuCategoryId = menuService.createMenuCategory(shopId, request);

        mockMvc.perform(post("/api/owner/v1/shops/{shopId}/categories", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", String.format("/api/owner/v1/shops/%d/categories/%d", shopId, menuCategoryId)))
                .andExpect(jsonPath("$.statusCode").value("201"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("owner-menu-category-create",
                        pathParameters(
                                parameterWithName("shopId").description("매장 id")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("메뉴 카테고리 이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("메뉴 카테고리 설명")
                        ),
                        responseFields(fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응다 바디(비어 있음)"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("생성된 서버 시간")
                        ),
                        responseHeaders(
                                headerWithName("Location").description("새로 생성된 메뉴 카테고리의 URI")
                        )
                ));
    }

    @DisplayName("점주는 메뉴 카테고리를 잘못 생성할 수 없다.")
    @Test
    void createMenuCategoryThrowException() throws Exception {
        var shopId = 1L;
        var request = new MenuCategoryCreateRequest("", "testDescriptiontestDescriptiontestDescriptiontestDescriptiontestDescriptiontestDescriptiontestDescription");

        mockMvc.perform(post("/api/owner/v1/shops/{shopId}/categories", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("MethodArgumentNotValidException"))
                .andDo(document("owner-menu-category-create-invalid",
                        pathParameters(
                                parameterWithName("shopId").description("매장 id")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("메뉴 카테고리 이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("메뉴 카테고리 설명")
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

    @DisplayName("점주는 메뉴를 생성할 수 있다.")
    @Test
    void createMenu() throws Exception {
        //given
        var categoryId = 1L;
        var request = new MenuCreateRequest(
                "Spaghetti",
                15000,
                "Delicious spaghetti with tomato sauce",
                "Popular",
                MenuType.MAIN,
                MealType.DINNER
        );
        given(menuService.createMenu(any(Long.class), any(MenuCreateRequest.class))).willReturn(1L);
        var menuId = menuService.createMenu(categoryId, request);

        //when & then
        mockMvc.perform(post("/api/owner/v1/categories/{categoryId}/menus", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", String.format("/api/owner/v1/categories/%d/menus/%d", categoryId, menuId)))
                .andExpect(jsonPath("$.statusCode").value("201"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("owner-menu-create",
                        pathParameters(
                                parameterWithName("categoryId").description("카테고리 id")
                        ),
                        requestFields(
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
                        ),
                        responseHeaders(
                                headerWithName("Location").description("새로 생성된 메뉴의 URI")
                        )
                ));
    }

    @DisplayName("점주는 잘못된 메뉴 정보로 메뉴를 생성할 수 없다.")
    @Test
    void createMenuWithInvalidInput() throws Exception {
        // given
        var categoryId = 1L;

        var request = new MenuCreateRequest(
                "",
                -1,
                "This description is way too long to fit into the database and should trigger a validation error",
                "This label is too long",
                null,
                null
        );

        // when & then
        mockMvc.perform(post("/api/owner/v1/categories/{categoryId}/menus", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("MethodArgumentNotValidException"))
                .andDo(document("owner-menu-create-invalid",
                        pathParameters(
                                parameterWithName("categoryId").description("카테고리 id")
                        ),
                        requestFields(
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
        // given
        var invalidCategoryId = -1L;  // Invalid category ID
        var request = new MenuCreateRequest(
                "Spaghetti",
                15000,
                "Delicious spaghetti with tomato sauce",
                "Popular",
                MenuType.MAIN,
                MealType.DINNER
        );
        doThrow(new NoSuchElementException("등록된 카테고리 ID가 없습니다."))
                .when(menuService).createMenu(any(Long.class), any(MenuCreateRequest.class));


        // when & then
        mockMvc.perform(post("/api/owner/v1/categories/{categoryId}/menus", invalidCategoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("NoSuchElementException"))
                .andDo(document("owner-menu-create-invalid-category",
                                pathParameters(
                                        parameterWithName("categoryId").description("카테고리 id")
                                ),
                                requestFields(
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

    @DisplayName("점주는 메뉴 카테고리를 업데이트할 수 있다.")
    @Test
    void updateMenuCategory() throws Exception {
        var categoryId = 1L;
        var request = new MenuCategoryUpdateRequest("Updated Main Course", "Updated description");

        mockMvc.perform(patch("/api/owner/v1/shops/categories/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("owner-menu-category-update",
                        pathParameters(
                                parameterWithName("categoryId").description("카테고리 id")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("메뉴 카테고리 이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("메뉴 카테고리 설명")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 바디(비어 있음)"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("생성된 서버 시간")
                        )
                ));
    }

    @DisplayName("점주는 잘못된 메뉴 카테고리 정보로 업데이트할 수 없다.")
    @Test
    void updateMenuCategoryWithInvalidInput() throws Exception {
        var categoryId = 1L;
        var request = new MenuCategoryUpdateRequest("", "This description is way too long to fit into the database and should trigger a validation error");

        mockMvc.perform(patch("/api/owner/v1/shops/categories/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("MethodArgumentNotValidException"))
                .andDo(document("owner-menu-category-update-invalid",
                        pathParameters(
                                parameterWithName("categoryId").description("카테고리 id")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("메뉴 카테고리 이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("메뉴 카테고리 설명")
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

    @DisplayName("점주는 매장의 메뉴 카테고리를 삭제할 수 있다.")
    @Test
    void deleteMenuCategory() throws Exception {

        var categoryId = 1L;
        var request = new MenuCategoryUpdateRequest("Delete Main Course", "Delete description");

        mockMvc.perform(delete("/api/owner/v1/shops/categories/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.statusCode").value("204"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("owner-menu-category-delete",
                        pathParameters(
                                parameterWithName("categoryId").description("카테고리 id")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("메뉴 카테고리 이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("메뉴 카테고리 설명")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 바디(비어 있음)"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("생성된 서버 시간")
                        )
                ));
    }

    @DisplayName("점주는 메뉴를 업데이트할 수 있다.")
    @Test
    void updateMenu() throws Exception {
        var menuId = 1L;
        var request = new MenuUpdateRequest(
                "Updated Spaghetti",
                16000,
                "Updated delicious spaghetti with tomato sauce",
                "Updated Popular",
                MenuType.MAIN,
                MealType.DINNER
        );

        mockMvc.perform(patch("/api/owner/v1/menus/{menuId}", menuId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("owner-menu-update",
                        pathParameters(
                                parameterWithName("menuId").description("메뉴 id")
                        ),
                        requestFields(
                                fieldWithPath("menuName").type(JsonFieldType.STRING).description("메뉴 이름"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("가격"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("설명"),
                                fieldWithPath("label").type(JsonFieldType.STRING).description("라벨"),
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

    @DisplayName("점주는 잘못된 메뉴 정보로 업데이트할 수 없다.")
    @Test
    void updateMenuWithInvalidInput() throws Exception {
        var menuId = 1L;
        var request = new MenuUpdateRequest(
                "",
                -1,
                "This description is way too long to fit into the database and should trigger a validation error",
                "Invalid Label",
                null,
                null
        );

        mockMvc.perform(patch("/api/owner/v1/menus/{menuId}", menuId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("MethodArgumentNotValidException"))
                .andDo(document("owner-menu-update-invalid",
                        pathParameters(
                                parameterWithName("menuId").description("메뉴 id")
                        ),
                        requestFields(
                                fieldWithPath("menuName").type(JsonFieldType.STRING).description("메뉴 이름"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("가격"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("설명"),
                                fieldWithPath("label").type(JsonFieldType.STRING).description("라벨"),
                                fieldWithPath("menuType").type(JsonFieldType.NULL).description("메뉴 타입"),
                                fieldWithPath("mealType").type(JsonFieldType.NULL).description("식사 타입")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("타이틀"),
                                fieldWithPath("data.status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data.detail").type(JsonFieldType.STRING).description("상세 설명"),
                                fieldWithPath("data.instance").type(JsonFieldType.STRING).description("인스턴스 URI"),
                                fieldWithPath("data.type").type(JsonFieldType.STRING).description("타입"),
                                fieldWithPath("data.validationError[].field").type(JsonFieldType.STRING).description("유효성 검사 실패 필드"),
                                fieldWithPath("data.validationError[].message").type(JsonFieldType.STRING).description("유효성 검사 실패 메시지"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("서버 시간")
                        )
                ));

    }
}
