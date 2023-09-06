package com.mdh.devtable.waiting.presentation;

import com.mdh.devtable.RestDocsSupport;
import com.mdh.devtable.waiting.application.WaitingService;
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

import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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
}