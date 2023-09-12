package com.mdh.devtable.menu.persentation;

import com.mdh.devtable.RestDocsSupport;
import com.mdh.devtable.menu.application.MenuCategoryService;
import com.mdh.devtable.menu.persentation.dto.MenuCategoryCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(MenuCategoryController.class)
class MenuCategoryControllerTest extends RestDocsSupport {

    @MockBean
    private MenuCategoryService menuCategoryService;

    @Override
    protected Object initController() {
        return new MenuCategoryController(menuCategoryService);
    }

    @DisplayName("점주는 메뉴 카테고리를 생성할 수 있다.")
    @Test
    void createMenuCategory() throws Exception {
        var shopId = 1L;
        var request = new MenuCategoryCreateRequest("testName", "testDescription");

        mockMvc.perform(post("/api/owner/v1/shops/{shopId}/categories", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", String.format("/api/owner/v1/shops/%d/categories", shopId)))
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
}