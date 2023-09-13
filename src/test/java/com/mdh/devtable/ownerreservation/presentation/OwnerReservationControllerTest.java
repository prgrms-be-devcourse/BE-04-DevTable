package com.mdh.devtable.ownerreservation.presentation;

import com.mdh.devtable.RestDocsSupport;
import com.mdh.devtable.ownerreservation.application.OwnerReservationService;
import com.mdh.devtable.ownerreservation.presentation.dto.SeatCreateRequest;
import com.mdh.devtable.ownerreservation.presentation.dto.ShopReservationCreateRequest;
import com.mdh.devtable.ownerreservation.presentation.dto.ShopReservationDateTimeCreateRequest;
import com.mdh.devtable.reservation.domain.SeatType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @DisplayName("점주는 매장의 좌석을 생성할 수 있다.")
    @Test
    void createSeat() throws Exception {
        var shopId = 1L;
        var request = new SeatCreateRequest(SeatType.BAR);
        given(ownerReservationService.saveSeat(any(Long.class), any(SeatCreateRequest.class))).willReturn(1L);

        mockMvc.perform(post("/api/owner/v1/shops/{shopId}/seats", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", String.format("/api/owner/v1/shops/%d/seats/%d", shopId, 1L)))
                .andExpect(jsonPath("$.statusCode").value("201"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("owner-seat-create",
                        pathParameters(
                                parameterWithName("shopId").description("매장 id")
                        ),
                        requestFields(
                                fieldWithPath("seatType").type(JsonFieldType.STRING).description("좌석 타입")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 바디(비어 있음)"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("생성된 서버 시간")
                        ),
                        responseHeaders(
                                headerWithName("Location").description("새로 생성된 좌석의 URI")
                        )
                ));
    }

    @DisplayName("점주는 잘못된 좌석 정보로 좌석을 생성할 수 없다.")
    @Test
    void createSeatWithInvalidInput() throws Exception {
        var shopId = 1L;
        var request = new SeatCreateRequest(null);  // Invalid value (null for seatType)

        mockMvc.perform(post("/api/owner/v1/shops/{shopId}/seats", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("MethodArgumentNotValidException"))
                .andDo(document("owner-seat-create-invalid",
                        pathParameters(
                                parameterWithName("shopId").description("매장 id")
                        ),
                        requestFields(
                                fieldWithPath("seatType").type(JsonFieldType.STRING).description("좌석 타입").optional()
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

    @DisplayName("점주는 매장의 예약 날짜와 시간을 생성할 수 있다.")
    @Test
    void createShopReservationDateTime() throws Exception {
        var shopId = 1L;
        var request = new ShopReservationDateTimeCreateRequest(LocalDate.of(2023, 9, 12), LocalTime.of(12, 30));
        given(ownerReservationService.createShopReservationDateTime(any(Long.class), any(ShopReservationDateTimeCreateRequest.class))).willReturn(1L);

        mockMvc.perform(post("/api/owner/v1/shops/{shopId}/reservation-date-time", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", String.format("/api/owner/v1/shops/%d/reservation-date-time/%d", shopId, 1L)))
                .andExpect(jsonPath("$.statusCode").value("201"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(print())
                .andDo(document("owner-shop-reservation-date-create",
                        pathParameters(
                                parameterWithName("shopId").description("매장 id")
                        ),
                        requestFields(
                                fieldWithPath("localDate").type(JsonFieldType.ARRAY).description("예약 날짜"),
                                fieldWithPath("localTime").type(JsonFieldType.ARRAY).description("예약 시간")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 바디(비어 있음)"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("생성된 서버 시간")
                        ),
                        responseHeaders(
                                headerWithName("Location").description("새로 생성된 예약 날짜와 시간의 URI")
                        )
                ));
    }

    @DisplayName("점주는 잘못된 예약 날짜와 시간 정보로 매장 예약을 생성할 수 없다.")
    @Test
    void createShopReservationDateTimeWithInvalidInput() throws Exception {
        var shopId = 1L;
        var request = new HashMap<>();
        request.put("localDate", "asdf");
        request.put("localTime", "asdf");

        mockMvc.perform(post("/api/owner/v1/shops/{shopId}/reservation-date-time", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("HttpMessageNotReadableException"))
                .andDo(document("owner-shop-reservation-date-create-invalid",
                        pathParameters(
                                parameterWithName("shopId").description("매장 id")
                        ),
                        requestFields(
                                fieldWithPath("localDate").type(JsonFieldType.STRING).description("예약 날짜").optional(),
                                fieldWithPath("localTime").type(JsonFieldType.STRING).description("예약 시간").optional()
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("타이틀"),
                                fieldWithPath("data.status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data.detail").type(JsonFieldType.STRING).description("상세 설명"),
                                fieldWithPath("data.instance").type(JsonFieldType.STRING).description("인스턴스 URI"),
                                fieldWithPath("data.type").type(JsonFieldType.STRING).description("타입"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("서버 시간")
                        )
                ));
    }

    @DisplayName("점주는 매장의 예약 날짜와 시간에 좌석을 추가할 수 있다.")
    @Test
    void createShopReservationDateTimeSeat() throws Exception {
        var shopReservationDateTimeId = 1L;
        var seatId = 1L;
        given(ownerReservationService.createShopReservationDateTimeSeat(any(Long.class), any(Long.class))).willReturn(1L);

        mockMvc.perform(post("/api/owner/v1/shop-reservation-date-times/{shopReservationDateTimeId}/seats/{seatId}/shop-reservation-date-time-seats", shopReservationDateTimeId, seatId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", String.format("/api/owner/v1/shop-reservation-date-times/%d/seats/%d/shop-reservation-date-time-seats/%d", shopReservationDateTimeId, seatId, 1L)))
                .andExpect(jsonPath("$.statusCode").value("201"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("owner-shop-reservation-date-time-seat-create",
                        pathParameters(
                                parameterWithName("shopReservationDateTimeId").description("매장 예약 날짜와 시간 ID"),
                                parameterWithName("seatId").description("좌석 ID")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 바디(비어 있음)"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("생성된 서버 시간")
                        ),
                        responseHeaders(
                                headerWithName("Location").description("새로 생성된 예약 날짜와 시간에 추가된 좌석의 URI")
                        )
                ));
    }


}