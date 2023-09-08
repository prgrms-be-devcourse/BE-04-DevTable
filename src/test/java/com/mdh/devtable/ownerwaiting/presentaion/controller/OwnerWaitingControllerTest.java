package com.mdh.devtable.ownerwaiting.presentaion.controller;

import com.mdh.devtable.RestDocsSupport;
import com.mdh.devtable.ownerwaiting.application.OwnerWaitingService;
import com.mdh.devtable.ownerwaiting.presentaion.dto.OwnerShopWaitingStatusChangeRequest;
import com.mdh.devtable.ownerwaiting.presentaion.dto.OwnerWaitingStatusChangeRequest;
import com.mdh.devtable.waiting.domain.ShopWaitingStatus;
import com.mdh.devtable.waiting.domain.WaitingStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.HashMap;

import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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
        mockMvc.perform(patch("/api/owner/v1/shops/" + shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("owner-shops-waiting",
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
        mockMvc.perform(patch("/api/owner/v1/shops/" + shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("HttpMessageNotReadableException"))
                .andDo(document("user-sign-up-invalid-password",
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

    @DisplayName("매장의 웨이팅 상태를 변경할 수 있다.")
    @Test
    void changWaitingStatus() throws Exception {
        //given
        var waitingId = 1L;
        var request = new OwnerWaitingStatusChangeRequest(WaitingStatus.VISITED);
        doNothing().when(ownerWaitingService).changeWaitingStatus(waitingId, request);

        //when & then
        mockMvc.perform(patch("/api/owner/v1/waitings/" + waitingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("owner-shops-waiting",
                        requestFields(
                                fieldWithPath("waitingStatus").type(JsonFieldType.STRING).description("웨이팅 상태")
                        ),
                        responseFields(fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 바디(비어있음)"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("변경된 서버 시간")
                        )
                ));
    }

    @DisplayName("매장의 웨이팅 상태를 잘못된 형태로 변경할 수 없다.")
    @Test
    void changWaitingStatusThrowException() throws Exception {
        //given
        var waitingId = 1L;
        var request = new HashMap<String, String>();
        request.put("waitingStatus", "asf");

        //when & then
        mockMvc.perform(patch("/api/owner/v1/waitings/" + waitingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("HttpMessageNotReadableException"))
                .andDo(document("user-sign-up-invalid-password",
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

}