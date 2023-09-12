package com.mdh.devtable.ownerreservation.presentation;

import com.mdh.devtable.RestDocsSupport;
import com.mdh.devtable.ownerreservation.application.OwnerReservationService;
import com.mdh.devtable.ownerreservation.presentation.dto.ShopReservationCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OwnerReservationController.class)
class OwnerReservationControllerTest extends RestDocsSupport {

    @MockBean
    private OwnerReservationService ownerReservationService;

    @Override
    protected Object initController() {
        return new OwnerReservationController(ownerReservationService);
    }

    @DisplayName("점주는 매장 예약을 생성할 수 있다.")
    @Test
    void createShopReservation() throws Exception {
        var shopId = 1L;
        var request = new ShopReservationCreateRequest(5, 1);
        given(ownerReservationService.createShopReservation(any(Long.class), any(ShopReservationCreateRequest.class))).willReturn(1L);

        mockMvc.perform(post("/api/owner/v1/shops/{shopId}/reservation", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", String.format("/api/owner/v1/shops/%d/reservation/%d", shopId, 1L)))
                .andExpect(jsonPath("$.statusCode").value("201"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("owner-shop-reservation-create",
                        pathParameters(
                                parameterWithName("shopId").description("매장 id")
                        ),
                        requestFields(
                                fieldWithPath("maximumPeople").type(JsonFieldType.NUMBER).description("최대 인원 수"),
                                fieldWithPath("minimumPeople").type(JsonFieldType.NUMBER).description("최소 인원 수")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 바디(비어 있음)"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("생성된 서버 시간")
                        ),
                        responseHeaders(
                                headerWithName("Location").description("새로 생성된 예약의 URI")
                        )
                ));
    }

    @DisplayName("점주는 잘못된 예약 정보로 매장 예약을 생성할 수 없다.")
    @Test
    void createShopReservationWithInvalidInput() throws Exception {
        var shopId = 1L;
        var request = new ShopReservationCreateRequest(0, 31);  // Invalid values

        mockMvc.perform(post("/api/owner/v1/shops/{shopId}/reservation", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("MethodArgumentNotValidException"))
                .andDo(document("owner-shop-reservation-create-invalid",
                        pathParameters(
                                parameterWithName("shopId").description("매장 id")
                        ),
                        requestFields(
                                fieldWithPath("maximumPeople").type(JsonFieldType.NUMBER).description("최대 인원 수"),
                                fieldWithPath("minimumPeople").type(JsonFieldType.NUMBER).description("최소 인원 수")
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