package com.mdh.devtable;

import com.mdh.devtable.controller.RestApiController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(RestApiController.class)
@MockBean(JpaMetamodelMappingContext.class)
class RestApiControllerTest extends RestDocsSupport {

    @Override
    protected Object initController() {
        return new RestApiController();
    }

    @Test
    @DisplayName("hello를 반환한다.")
    void responseHello() throws Exception {
        //given
        String response = "hello";

        // when

        // then
        mockMvc.perform(get("/hello")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect((ResultMatcher) content().string(response))
                .andDo(document("hello",
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                fieldWithPath("data").type(JsonFieldType.STRING).description("값")
                        )));
    }
}