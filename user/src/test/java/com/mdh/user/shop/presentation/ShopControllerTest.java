package com.mdh.user.shop.presentation;

import com.mdh.common.shop.domain.ShopType;
import com.mdh.user.RestDocsSupport;
import com.mdh.user.shop.application.ShopService;
import com.mdh.user.shop.application.dto.ShopDetailInfoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShopController.class)
public class ShopControllerTest extends RestDocsSupport {

    @MockBean
    private ShopService shopService;


    @Override
    protected Object initController() {
        return new ShopController(shopService);
    }

    @Test
    @DisplayName("특정 상점의 상세 정보를 조회할 수 있다.")
    void findShopDetailsByIdTest() throws Exception {
        // Given
        var shopId = 1L;
        var shopDetailInfoResponse = new ShopDetailInfoResponse(
                "Test Shop",
                "This is a test shop",
                ShopType.ASIAN,
                new ShopDetailInfoResponse.ShopDetailsResponse(
                        "We serve the best food",
                        "09:00 - 22:00",
                        "Additional Info",
                        "http://testshop.com",
                        "123-456-7890",
                        "Closed on Sundays"
                ),
                new ShopDetailInfoResponse.ShopPriceResponse(
                        10,
                        20,
                        30,
                        40
                ),
                new ShopDetailInfoResponse.ShopAddressResponse(
                        "12345",
                        "123 Test Street",
                        "37.123",
                        "127.123"
                ),
                new ShopDetailInfoResponse.ShopRegionResponse(
                        "Test City",
                        "Test District"
                )
        );

        given(shopService.findShopDetailsById(shopId)).willReturn(shopDetailInfoResponse);

        // When & Then
        // When & Then
        mockMvc.perform(get("/api/customer/v1/shops/{shopId}", shopId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.name").value("Test Shop"))
                .andExpect(jsonPath("$.data.description").value("This is a test shop"))
                .andExpect(jsonPath("$.data.shopType").value("ASIAN"))
                .andExpect(jsonPath("$.data.shopDetails.introduce").value("We serve the best food"))
                .andExpect(jsonPath("$.data.shopDetails.openingHour").value("09:00 - 22:00"))
                .andExpect(jsonPath("$.data.shopDetails.info").value("Additional Info"))
                .andExpect(jsonPath("$.data.shopDetails.url").value("http://testshop.com"))
                .andExpect(jsonPath("$.data.shopDetails.phoneNumber").value("123-456-7890"))
                .andExpect(jsonPath("$.data.shopDetails.holiday").value("Closed on Sundays"))
                .andExpect(jsonPath("$.data.shopPrice.lunchMinPrice").value(10))
                .andExpect(jsonPath("$.data.shopPrice.lunchMaxPrice").value(20))
                .andExpect(jsonPath("$.data.shopPrice.DinnerMinPrice").value(30))
                .andExpect(jsonPath("$.data.shopPrice.DinnerMaxPrice").value(40))
                .andExpect(jsonPath("$.data.shopAddress.zipcode").value("12345"))
                .andExpect(jsonPath("$.data.shopAddress.address").value("123 Test Street"))
                .andExpect(jsonPath("$.data.shopAddress.latitude").value("37.123"))
                .andExpect(jsonPath("$.data.shopAddress.longitude").value("127.123"))
                .andExpect(jsonPath("$.data.shopRegion.city").value("Test City"))
                .andExpect(jsonPath("$.data.shopRegion.region").value("Test District"))
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("find-shop-details-by-id",
                        pathParameters(
                                parameterWithName("shopId").description("상점 ID")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("상점 상세 정보"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("상점 이름"),
                                fieldWithPath("data.description").type(JsonFieldType.STRING).description("상점 설명"),
                                fieldWithPath("data.shopType").type(JsonFieldType.STRING).description("상점 유형"),
                                fieldWithPath("data.shopDetails.introduce").type(JsonFieldType.STRING).description("상점 소개"),
                                fieldWithPath("data.shopDetails.openingHour").type(JsonFieldType.STRING).description("영업 시간"),
                                fieldWithPath("data.shopDetails.info").type(JsonFieldType.STRING).description("추가 정보"),
                                fieldWithPath("data.shopDetails.url").type(JsonFieldType.STRING).description("웹사이트 URL"),
                                fieldWithPath("data.shopDetails.phoneNumber").type(JsonFieldType.STRING).description("전화번호"),
                                fieldWithPath("data.shopDetails.holiday").type(JsonFieldType.STRING).description("휴무일"),
                                fieldWithPath("data.shopPrice.lunchMinPrice").type(JsonFieldType.NUMBER).description("점심 최소 가격"),
                                fieldWithPath("data.shopPrice.lunchMaxPrice").type(JsonFieldType.NUMBER).description("점심 최대 가격"),
                                fieldWithPath("data.shopPrice.DinnerMinPrice").type(JsonFieldType.NUMBER).description("저녁 최소 가격"),
                                fieldWithPath("data.shopPrice.DinnerMaxPrice").type(JsonFieldType.NUMBER).description("저녁 최대 가격"),
                                fieldWithPath("data.shopAddress.zipcode").type(JsonFieldType.STRING).description("우편번호"),
                                fieldWithPath("data.shopAddress.address").type(JsonFieldType.STRING).description("주소"),
                                fieldWithPath("data.shopAddress.latitude").type(JsonFieldType.STRING).description("위도"),
                                fieldWithPath("data.shopAddress.longitude").type(JsonFieldType.STRING).description("경도"),
                                fieldWithPath("data.shopRegion.city").type(JsonFieldType.STRING).description("도시"),
                                fieldWithPath("data.shopRegion.region").type(JsonFieldType.STRING).description("지역"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("서버 응답 시간")
                        )
                ));

    }

}