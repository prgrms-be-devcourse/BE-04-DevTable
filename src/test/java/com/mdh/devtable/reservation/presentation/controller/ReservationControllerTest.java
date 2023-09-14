package com.mdh.devtable.reservation.presentation.controller;

import com.mdh.devtable.RestDocsSupport;
import com.mdh.devtable.reservation.application.ReservationService;
import com.mdh.devtable.reservation.presentation.dto.ReservationUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @Test
    @DisplayName("매장의 예약을 수정 할 수 있다.")
    void updateReservationTest() throws Exception {
        //given
        var request = new ReservationUpdateRequest(List.of(1L, 2L), 2);
        var reservationId = 1L;

        doNothing().when(reservationService)
                .updateReservation(reservationId, request);

        //when & then
        mockMvc.perform(patch("/api/customer/v1/reservations/{reservationId}", reservationId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("update-shop-waiting",
                        pathParameters(
                                parameterWithName("reservationId").description("예약 id")
                        ),
                        requestFields(
                                fieldWithPath("shopReservationDateTimeSeatsIds").type(JsonFieldType.ARRAY).description("테이블 좌석 ID들"),
                                fieldWithPath("personCount").type(JsonFieldType.NUMBER).description("예약 인원 수")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 바디(비어있음)"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("변경된 서버 시간")
                        )
                ));
    }

    @Test
    @DisplayName("매장의 예약을 당일에는 수정 할 수 없다.")
    void updateReservationExTest() throws Exception {
        //given
        var request = new ReservationUpdateRequest(List.of(1L, 2L), 2);
        var reservationId = 1L;

        doThrow(new IllegalStateException("예약이 24시간 이내로 남은 경우 예약 수정이 불가능합니다. reservationId : " + reservationId))
                .when(reservationService)
                .updateReservation(reservationId, request);

        //when & then
        mockMvc.perform(patch("/api/customer/v1/reservations/{reservationId}", reservationId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.type").value("about:blank"))
                .andExpect(jsonPath("$.data.title").value("RuntimeException"))
                .andExpect(jsonPath("$.data.status").value(400))
                .andExpect(jsonPath("$.data.detail").value(String.format("예약이 24시간 이내로 남은 경우 예약 수정이 불가능합니다. reservationId : %d", reservationId)))
                .andExpect(jsonPath("$.data.instance").value(String.format("/api/customer/v1/reservations/%d", reservationId)))
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("today-update-shop-waiting-error",
                        pathParameters(
                                parameterWithName("reservationId").description("예약 id")
                        ),
                        requestFields(
                                fieldWithPath("shopReservationDateTimeSeatsIds").type(JsonFieldType.ARRAY).description("테이블 좌석 ID들"),
                                fieldWithPath("personCount").type(JsonFieldType.NUMBER).description("예약 인원 수")
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

    @ParameterizedTest
    @MethodSource("provideReservationUpdateRequestInvalidValue")
    @DisplayName("매장의 예약을 수정 할 때 입력 값이 잘못되면 예외를 반환한다.")
    void inValidReservationUpdateRequestTest(ReservationUpdateRequest request) throws Exception {
        //given
        var reservationId = 2;

        //when & then
        mockMvc.perform(patch("/api/customer/v1/reservations/{reservationId}", reservationId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("MethodArgumentNotValidException"))
                .andDo(document("reservation-update-invalid-value",
                        pathParameters(
                                parameterWithName("reservationId").description("예약 id")
                        ),
                        requestFields(
                                fieldWithPath("shopReservationDateTimeSeatsIds").type(JsonFieldType.ARRAY).description("테이블 좌석 ID들"),
                                fieldWithPath("personCount").type(JsonFieldType.NUMBER).description("예약 인원 수")
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

    static Stream<Arguments> provideReservationUpdateRequestInvalidValue() {
        return Stream.of(
                Arguments.arguments(new ReservationUpdateRequest(List.of(), 0)),
                Arguments.arguments(new ReservationUpdateRequest(List.of(1L), 0)),
                Arguments.arguments(new ReservationUpdateRequest(List.of(), 2))
        );
    }
}