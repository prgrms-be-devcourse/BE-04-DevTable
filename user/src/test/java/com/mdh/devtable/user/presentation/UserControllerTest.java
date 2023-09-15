package com.mdh.devtable.user.presentation;

import com.mdh.devtable.RestDocsSupport;
import com.mdh.devtable.user.application.UserService;
import com.mdh.devtable.user.presentation.dto.SignUpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest extends RestDocsSupport {

    @MockBean
    private UserService userService;

    @Override
    protected Object initController() {
        return new UserController(userService);
    }

    @Test
    @DisplayName("회원가입을 한다.")
    void signUp() throws Exception {
        //given
        var signUpRequest = new SignUpRequest("test@example.com", "password123", "password123", "01012345678");
        var expectedId = 1L;
        when(userService.signUp(signUpRequest)).thenReturn(expectedId);

        //when & then
        mockMvc.perform(post("/api/v1/users")
                        .content(objectMapper.writeValueAsString(signUpRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value("201"))
                .andExpect(jsonPath("$.data").value("/api/v1/users/" + expectedId))
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("user-sign-up",
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                fieldWithPath("passwordCheck").type(JsonFieldType.STRING).description("비밀번호 확인"),
                                fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("핸드폰 번호")
                        ),
                        responseFields(fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.STRING).description("생성된 URI + id"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("생성된 서버 시간")
                        )
                ));
    }

    @Test
    @DisplayName("회원가입시 email을 잘못 입력한다.")
    void signUpException1() throws Exception {
        //given
        var signUpRequest = new SignUpRequest("tesexample.com", "password123", "password123", "01012345678");

        //when & then
        mockMvc.perform(post("/api/v1/users")
                        .content(objectMapper.writeValueAsString(signUpRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("MethodArgumentNotValidException"))
                .andDo(document("user-sign-up-invalid-email",
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                fieldWithPath("passwordCheck").type(JsonFieldType.STRING).description("비밀번호 확인"),
                                fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("핸드폰 번호")
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

    @Test
    @DisplayName("회원가입시 password를 잘못 입력한다.")
    void signUpException2() throws Exception {
        // given
        var signUpRequest = new SignUpRequest("test@example.com", "invalid", "invalid", "01012345678");

        // when & then
        mockMvc.perform(post("/api/v1/users")
                        .content(objectMapper.writeValueAsString(signUpRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("MethodArgumentNotValidException"))
                .andDo(document("user-sign-up-invalid-password",
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                fieldWithPath("passwordCheck").type(JsonFieldType.STRING).description("비밀번호 확인"),
                                fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("핸드폰 번호")
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

    @Test
    @DisplayName("회원가입시 password 확인을 잘못 입력한다.")
    void signUpException3() throws Exception {
        //given
        var signUpRequest = new SignUpRequest("test@example.com", "password123", "password1234", "01012345678"); // 비밀번호와 비밀번호 확인이 다름

        //when & then
        mockMvc.perform(post("/api/v1/users")
                        .content(objectMapper.writeValueAsString(signUpRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("MethodArgumentNotValidException"))
                .andDo(document("user-sign-up-invalid-password-check",
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                fieldWithPath("passwordCheck").type(JsonFieldType.STRING).description("비밀번호 확인"),
                                fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("핸드폰 번호")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data.type").type(JsonFieldType.STRING).description("타입"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("타이틀"),
                                fieldWithPath("data.status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data.detail").type(JsonFieldType.STRING).description("상세 설명"),
                                fieldWithPath("data.instance").type(JsonFieldType.STRING).description("인스턴스 URI"),
                                fieldWithPath("data.validationError").type(JsonFieldType.ARRAY).description("Validation 오류"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("서버 시간")
                        )
                ));
    }

    @Test
    @DisplayName("회원가입시 핸드폰 번호를 잘못 입력할 수 없다.")
    void signUpException4() throws Exception {
        //given
        var signUpRequest = new SignUpRequest("test@example.com", "password123", "password123", "0101236578910");

        //when & then
        mockMvc.perform(post("/api/v1/users")
                        .content(objectMapper.writeValueAsString(signUpRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("MethodArgumentNotValidException"))
                .andDo(document("user-sign-up-invalid-phone-number",
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                fieldWithPath("passwordCheck").type(JsonFieldType.STRING).description("비밀번호 확인"),
                                fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("핸드폰 번호")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data.type").type(JsonFieldType.STRING).description("타입"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("타이틀"),
                                fieldWithPath("data.status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data.detail").type(JsonFieldType.STRING).description("상세 설명"),
                                fieldWithPath("data.instance").type(JsonFieldType.STRING).description("인스턴스 URI"),
                                fieldWithPath("data.validationError").type(JsonFieldType.ARRAY).description("Validation 오류"),
                                fieldWithPath("data.validationError[].field").type(JsonFieldType.STRING).description("오류 필드"),
                                fieldWithPath("data.validationError[].message").type(JsonFieldType.STRING).description("오류 메시지"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("서버 시간")
                        )
                ));
    }
}