package com.mdh.user.reservation.presentation.controller;

import com.mdh.user.RestDocsSupport;
import com.mdh.common.reservation.domain.ReservationStatus;
import com.mdh.user.reservation.application.ReservationService;
import com.mdh.user.reservation.application.dto.ReservationResponse;
import com.mdh.user.reservation.application.dto.ReservationResponses;
import com.mdh.common.reservation.persistence.dto.ReservationQueryDto;
import com.mdh.user.reservation.presentation.dto.ReservationCancelRequest;
import com.mdh.user.reservation.presentation.dto.ReservationPreemptiveRequest;
import com.mdh.user.reservation.presentation.dto.ReservationRegisterRequest;
import com.mdh.user.reservation.presentation.dto.ReservationUpdateRequest;
import com.mdh.common.shop.domain.ShopType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest extends RestDocsSupport {

    @Override
    protected Object initController() {
        return new ReservationController(reservationService);
    }

    @MockBean
    private ReservationService reservationService;

    @Test
    @DisplayName("예약 좌석을 선점할 수 있다.")
    void preemptReservation() throws Exception {
        //given
        var reservationId = UUID.randomUUID();
        var request = new ReservationPreemptiveRequest(1L, List.of(1L, 2L), "요구사항 입니다.", 4);
        when(reservationService.preemtiveReservation(request)).thenReturn(reservationId);

        //when & then
        mockMvc.perform(post("/api/customer/v1/reservations/preemption")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.data").value(String.valueOf(reservationId)))
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("s",
                        requestFields(
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저 아이디"),
                                fieldWithPath("shopReservationDateTimeSeatIds").type(JsonFieldType.ARRAY).description("선점하려는 좌석들"),
                                fieldWithPath("requirement").type(JsonFieldType.STRING).description("요구사항"),
                                fieldWithPath("personCount").type(JsonFieldType.NUMBER).description("예약 인원")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.STRING).description("선점된 예약 아이디"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("서버 응답 시간")
                        )
                ));
    }

    @Test
    @DisplayName("선점하던 예약을 확정할 수 있다.")
    void registerReservation() throws Exception {
        //given
        var reservationId = UUID.randomUUID();
        var registerdReservationId = 1L;
        var request = new ReservationRegisterRequest(1L, List.of(1L, 2L), 4);
        when(reservationService.registerReservation(reservationId, request)).thenReturn(registerdReservationId);

        //when & then
        mockMvc.perform(post("/api/customer/v1/reservations/{reservationId}/register", reservationId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", String.format("/api/customer/v1/reservations/%d", registerdReservationId)))
                .andExpect(jsonPath("$.statusCode").value("201"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("change-shop-waiting-status",
                        pathParameters(
                                parameterWithName("reservationId").description("예약 id")
                        ),
                        requestFields(
                                fieldWithPath("shopId").type(JsonFieldType.NUMBER).description("유저 아이디"),
                                fieldWithPath("shopReservationDateTimeSeatIds").type(JsonFieldType.ARRAY).description("선점하려는 좌석들"),
                                fieldWithPath("totalSeatCount").type(JsonFieldType.NUMBER).description("선점하려는 좌석들의 수")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 바디(비어 있음)"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("서버 응답 시간")
                        ),
                        responseHeaders(
                                headerWithName("Location").description("새로 생성된 예약의 URI")
                        )
                ));
    }

    @Test
    @DisplayName("선점하던 예약을 취소할 수 있다.")
    void cancelPreemptiveReservation() throws Exception {
        //given
        var reservationId = UUID.randomUUID();
        var cancelMessage = "성공적으로 선점된 예약을 취소했습니다.";
        var request = new ReservationCancelRequest(List.of(1L, 2L));
        given(reservationService.cancelPreemptiveReservation(reservationId, request)).willReturn(cancelMessage);

        //when & then
        mockMvc.perform(post("/api/customer/v1/reservations/preemption/{reservationId}/cancel", reservationId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.data").value(cancelMessage))
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("change-shop-waiting-status",
                        pathParameters(
                                parameterWithName("reservationId").description("예약 id")
                        ),
                        requestFields(
                                fieldWithPath("shopReservationDateTimeSeatIds").type(JsonFieldType.ARRAY).description("선점 취소하려는 좌석들")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.STRING).description("선점 취소 성공 메시지"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("서버 응답 시간")
                        )
                ));
    }

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

    @Test
    @DisplayName("유저가 이전에 예약했던 정보들을 상태 별로 조회 할 수 있다.")
    void findAllReservationsUserIdAndStatusTest() throws Exception {
        //given
        var shopId = 1L;
        var userId = 1L;
        var name = "shopName";
        var shopType = ShopType.ASIAN;
        var city = "city";
        var district = "district";
        var reservationDate = LocalDate.now();
        var formattedReservationDate = reservationDate.format(DateTimeFormatter.ISO_DATE);

        var reservationTime = LocalTime.now();
        var formattedReservationTime = reservationTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        var personCount = 1;
        var reservationStatus = ReservationStatus.VISITED;

        var reservationQueryDto = new ReservationQueryDto(
                shopId,
                name,
                shopType,
                city,
                district,
                reservationDate,
                reservationTime,
                personCount,
                reservationStatus
        );

        var reservationResponse = new ReservationResponse(reservationQueryDto);
        var reservationResponses = new ReservationResponses(List.of(reservationResponse));

        when(reservationService.findAllReservations(any(Long.class), any(ReservationStatus.class)))
                .thenReturn(reservationResponses);

        //when & then
        mockMvc.perform(get("/api/customer/v1/reservations/me/{userId}", userId)
                        .param("status", reservationStatus.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.data.reservations[0].shop.id").value(shopId))
                .andExpect(jsonPath("$.data.reservations[0].shop.name").value(name))
                .andExpect(jsonPath("$.data.reservations[0].shop.shopType").value(shopType.name()))
                .andExpect(jsonPath("$.data.reservations[0].shop.region").value(city + " " + district))
                .andExpect(jsonPath("$.data.reservations[0].reservation.reservationDate").value(formattedReservationDate))
                .andExpect(jsonPath("$.data.reservations[0].reservation.reservationTime").value(formattedReservationTime))
                .andExpect(jsonPath("$.data.reservations[0].reservation.personCount").value(personCount))
                .andExpect(jsonPath("$.data.reservations[0].reservation.reservationStatus").value(reservationStatus.name()))
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("find-all-reservations-userId",
                        pathParameters(
                                parameterWithName("userId").description("고객 ID")
                        ),
                        queryParameters(
                                parameterWithName("status").description("예약 상태")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                subsectionWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("서버 시간"),

                                // "data" 하위 필드에 대한 문서화 시작
                                subsectionWithPath("data.reservations").type(JsonFieldType.ARRAY).description("예약 목록"),

                                // "data.reservations" 배열 요소의 문서화 시작
                                subsectionWithPath("data.reservations[].shop").type(JsonFieldType.OBJECT).description("가게 정보"),
                                subsectionWithPath("data.reservations[].reservation").type(JsonFieldType.OBJECT).description("예약 정보"),

                                // "data.reservations[].shop" 객체의 문서화
                                fieldWithPath("data.reservations[].shop.id").type(JsonFieldType.NUMBER).description("가게 ID"),
                                fieldWithPath("data.reservations[].shop.name").type(JsonFieldType.STRING).description("가게 이름"),
                                fieldWithPath("data.reservations[].shop.shopType").type(JsonFieldType.STRING).description("가게 유형"),
                                fieldWithPath("data.reservations[].shop.region").type(JsonFieldType.STRING).description("가게 지역"),

                                // "data.reservations[].reservation" 객체의 문서화
                                fieldWithPath("data.reservations[].reservation.reservationDate").type(JsonFieldType.STRING).description("예약 날짜"),
                                fieldWithPath("data.reservations[].reservation.reservationTime").type(JsonFieldType.STRING).description("예약 시간"),
                                fieldWithPath("data.reservations[].reservation.personCount").type(JsonFieldType.NUMBER).description("인원 수"),
                                fieldWithPath("data.reservations[].reservation.reservationStatus").type(JsonFieldType.STRING).description("예약 상태")
                        )
                ));
    }

    @Test
    @DisplayName("유저가 이전에 예약했던 정보들을 상태별로 조회 시 상태가 잘못 입력되면 예외가 반환된다.")
    void findAllReservationsUserIdAndStatusInvalidValueTest() throws Exception {
        //given
        var userId = 1L;
        var reservationStatusName = "invalidReservationStatus";

        //when & then
        mockMvc.perform(get("/api/customer/v1/reservations/me/{userId}", userId)
                        .param("status", reservationStatusName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("MethodArgumentTypeMismatchException"))
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("find-all-reservations-userId-invalidValue",
                        pathParameters(
                                parameterWithName("userId").description("유저 ID")
                        ),
                        queryParameters(
                                parameterWithName("status").description("예약 상태")
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