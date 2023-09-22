package com.mdh.owner.global.security.controller;

import com.mdh.owner.RestDocsSupport;
import com.mdh.owner.login.application.LoginService;
import com.mdh.owner.login.presentation.LoginController;
import com.mdh.owner.login.presentation.SignUpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
class LoginControllerTest extends RestDocsSupport {

    @MockBean
    private LoginService loginService;

    @DisplayName("점주는 회원가입을 할 수 있다.")
    @Test
    void signUp() throws Exception {
        // Given
        long ownerId = 1L;
        SignUpRequest request = new SignUpRequest(
                "test@email.com",
                "Password123",
                "Password123",
                "01012345678"
        );

        given(loginService.signUp(any(SignUpRequest.class))).willReturn(ownerId);

        // When & Then
        mockMvc.perform(post("/api/v1/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/owner/" + ownerId))
                .andExpect(jsonPath("$.statusCode").value("201"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("sign-up",
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                fieldWithPath("passwordCheck").type(JsonFieldType.STRING).description("비밀번호 확인"),
                                fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("핸드폰 번호")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 바디(비어있음)"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("서버 시간")
                        )
                ));
    }

    @Override
    protected Object initController() {
        return new LoginController(loginService);
    }
}