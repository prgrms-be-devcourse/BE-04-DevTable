package com.mdh.devtable.waiting.presentation;

import com.mdh.devtable.RestDocsSupport;
import com.mdh.devtable.shop.ShopType;
import com.mdh.devtable.waiting.application.WaitingService;
import com.mdh.devtable.waiting.application.dto.UserWaitingResponse;
import com.mdh.devtable.waiting.domain.WaitingStatus;
import com.mdh.devtable.waiting.presentation.dto.MyWaitingsRequest;
import com.mdh.devtable.waiting.presentation.dto.WaitingCreateRequest;
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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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
        var waitingCreateRequest = new WaitingCreateRequest(1L, 1L, 4, 2);
        var waitingId = 1L;
        when(waitingService.createWaiting(waitingCreateRequest)).thenReturn(waitingId);

        //when & then
        mockMvc.perform(post("/api/customer/v1/waitings")
                        .content(objectMapper.writeValueAsString(waitingCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value("201"))
                .andExpect(jsonPath("$.data").value("/api/customer/v1/waitings" + waitingId))
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("waiting-create",
                        requestFields(
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저 아이디"),
                                fieldWithPath("shopId").type(JsonFieldType.NUMBER).description("매장 아이디"),
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

        // when & then
        mockMvc.perform(post("/api/customer/v1/waitings")
                        .content(objectMapper.writeValueAsString(waitingCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("MethodArgumentNotValidException"))
                .andDo(document("waiting-create-valid-count",
                        requestFields(
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저 아이디"),
                                fieldWithPath("shopId").type(JsonFieldType.NUMBER).description("매장 아이디"),
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
                Arguments.arguments(new WaitingCreateRequest(1L, 1L, -1, 0)),
                Arguments.arguments(new WaitingCreateRequest(1L, 1L, 31, 0)),
                Arguments.arguments(new WaitingCreateRequest(1L, 1L, 0, -1)),
                Arguments.arguments(new WaitingCreateRequest(1L, 1L, 0, 31))
        );
    }

    @ParameterizedTest
    @MethodSource("provideWaitingCreateNullValue")
    @DisplayName("웨이팅 생성할 때 아이디가 null이면 예외가 발생한다.")
    void createWaitingValidationNullTest(WaitingCreateRequest waitingCreateRequest) throws Exception {
        // given

        // when & then
        mockMvc.perform(post("/api/customer/v1/waitings")
                        .content(objectMapper.writeValueAsString(waitingCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("MethodArgumentNotValidException"))
                .andDo(document("waiting-create-valid-null",
                        requestFields(
                                fieldWithPath("userId").type(JsonFieldType.VARIES).description("유저 아이디"),
                                fieldWithPath("shopId").type(JsonFieldType.VARIES).description("매장 아이디"),
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

    static Stream<Arguments> provideWaitingCreateNullValue() {
        return Stream.of(
                Arguments.arguments(new WaitingCreateRequest(null, 1L, 2, 0)),
                Arguments.arguments(new WaitingCreateRequest(1L, null, 2, 0))
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
        var myWaitingsRequest = new MyWaitingsRequest(1L, WaitingStatus.PROGRESS);
        var userWaitingResponse = new UserWaitingResponse(
                1L,
                1L,
                "상점이름",
                ShopType.AMERICAN.getName(),
                "서울시 강남구",
                1,
                3
        );

        when(waitingService.findAllByUserIdAndStatus(myWaitingsRequest)).thenReturn(List.of(userWaitingResponse));

        //when & then
        mockMvc.perform(get("/api/customer/v1/waitings/{userId}", 1)
                        .content(objectMapper.writeValueAsString(myWaitingsRequest))
                        .contentType(MediaType.APPLICATION_JSON))
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
                        requestFields(
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저 아이디"),
                                fieldWithPath("waitingStatus").type(JsonFieldType.STRING).description("웨이팅 상태")
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
}