package com.mdh.devtable.reservation.presentation.controller;

import com.mdh.devtable.RestDocsSupport;
import com.mdh.devtable.reservation.application.ReservationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest extends RestDocsSupport {

    @Override
    protected Object initController() {
        return new ReservationController(reservationService);
    }

    @MockBean
    private ReservationService reservationService;

    @Test
    @DisplayName("매장의 예약을 취소 할 수 있다.")
    void cancelReservationTest() throws Exception {
        //given
        var reservationId = 1L;
        var resultValue = "정상적으로 예약이 취소되었습니다.";
        when(reservationService.cancelReservation(reservationId)).thenReturn(resultValue);

        //when & then
        mockMvc.perform(patch("/api/customer/v1/reservations/{reservationId}/cancel", reservationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.data").value(resultValue))
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("change-shop-waiting-status",
                        pathParameters(
                                parameterWithName("reservationId").description("예약 id")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.STRING).description("예약 취소 메시지"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("변경된 서버 시간")
                        )
                ));
    }

    @Test
    @DisplayName("매장의 예약을 당일에 취소하면 패널티가 발생 할 수 있다.")
    void cancelReservationWithPenaltyTest() throws Exception {
        //given
        var reservationId = 1L;
        var resultValue = "당일 취소의 경우 패널티가 발생 할 수 있습니다.";
        when(reservationService.cancelReservation(reservationId)).thenReturn(resultValue);

        //when & then
        mockMvc.perform(patch("/api/customer/v1/reservations/{reservationId}/cancel", reservationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.data").value(resultValue))
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("change-shop-waiting-status",
                        pathParameters(
                                parameterWithName("reservationId").description("예약 id")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.STRING).description("예약 취소 메시지"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("변경된 서버 시간")
                        )
                ));
    }
}