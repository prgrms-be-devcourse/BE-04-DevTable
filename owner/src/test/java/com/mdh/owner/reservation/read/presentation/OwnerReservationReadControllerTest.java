package com.mdh.owner.reservation.read.presentation;

import com.mdh.owner.RestDocsSupport;
import com.mdh.common.reservation.domain.ReservationStatus;
import com.mdh.common.reservation.domain.SeatType;
import com.mdh.common.reservation.persistence.dto.OwnerShopReservationInfoResponse;
import com.mdh.owner.reservation.read.application.OwnerReservationReadService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
        var status = ReservationStatus.CREATED;

        var mockResponse = List.of(
                new OwnerShopReservationInfoResponse("requirement",
                        LocalDate.of(2023, 9, 12),
                        LocalTime.of(12, 30),
                        ReservationStatus.CREATED,
                        4,
                        SeatType.BAR)
        );

        given(ownerReservationReadService.findAllReservationsByOwnerIdAndStatus(any(), any(ReservationStatus.class))).willReturn(mockResponse);

        //when & then
        mockMvc.perform(get("/api/owner/v1/shops/reservations?status={status}", status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andDo(document("owner-find-all-reservations-by-status",
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