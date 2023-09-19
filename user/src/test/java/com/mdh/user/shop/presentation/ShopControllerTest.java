package com.mdh.user.shop.presentation;

import com.mdh.common.shop.domain.ShopType;
import com.mdh.user.RestDocsSupport;
import com.mdh.user.shop.application.ShopService;
import com.mdh.user.shop.application.dto.ShopDetailInfoResponse;
import com.mdh.user.shop.application.dto.ShopResponse;
import com.mdh.user.shop.application.dto.ShopResponses;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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


    @Test
    @DisplayName("조건에 따라 대기 중인 상점을 조회할 수 있다.")
    void findByConditionWithWaitingTest() throws Exception {
        // Given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("name", "Test Shop");
        params.add("shopType", "ASIAN");
        params.add("region", "Test City");
        params.add("minPrice", "10");
        params.add("maxPrice", "50");
        params.add("sort", "priceAsc");
        var shopResponsesList = Arrays.asList(
                new ShopResponse(1L, "Shop1", ShopType.ASIAN, 10, 20, "City1", "District1", 5)
        );
        Page<ShopResponse> mockPage = new PageImpl<>(shopResponsesList, PageRequest.of(0, 10), shopResponsesList.size());
                ShopResponses shopResponses = new ShopResponses(mockPage);

        given(shopService.findByConditionWithWaiting(any(), any()))
                .willReturn(shopResponses);

        // When & Then
        mockMvc.perform(get("/api/customer/v1/shops/waitings")
                        .param("page", "0")
                        .param("size", "10")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.data").exists())
                // ... (이전 코드 생략)

                .andDo(document("find-shops-by-condition-with-waiting",
                        queryParameters(
                                parameterWithName("page").description("페이지"),
                                parameterWithName("size").description("데이터 크기"),
                                parameterWithName("name").description("상점 이름").optional(),
                                parameterWithName("shopType").description("상점 유형").optional(),
                                parameterWithName("region").description("지역").optional(),
                                parameterWithName("minPrice").description("최소 가격").optional(),
                                parameterWithName("maxPrice").description("최대 가격").optional(),
                                parameterWithName("sort").description("정렬 방식").optional()
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("data.shopResponses.content[].shopId").type(JsonFieldType.NUMBER).description("상점 ID"),
                                fieldWithPath("data.shopResponses.content[].shopName").type(JsonFieldType.STRING).description("상점 이름"),
                                fieldWithPath("data.shopResponses.content[].shopType").type(JsonFieldType.STRING).description("상점 유형"),
                                fieldWithPath("data.shopResponses.content[].minPrice").type(JsonFieldType.NUMBER).description("최소 가격"),
                                fieldWithPath("data.shopResponses.content[].maxPrice").type(JsonFieldType.NUMBER).description("최대 가격"),
                                fieldWithPath("data.shopResponses.content[].city").type(JsonFieldType.STRING).description("도시"),
                                fieldWithPath("data.shopResponses.content[].district").type(JsonFieldType.STRING).description("지역"),
                                fieldWithPath("data.shopResponses.content[].totalWaitingCount").type(JsonFieldType.NUMBER).description("총 대기 수"),
                                fieldWithPath("data.shopResponses.pageable").type(JsonFieldType.OBJECT).description("페이지 정보"),
                                fieldWithPath("data.shopResponses.pageable.pageNumber").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                fieldWithPath("data.shopResponses.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("data.shopResponses.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                                fieldWithPath("data.shopResponses.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                fieldWithPath("data.shopResponses.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬된 여부"),
                                fieldWithPath("data.shopResponses.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("미정렬 여부"),
                                fieldWithPath("data.shopResponses.pageable.offset").type(JsonFieldType.NUMBER).description("오프셋"),
                                fieldWithPath("data.shopResponses.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                                fieldWithPath("data.shopResponses.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("페이징 미적용 여부"),
                                fieldWithPath("data.shopResponses.totalElements").type(JsonFieldType.NUMBER).description("총 요소 수"),
                                fieldWithPath("data.shopResponses.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                fieldWithPath("data.shopResponses.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("data.shopResponses.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("data.shopResponses.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("data.shopResponses.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                                fieldWithPath("data.shopResponses.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                fieldWithPath("data.shopResponses.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬된 여부"),
                                fieldWithPath("data.shopResponses.sort.unsorted").type(JsonFieldType.BOOLEAN).description("미정렬 여부"),
                                fieldWithPath("data.shopResponses.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지의 요소 수"),
                                fieldWithPath("data.shopResponses.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                fieldWithPath("data.shopResponses.empty").type(JsonFieldType.BOOLEAN).description("빈 페이지 여부"),
                                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("서버 응답 시간")
                        )

                ));

    }


}