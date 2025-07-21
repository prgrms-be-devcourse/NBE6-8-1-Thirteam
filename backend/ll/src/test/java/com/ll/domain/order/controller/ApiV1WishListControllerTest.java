// src/test/java/com/ll/domain/order/controller/ApiV1WishListControllerTest.java
package com.ll.domain.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.domain.member.entity.Member;
import com.ll.domain.member.service.MemberService;
import com.ll.domain.order.entity.WishList;
import com.ll.domain.order.service.WishListService;
import com.ll.domain.product.entity.Product;
import com.ll.domain.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


// Product 엔티티에 사용되는 enum들이 같은 패키지에 있다면 별도 임포트는 필요 없다.
// import com.ll.domain.product.entity.ProductCategory;
// import com.ll.domain.product.entity.ProductStatus;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ApiV1WishListControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WishListService wishListService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ProductService productService;

    // 테스트용 멤버와 상품 데이터
    private Member testMember1;
    private Member testMember2;
    private Product testProduct1;
    private Product testProduct2;
    private Product testProduct3;

    @BeforeEach
    void setUp() {
        // 1. Member 데이터 생성 (data.sql에 없으므로 여기서 생성)
        testMember1 = memberService.addUserMember("testuser1@example.com", "1234", "테스트유저1", "서울시 강남구");
        testMember2 = memberService.addUserMember("testuser2@example.com", "1234", "테스트유저2", "부산시 해운대구");

        // 2. Product 데이터 조회 (data.sql에서 이미 삽입되므로, ID로 조회하여 사용)
        // data.sql의 INSERT 순서에 따라 ID가 1부터 순서대로 할당된다고 가정한다.
        testProduct1 = productService.getProduct(1); // ID 1번 상품 (아이스 라떼)
        testProduct2 = productService.getProduct(2); // ID 2번 상품 (에스프레소)
        testProduct3 = productService.getProduct(5); // ID 5번 상품 (오렌지 주스)
    }


    @Test
    @DisplayName("t1_찜 목록에 상품 추가 성공 (새 항목)")
    @WithMockUser(username = "testuser1@example.com", roles = "USER")
    void t1() throws Exception {
        // Given
        ApiV1WishListController.AddWishListRequest requestBody = new ApiV1WishListController.AddWishListRequest(
                testMember1.getId(),
                testProduct1.getId(),
                1
        );
        String jsonBody = objectMapper.writeValueAsString(requestBody);

        // When
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/wishlist")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(ApiV1WishListController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.msg").value("상품이 위시리스트에 담겼습니다."))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.id").value(testMember1.getId()))
                .andExpect(jsonPath("$.data.productId").value(testProduct1.getId()))
                .andExpect(jsonPath("$.data.quantity").value(1));
    }


    @Test
    @DisplayName("t2_찜 목록에 상품 추가 성공 (기존 항목 수량 증가)")
    @WithMockUser(username = "testuser1@example.com", roles = "USER")
    void t2() throws Exception {
        // Given: 미리 찜 목록에 항목 추가
        WishList existingItem = wishListService.create(testMember1.getId(), testProduct1.getId(), 1); // 초기 수량 1

        ApiV1WishListController.AddWishListRequest requestBody = new ApiV1WishListController.AddWishListRequest(
                testMember1.getId(),
                testProduct1.getId(),
                2 // 2개 더 추가
        );
        String jsonBody = objectMapper.writeValueAsString(requestBody);

        // When
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/wishlist")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(existingItem.getId()))
                .andExpect(jsonPath("$.data.quantity").value(1 + 2));
    }

    @Test
    @DisplayName("t3_찜 목록 상품 제거 성공")
    @WithMockUser(username = "testuser1@example.com", roles = "USER")
    void t3() throws Exception {
        // Given: 찜 목록 항목 생성
        WishList wishListToRemove = wishListService.create(testMember1.getId(), testProduct1.getId(), 1);
        int wishListId = wishListToRemove.getId();

        // When
        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/wishlist/" + wishListId)
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1WishListController.class))
                .andExpect(handler().methodName("delete")) // 컨트롤러 메서드명
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("위시리스트가 삭제되었습니다."));

    }

    @Test
    @DisplayName("t4_찜 목록 상품 제거 실패 - 존재하지 않는 항목")
    @WithMockUser(username = "testuser1@example.com", roles = "USER")
    void t4() throws Exception {
        // Given: 존재하지 않는 ID
        int nonExistentId = Integer.MAX_VALUE;

        // When
        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/wishlist/" + nonExistentId)
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(ApiV1WishListController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(jsonPath("$.resultCode").value("404-1"))
                .andExpect(jsonPath("$.msg").value("존재하지 않는 찜 목록 항목이다."));
    }

    @Test
    @DisplayName("t5_특정_회원의_찜_목록_조회_성공")
    @WithMockUser(username = "testuser1@example.com", roles = "USER")
    void t5() throws Exception {
        // Given: 찜 목록에 2개 상품 추가
        wishListService.create(testMember1.getId(), testProduct1.getId(), 1);
        wishListService.create(testMember1.getId(), testProduct2.getId(), 2);

        // When
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/wishlist/member/" + testMember1.getId())
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getWishList"))
                // [수정] 응답이 List가 아닌 RsData 형식이므로, RsData 구조를 검증
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("위시리스트 목록을 조회했습니다."))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].productId").value(testProduct1.getId()))
                .andExpect(jsonPath("$.data[1].productId").value(testProduct2.getId()));
    }


    @Test
    @DisplayName("t6_특정 회원의 찜 목록 조회 실패 - 존재하지 않는 회원")
    @WithMockUser(username = "testuser1@example.com", roles = "USER")
    void t6() throws Exception {
        // Given: 존재하지 않는 회원 ID
        int nonExistentMemberId = Integer.MAX_VALUE;

        // When
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/wishlist/member/" + nonExistentMemberId)
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(handler().methodName("getWishList"))
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("존재하지 않는 회원이다."));
    }

    @Test
    @DisplayName("t7_찜 목록 비우기 성공")
    @WithMockUser(username = "testuser1@example.com", roles = "USER")
    void t7() throws Exception {
        // Given: 찜 목록 항목 생성
        wishListService.create(testMember1.getId(), testProduct1.getId(), 1);
        wishListService.create(testMember1.getId(), testProduct2.getId(), 2);

        // When - 이메일로 요청 변경
        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/wishlist/member/" + testMember1.getId())
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1WishListController.class))
                .andExpect(handler().methodName("clearMemberWishList"))
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("위시리스트가 모두 삭제되었습니다."));


        assertThat(wishListService.getMemberWishList(testMember1.getId())).isEmpty();
    }

}