package com.mdh.user.waiting.presentation;

import com.mdh.common.shop.domain.ShopType;
import com.mdh.common.waiting.domain.WaitingStatus;
import com.mdh.user.DataInitializerFactory;
import com.mdh.user.RestDocsSupport;
import com.mdh.user.waiting.application.WaitingService;
import com.mdh.user.waiting.application.dto.UserWaitingResponse;
import com.mdh.user.waiting.application.dto.WaitingDetailsResponse;
import com.mdh.user.waiting.presentation.dto.MyWaitingsRequest;
import com.mdh.user.waiting.presentation.dto.WaitingCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserWaitingController.class)
class UserWaitingControllerTest extends RestDocsSupport {

    @MockBean
    private WaitingService waitingService;

    @Override
    protected Object initController() {
        return new UserWaitingController(waitingService);
    }

    @Test
    @DisplayName("웨이팅을 생성한다.")
    void createWaitingTest() throws Exception {
        //given
        var waitingCreateRequest = new WaitingCreateRequest(4, 2);
        var waitingId = 1L;

        var shopId = 1L;
        given(waitingService.createWaiting(any(), any(Long.class), any(WaitingCreateRequest.class))).willReturn(waitingId);

        //when & then
        mockMvc.perform(post("/api/customer/v1/waitings/shops/{shopId}", shopId)
                        .content(objectMapper.writeValueAsString(waitingCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value("201"))
                .andExpect(jsonPath("$.data").value("/api/customer/v1/waitings/" + waitingId))
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("waiting-create",
                        pathParameters(
                                parameterWithName("shopId").description("매장 아이디")
                        ),
                        requestFields(
                                fieldWithPath("adultCount").type(JsonFieldType.NUMBER).description("어른 인원 수"),
                                fieldWithPath("childCount").type(JsonFieldType.NUMBER).description("유아 인원 수")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.STRING).description("생성된 URI + id"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("생성된 서버 시간")
                        )
                ));
    }

    @ParameterizedTest
    @MethodSource("provideWaitingCreateOutOfRangeCount")
    @DisplayName("웨이팅 생성할 때 허용된 인원 범위를 넘어가면 예외가 발생한다.")
    void createWaitingValidationCountTest(WaitingCreateRequest waitingCreateRequest) throws Exception {
        // given
        var shopId = 1L;

        // when & then
        mockMvc.perform(post("/api/customer/v1/waitings/shops/{shopId}", shopId)
                        .content(objectMapper.writeValueAsString(waitingCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("MethodArgumentNotValidException"))
                .andDo(document("waiting-create-valid-count",
                        pathParameters(
                                parameterWithName("shopId").description("매장 아이디")
                        ),
                        requestFields(
                                fieldWithPath("adultCount").type(JsonFieldType.NUMBER).description("어른 인원 수"),
                                fieldWithPath("childCount").type(JsonFieldType.NUMBER).description("유아 인원 수")
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

    static Stream<Arguments> provideWaitingCreateOutOfRangeCount() {
        return Stream.of(
                Arguments.arguments(new WaitingCreateRequest(-1, 0)),
                Arguments.arguments(new WaitingCreateRequest(31, 0)),
                Arguments.arguments(new WaitingCreateRequest(0, -1)),
                Arguments.arguments(new WaitingCreateRequest(0, 31))
        );
    }

    @Test
    @DisplayName("웨이팅을 취소한다.")
    void cancelWaitingTest() throws Exception {
        //given
        var waitingId = 1L;
        doNothing().when(waitingService).cancelWaiting(waitingId);

        //when & then
        mockMvc.perform(patch("/api/customer/v1/waitings/{waitingId}", waitingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.statusCode").value("204"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("waiting-cancel",
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 바디(비어있음)"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("생성된 서버 시간")
                        )
                ));
    }

    @Test
    @DisplayName("유저가 등록했던 웨이팅을 상태별로 조회한다.")
    void findUserWaitingWithStatus() throws Exception {
        //given
        var waitingStatus = WaitingStatus.PROGRESS;
        var userWaitingResponse = new UserWaitingResponse(
                1L,
                1L,
                "상점이름",
                ShopType.AMERICAN.getName(),
                "서울시 강남구",
                1,
                3
        );
        given(waitingService.findAllByUserIdAndStatus(any(), any(WaitingStatus.class))).willReturn(List.of(userWaitingResponse));
        MultiValueMap<String, String> prams = new LinkedMultiValueMap<>();
        prams.add("status", waitingStatus.name());

        //when & then
        mockMvc.perform(get("/api/customer/v1/waitings/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .params(prams))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.data[0].shopId").value("1"))
                .andExpect(jsonPath("$.data[0].waitingId").value("1"))
                .andExpect(jsonPath("$.data[0].shopName").value("상점이름"))
                .andExpect(jsonPath("$.data[0].shopType").value("아메리칸음식"))
                .andExpect(jsonPath("$.data[0].region").value("서울시 강남구"))
                .andExpect(jsonPath("$.data[0].waitingNumber").value("1"))
                .andExpect(jsonPath("$.data[0].waitingCount").value("3"))
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("find-my-waitings",
                        queryParameters(
                                parameterWithName("status").description("웨이팅 상태")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                subsectionWithPath("data").description("데이터 항목의 배열").type(JsonFieldType.ARRAY),
                                fieldWithPath("data[].shopId").type(JsonFieldType.NUMBER).description("매장의 ID"),
                                fieldWithPath("data[].waitingId").type(JsonFieldType.NUMBER).description("웨이팅의 ID"),
                                fieldWithPath("data[].shopName").type(JsonFieldType.STRING).description("매장의 이름"),
                                fieldWithPath("data[].shopType").type(JsonFieldType.STRING).description("매장의 타입"),
                                fieldWithPath("data[].region").type(JsonFieldType.STRING).description("매장의 위치"),
                                fieldWithPath("data[].waitingNumber").type(JsonFieldType.NUMBER).description("발급된 웨이팅 번호"),
                                fieldWithPath("data[].waitingCount").type(JsonFieldType.NUMBER).description("웨이팅 등록 인원 수"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("생성된 서버 시간")
                        )
                ));
    }

    @Test
    @DisplayName("웨이팅을 상세 조회한다.")
    void findWaitingDetailsTest() throws Exception {
        //given
        var shopDetails = DataInitializerFactory.shopDetails();
        var waitingPeople = DataInitializerFactory.waitingPeople(2, 0);
        var waitingDetails = DataInitializerFactory.waitingDetails(shopDetails, WaitingStatus.PROGRESS, waitingPeople);
        // TODO LocalDate
        var waitingDetailsResponse = new WaitingDetailsResponse(waitingDetails, 5L);
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        var formattedCreateDate = waitingDetailsResponse.createdDate().format(formatter);
        var formattedModifiedDate = waitingDetailsResponse.modifiedDate().format(formatter);
        var waitingId = 1L;
        when(waitingService.findWaitingDetails(waitingId)).thenReturn(waitingDetailsResponse);

        //when & then
        mockMvc.perform(get("/api/customer/v1/waitings/{waitingId}", waitingId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.data.shop.shopName").value(waitingDetailsResponse.shop().shopName()))
                .andExpect(jsonPath("$.data.shop.shopType").value("KOREAN"))
                .andExpect(jsonPath("$.data.shop.region").value(waitingDetailsResponse.shop().region()))
                .andExpect(jsonPath("$.data.shop.shopDetails.url").value(waitingDetailsResponse.shop().shopDetails().url()))
                .andExpect(jsonPath("$.data.shop.shopDetails.phoneNumber").value(shopDetails.getPhoneNumber()))
                .andExpect(jsonPath("$.data.shop.shopDetails.introduce").value(shopDetails.getIntroduce()))
                .andExpect(jsonPath("$.data.shop.shopDetails.openingHours").value(shopDetails.getOpeningHours()))
                .andExpect(jsonPath("$.data.shop.shopDetails.holiday").value(shopDetails.getHoliday()))
                .andExpect(jsonPath("$.data.shop.shopDetails.info").value(shopDetails.getInfo()))
                .andExpect(jsonPath("$.data.waitingNumber").value(85))
                .andExpect(jsonPath("$.data.waitingRank").value(5))
                .andExpect(jsonPath("$.data.waitingStatus").value("PROGRESS"))
                .andExpect(jsonPath("$.data.waitingPeople.adultCount").value(waitingPeople.getAdultCount()))
                .andExpect(jsonPath("$.data.waitingPeople.childCount").value(waitingPeople.getChildCount()))
                .andExpect(jsonPath("$.data.createdDate").value(formattedCreateDate))
                .andExpect(jsonPath("$.data.modifiedDate").value(formattedModifiedDate))
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("waiting-detail-find",
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data.shop.shopName").type(JsonFieldType.STRING).description("상점 이름"),
                                fieldWithPath("data.shop.shopType").type(JsonFieldType.STRING).description("상점 타입"),
                                fieldWithPath("data.shop.region").type(JsonFieldType.STRING).description("상점 지역"),
                                fieldWithPath("data.shop.shopDetails.url").type(JsonFieldType.STRING).description("상점 URL"),
                                fieldWithPath("data.shop.shopDetails.phoneNumber").type(JsonFieldType.STRING).description("상점 전화번호"),
                                fieldWithPath("data.shop.shopDetails.introduce").type(JsonFieldType.STRING).description("상점 소개"),
                                fieldWithPath("data.shop.shopDetails.openingHours").type(JsonFieldType.STRING).description("상점 영업 시간"),
                                fieldWithPath("data.shop.shopDetails.holiday").type(JsonFieldType.STRING).description("상점 휴무일"),
                                fieldWithPath("data.shop.shopDetails.info").type(JsonFieldType.STRING).description("상점 간단 소개"),
                                fieldWithPath("data.waitingNumber").type(JsonFieldType.NUMBER).description("대기 번호"),
                                fieldWithPath("data.waitingRank").type(JsonFieldType.NUMBER).description("대기 순위"),
                                fieldWithPath("data.waitingStatus").type(JsonFieldType.STRING).description("대기 상태"),
                                fieldWithPath("data.waitingPeople.adultCount").type(JsonFieldType.NUMBER).description("어른 인원 수"),
                                fieldWithPath("data.waitingPeople.childCount").type(JsonFieldType.NUMBER).description("유아 인원 수"),
                                fieldWithPath("data.createdDate").type(JsonFieldType.STRING).description("생성일"),
                                fieldWithPath("data.modifiedDate").type(JsonFieldType.STRING).description("수정일"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("서버 시간")
                        )
                ));
    }

    @Test
    @DisplayName("웨이팅을 미룰 수 있다.")
    void postponeWaitingTest() throws Exception {
        //given
        var waitingId = 1L;
        doNothing().when(waitingService).postPoneWaiting(waitingId);

        //when & then
        mockMvc.perform(patch("/api/customer/v1/waitings/{waitingId}/postpone", waitingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("waiting-postpone",
                        pathParameters(
                                parameterWithName("waitingId").description("웨이팅 id")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 바디(비어있음)"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("생성된 서버 시간")
                        )
                ));
    }

    @Test
    @DisplayName("웨이팅을 미룰 수 없는 경우 예외를 발생 시킨다.")
    void NotAcceptablePostponeWaitingTest() throws Exception {
        // given
        var waitingId = 2L;
        doThrow(new IllegalStateException("미루기를 수행 할 수 없는 웨이팅 입니다. " + waitingId)).when(waitingService).postPoneWaiting(waitingId);

        // when & then
        mockMvc.perform(patch("/api/customer/v1/waitings/{waitingId}/postpone", waitingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.type").value("about:blank"))
                .andExpect(jsonPath("$.data.title").value("RuntimeException"))
                .andExpect(jsonPath("$.data.status").value(400))
                .andExpect(jsonPath("$.data.detail").value("미루기를 수행 할 수 없는 웨이팅 입니다. 2"))
                .andExpect(jsonPath("$.data.instance").value("/api/customer/v1/waitings/2/postpone"))
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("waiting-postpone-notAcceptable",
                        pathParameters(
                                parameterWithName("waitingId").description("웨이팅 id")
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