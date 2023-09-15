package com.mdh.devtable.reservation.read.presentation;

import com.mdh.devtable.RestDocsSupport;
import com.mdh.devtable.reservation.ReservationStatus;
import com.mdh.devtable.reservation.SeatType;
import com.mdh.devtable.reservation.persistence.dto.OwnerShopReservationInfoResponse;
import com.mdh.devtable.reservation.read.application.OwnerReservationReadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OwnerReservationReadController.class)
class OwnerReservationReadControllerTest extends RestDocsSupport {

    @MockBean
    private OwnerReservationReadService ownerReservationReadService;

    @Override
    protected Object initController() {
        return new OwnerReservationReadController(ownerReservationReadService);
    }

    @DisplayName("점주는 자신이 갖고 있는 매장 정보를 상태에 따라 조회할 수 있다.")
    @Test
    public void findAllReservationsByOwnerIdAndStatus() throws Exception {
        //given
        var ownerId = 1L;
        var status = ReservationStatus.CREATED;

        var mockResponse = List.of(
                new OwnerShopReservationInfoResponse("requirement",
                        LocalDate.of(2023, 9, 12),
                        LocalTime.of(12, 30),
                        ReservationStatus.CREATED,
                        4,
                        SeatType.BAR)
        );

        given(ownerReservationReadService.findAllReservationsByOwnerIdAndStatus(any(Long.class), any(ReservationStatus.class))).willReturn(mockResponse);

        //when & then
        mockMvc.perform(get("/api/owner/v1/shops/{ownerId}/reservations?status={status}", ownerId, status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.data[0].requirement").value("requirement"))
                .andDo(document("owner-find-all-reservations-by-status",
                        pathParameters(
                                parameterWithName("ownerId").description("점주 ID")
                        ),
                        queryParameters(
                                parameterWithName("status").description("예약 상태")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data[].requirement").type(JsonFieldType.STRING).description("예약 ID"),
                                fieldWithPath("data[].reservationDate").type(JsonFieldType.ARRAY).description("예약 날짜"),
                                fieldWithPath("data[].reservationTime").type(JsonFieldType.ARRAY).description("예약 시간"),
                                fieldWithPath("data[].reservationStatus").type(JsonFieldType.STRING).description("예약 상태"),
                                fieldWithPath("data[].personCount").type(JsonFieldType.NUMBER).description("예약 인원 수"),
                                fieldWithPath("data[].seatType").type(JsonFieldType.STRING).description("좌석 타입"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("서버 시간")
                        )
                ));
    }
}