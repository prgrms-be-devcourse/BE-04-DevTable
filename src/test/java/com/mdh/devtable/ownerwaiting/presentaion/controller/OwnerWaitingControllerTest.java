package com.mdh.devtable.ownerwaiting.presentaion.controller;

import com.mdh.devtable.RestDocsSupport;
import com.mdh.devtable.ownerwaiting.application.OwnerWaitingService;
import com.mdh.devtable.ownerwaiting.application.dto.WaitingInfoResponseForOwner;
import com.mdh.devtable.ownerwaiting.presentaion.dto.OwnerShopWaitingStatusChangeRequest;
import com.mdh.devtable.ownerwaiting.presentaion.dto.OwnerUpdateShopWaitingInfoRequest;
import com.mdh.devtable.ownerwaiting.presentaion.dto.OwnerWaitingStatusChangeRequest;
import com.mdh.devtable.waiting.domain.ShopWaitingStatus;
import com.mdh.devtable.waiting.domain.WaitingStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.Collections;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OwnerWaitingController.class)
class OwnerWaitingControllerTest extends RestDocsSupport {

    @Override
    protected Object initController() {
        return new OwnerWaitingController(ownerWaitingService);
    }

    @MockBean
    private OwnerWaitingService ownerWaitingService;

    @DisplayName("매장의 웨이팅 상태를 변경할 수 있다.")
    @Test
    void changShopWaitingStatus() throws Exception {
        //given
        var shopId = 1L;
        var request = new OwnerShopWaitingStatusChangeRequest(ShopWaitingStatus.OPEN);
        doNothing().when(ownerWaitingService).changeShopWaitingStatus(shopId, request);

        //when & then
        mockMvc.perform(patch("/api/owner/v1/shops/{shopId}", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("change-shop-waiting-status",
                        pathParameters(
                                parameterWithName("shopId").description("매장 id")
                        ),
                        requestFields(
                                fieldWithPath("shopWaitingStatus").type(JsonFieldType.STRING).description("매장 상태")
                        ),
                        responseFields(fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 바디(비어있음)"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("변경된 서버 시간")
                        )
                ));
    }

    @DisplayName("매장의 웨이팅 상태를 잘못된 형태로 변경할 수 없다.")
    @Test
    void changShopWaitingStatusThrowException() throws Exception {
        //given
        var shopId = 1L;
        var request = new HashMap<String, String>();
        request.put("shopWaitingStatus", "asf");

        //when & then
        mockMvc.perform(patch("/api/owner/v1/shops/{shopId}", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("HttpMessageNotReadableException"))
                .andDo(document("change-shop-waiting-status-invalid",
                        pathParameters(
                                parameterWithName("shopId").description("매장 id")
                        ),
                        requestFields(
                                fieldWithPath("shopWaitingStatus").type(JsonFieldType.STRING).description("매장 상태")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data.type").type(JsonFieldType.STRING).description("타입"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("타이틀"),
                                fieldWithPath("data.status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data.detail").type(JsonFieldType.STRING).description("상세 설명"),
                                fieldWithPath("data.instance").type(JsonFieldType.STRING).description("인스턴스 URI"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("서버 시간")
                        )
                ));
    }

    @DisplayName("매장이 갖고 있는 손님의 웨이팅 상태를 변경할 수 있다.")
    @Test
    void changWaitingStatus() throws Exception {
        //given
        var waitingId = 1L;
        var request = new OwnerWaitingStatusChangeRequest(WaitingStatus.VISITED);
        doNothing().when(ownerWaitingService).changeWaitingStatus(waitingId, request);

        //when & then
        mockMvc.perform(patch("/api/owner/v1/waitings/{waitingId}", waitingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("change-waiting-status",
                        pathParameters(
                                parameterWithName("waitingId").description("웨이팅 id")
                        ),
                        requestFields(
                                fieldWithPath("waitingStatus").type(JsonFieldType.STRING).description("웨이팅 상태")
                        ),
                        responseFields(fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 바디(비어있음)"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("서버 시간")
                        )
                ));
    }

    @DisplayName("매장이 갖고 있는 손님의 웨이팅 상태를 잘못된 형태로 변경할 수 없다.")
    @Test
    void changWaitingStatusThrowException() throws Exception {
        //given
        var waitingId = 1L;
        var request = new HashMap<String, String>();
        request.put("waitingStatus", "asf");

        //when & then
        mockMvc.perform(patch("/api/owner/v1/waitings/{waitingId}", waitingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("HttpMessageNotReadableException"))
                .andDo(document("change-waiting-status-invalid",
                        pathParameters(
                                parameterWithName("waitingId").description("웨이팅 id")
                        ),
                        requestFields(
                                fieldWithPath("waitingStatus").type(JsonFieldType.STRING).description("웨이팅 상태")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data.type").type(JsonFieldType.STRING).description("타입"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("타이틀"),
                                fieldWithPath("data.status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data.detail").type(JsonFieldType.STRING).description("상세 설명"),
                                fieldWithPath("data.instance").type(JsonFieldType.STRING).description("인스턴스 URI"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("서버 시간")
                        )
                ));
    }

    @DisplayName("점주가 갖고 있는 매장의 웨이팅 정보를 웨이팅 상태를 입력 받아 조회할 수 있다.")
    @Test
    void findWaitingByOwnerIdAndWaitingStatus() throws Exception {
        //given
        var ownerId = 1L;
        var waitingNumber = 1;
        var phoneNumber = "0101234578";
        var status = WaitingStatus.PROGRESS;
        var response = Collections.singletonList(new WaitingInfoResponseForOwner(waitingNumber, phoneNumber));
        when(ownerWaitingService.findWaitingOwnerIdAndWaitingStatus(any(Long.class), any(WaitingStatus.class))).thenReturn(response);

        //when & then
        mockMvc.perform(get("/api/owner/v1/waitings/{ownerId}", ownerId)
                        .param("status", status.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.data[0].waitingNumber").value(waitingNumber))
                .andExpect(jsonPath("$.data[0].phoneNumber").value(phoneNumber))
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("owners-shop-waiting-info",
                        queryParameters(
                                parameterWithName("status").description("예약 상태")
                        ),
                        pathParameters(
                                parameterWithName("ownerId").description("매장 주인의 id")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.ARRAY).description("응답 바디"),
                                fieldWithPath("data[].waitingNumber").type(JsonFieldType.NUMBER).description("웨이팅 번호"),
                                fieldWithPath("data[].phoneNumber").type(JsonFieldType.STRING).description("전화번호"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("서버 시간")
                        )
                ));
    }

    @DisplayName("점주가 갖고 있는 매장의 웨이팅 정보를 웨이팅 상태를 입력 받아 조회할 수 없다.(웨이팅 상태를 잘못 입력 했을 때)")
    @Test
    void findWaitingByShopIdAndWaitingStatusThrowsException() throws Exception {
        //given
        var ownerId = 1L;
        var invalidStatus = "INVALID_STATUS";

        //when & then
        mockMvc.perform(get("/api/owner/v1/waitings/{ownerId}", ownerId)
                        .param("status", invalidStatus)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("MethodArgumentTypeMismatchException"))
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("owners-shop-waiting-info-invalid",
                        queryParameters(
                                parameterWithName("status").description("예약 상태")
                        ),
                        pathParameters(
                                parameterWithName("ownerId").description("매장 주인의 id")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data.type").type(JsonFieldType.STRING).description("타입"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("타이틀"),
                                fieldWithPath("data.status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data.detail").type(JsonFieldType.STRING).description("상세 설명"),
                                fieldWithPath("data.instance").type(JsonFieldType.STRING).description("인스턴스 URI"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("서버 시간")
                        )
                ));
    }

    @DisplayName("점주는 매장의 웨이팅 정보를 변경할 수 있다.")
    @Test
    void updateShopWaitingInfo() throws Exception {
        var shopId = 1L;
        var ownerUpdateShopWaitingInfoRequest = new OwnerUpdateShopWaitingInfoRequest(true,
                1,
                2,
                3);

        mockMvc.perform(patch("/api/owner/v1/shops/{shopId}/waiting", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ownerUpdateShopWaitingInfoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("owner-change-shop-waiting-info",
                        pathParameters(
                                parameterWithName("shopId").description("매장 id")
                        ),
                        requestFields(
                                fieldWithPath("childEnabled").type(JsonFieldType.BOOLEAN).description("유아 가능 여부"),
                                fieldWithPath("maximumPeople").type(JsonFieldType.NUMBER).description("최대 인원 수"),
                                fieldWithPath("minimumPeople").type(JsonFieldType.NUMBER).description("최소 인원 수"),
                                fieldWithPath("maximumWaitingTeam").type(JsonFieldType.NUMBER).description("최대 웨이팅 팀 수")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 바디(비어있음)"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("서버 시간")
                        )
                ));
    }

    @DisplayName("점주는 매장의 웨이팅 정보를 잘못된 형태로 변경할 수 없다.")
    @Test
    void updateShopWaitingInfoThrowException() throws Exception {
        var shopId = 1L;
        var ownerUpdateShopWaitingInfoRequest = new OwnerUpdateShopWaitingInfoRequest(true,
                -1,
                -2,
                -3);

        mockMvc.perform(patch("/api/owner/v1/shops/{shopId}/waiting", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ownerUpdateShopWaitingInfoRequest)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("owner-change-shop-waiting-info-error",
                        pathParameters(
                                parameterWithName("shopId").description("매장 id")
                        ),
                        requestFields(
                                fieldWithPath("childEnabled").type(JsonFieldType.BOOLEAN).description("유아 가능 여부"),
                                fieldWithPath("maximumPeople").type(JsonFieldType.NUMBER).description("최대 인원 수"),
                                fieldWithPath("minimumPeople").type(JsonFieldType.NUMBER).description("최소 인원 수"),
                                fieldWithPath("maximumWaitingTeam").type(JsonFieldType.NUMBER).description("최대 웨이팅 팀 수")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data.type").type(JsonFieldType.STRING).description("타입"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("타이틀"),
                                fieldWithPath("data.status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data.detail").type(JsonFieldType.STRING).description("상세 설명"),
                                fieldWithPath("data.instance").type(JsonFieldType.STRING).description("인스턴스 URI"),
                                fieldWithPath("data.validationError[]").type(JsonFieldType.ARRAY).description("유효성 검사 오류 목록"),
                                fieldWithPath("data.validationError[].field").type(JsonFieldType.STRING).description("유효성 검사에 실패한 필드"),
                                fieldWithPath("data.validationError[].message").type(JsonFieldType.STRING).description("유효성 검사 실패 메시지"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("서버 시간")
                        )
                ));
    }
}