package com.mdh.owner.shop.presentation;

import com.mdh.owner.RestDocsSupport;
import com.mdh.owner.global.security.session.CustomUser;
import com.mdh.owner.global.security.session.UserInfo;
import com.mdh.owner.shop.application.OwnerShopService;
import com.mdh.common.shop.domain.ShopType;
import com.mdh.owner.shop.application.dto.ShopDetailInfoResponse;
import com.mdh.owner.shop.presentation.dto.OwnerShopCreateRequest;
import com.mdh.owner.shop.presentation.dto.RegionRequest;
import com.mdh.owner.shop.presentation.dto.ShopAddressRequest;
import com.mdh.owner.shop.presentation.dto.ShopDetailsRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OwnerShopController.class)
class OwnerShopControllerTest extends RestDocsSupport {

    @MockBean
    private OwnerShopService ownerShopService;

    @Override
    protected Object initController() {
        return new OwnerShopController(ownerShopService);
    }

    @DisplayName("점주는 새로운 매장을 생성할 수 있다.")
    @Test
    void createShop() throws Exception {
        // Given
        var shopId = 1L;
        var request = new OwnerShopCreateRequest(
                "ShopName",
                "ShopDescription",
                ShopType.AMERICAN,
                new ShopDetailsRequest(
                        "Introduce",
                        "OpeningHours",
                        "Info",
                        "https://example.com",
                        "01012345678",
                        "Holiday"
                ),
                new ShopAddressRequest(
                        "123 Main St",
                        "12345",
                        "37.7749",
                        "-122.4194"
                ),
                new RegionRequest(
                        "City",
                        "District"
                )
        );
        given(ownerShopService.createShop(any(), any(OwnerShopCreateRequest.class))).willReturn(shopId);

        // When & Then
        mockMvc.perform(post("/api/owner/v1/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", String.format("/api/owner/v1/shops/%d", shopId)))
                .andExpect(jsonPath("$.statusCode").value("201"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.serverDateTime").exists())
                .andDo(document("owner-shop-create",
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("매장 이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("매장 설명"),
                                fieldWithPath("shopType").type(JsonFieldType.STRING).description("매장 유형"),
                                fieldWithPath("shopDetailsRequest.introduce").type(JsonFieldType.STRING).description("매장 소개"),
                                fieldWithPath("shopDetailsRequest.introduce").type(JsonFieldType.STRING).description("Shop introduce"),
                                fieldWithPath("shopDetailsRequest.openingHours").type(JsonFieldType.STRING).description("Shop opening hours"),
                                fieldWithPath("shopDetailsRequest.info").type(JsonFieldType.STRING).description("Shop info"),
                                fieldWithPath("shopDetailsRequest.url").type(JsonFieldType.STRING).description("Shop URL"),
                                fieldWithPath("shopDetailsRequest.phoneNumber").type(JsonFieldType.STRING).description("Shop phone number"),
                                fieldWithPath("shopDetailsRequest.holiday").type(JsonFieldType.STRING).description("Shop holiday"),
                                fieldWithPath("shopDetailsRequest.openingHours").type(JsonFieldType.STRING).description("영업 시간"),
                                fieldWithPath("shopAddressRequest.address").type(JsonFieldType.STRING).description("주소"),
                                fieldWithPath("shopAddressRequest.zipcode").type(JsonFieldType.STRING).description("우편번호"),
                                fieldWithPath("shopAddressRequest.latitude").type(JsonFieldType.STRING).description("위도"),
                                fieldWithPath("shopAddressRequest.longitude").type(JsonFieldType.STRING).description("경도"),
                                fieldWithPath("regionRequest.city").type(JsonFieldType.STRING).description("도시"),
                                fieldWithPath("regionRequest.district").type(JsonFieldType.STRING).description("지역")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 바디(비어 있음)"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("생성된 서버 시간")
                        ),
                        responseHeaders(
                                headerWithName("Location").description("새로 생성된 매장의 URI")
                        )
                ));
    }

    @DisplayName("점주는 상점을 생성할 때 잘못된 값을 입력할 수 없다.")
    @Test
    void createShopThrowException() throws Exception {
        var request = new OwnerShopCreateRequest(
                "", // 빈 이름
                "상점 설명",
                ShopType.AMERICAN,
                new ShopDetailsRequest("소개", "영업 시간", null, null, "109", null),
                new ShopAddressRequest("주소", "우편번호", "위도", "경도"),
                new RegionRequest("도시", "지역")
        );

        mockMvc.perform(post("/api/owner/v1/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.data.title").value("MethodArgumentNotValidException"))
                .andDo(print())
                .andDo(document("owner-shop-create-invalid",
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("매장 이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("매장 설명"),
                                fieldWithPath("shopType").type(JsonFieldType.STRING).description("매장 유형"),
                                fieldWithPath("shopDetailsRequest.introduce").type(JsonFieldType.STRING).description("매장 소개"),
                                fieldWithPath("shopDetailsRequest.introduce").type(JsonFieldType.STRING).description("Shop introduce"),
                                fieldWithPath("shopDetailsRequest.openingHours").type(JsonFieldType.STRING).description("Shop opening hours"),
                                fieldWithPath("shopDetailsRequest.info").type(JsonFieldType.NULL).description("Shop info"),
                                fieldWithPath("shopDetailsRequest.url").type(JsonFieldType.NULL).description("Shop URL"),
                                fieldWithPath("shopDetailsRequest.phoneNumber").type(JsonFieldType.STRING).description("Shop phone number"),
                                fieldWithPath("shopDetailsRequest.holiday").type(JsonFieldType.NULL).description("Shop holiday"),
                                fieldWithPath("shopDetailsRequest.openingHours").type(JsonFieldType.STRING).description("영업 시간"),
                                fieldWithPath("shopAddressRequest.address").type(JsonFieldType.STRING).description("주소"),
                                fieldWithPath("shopAddressRequest.zipcode").type(JsonFieldType.STRING).description("우편번호"),
                                fieldWithPath("shopAddressRequest.latitude").type(JsonFieldType.STRING).description("위도"),
                                fieldWithPath("shopAddressRequest.longitude").type(JsonFieldType.STRING).description("경도"),
                                fieldWithPath("regionRequest.city").type(JsonFieldType.STRING).description("도시"),
                                fieldWithPath("regionRequest.district").type(JsonFieldType.STRING).description("지역")
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
    public void findShopDetailsByOwner() throws Exception {
        // Given
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

        given(ownerShopService.findShopByOwner(any())).willReturn(shopDetailInfoResponse);

        // When & Then
        mockMvc.perform(get("/api/owner/v1/shop")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
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