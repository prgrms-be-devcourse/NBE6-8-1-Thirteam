package com.ll.domain.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.domain.member.entity.Member;
import com.ll.domain.member.service.MemberService;
import com.ll.domain.order.entity.Order;
import com.ll.domain.order.service.OrderService;
import com.ll.domain.product.entity.Product;
import com.ll.domain.product.repository.ProductRepository;
import com.ll.domain.product.service.ProductService;
import com.ll.global.rq.Rq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ApiV1OrderControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderService orderService;

    @Autowired
    private Rq rq;

    private Member testMember;
    private Product testProduct1, testProduct2;

    @BeforeEach
    void setUp() {
        // 테스트에 필요한 회원 및 상품 데이터를 미리 생성
        testMember = memberService.addUserMember("user1@test.com", "1234", "테스터1", "서울시 강남구");
        testProduct1 = productRepository.findById(1).orElse(null);
        testProduct2 = productRepository.findById(2).orElse(null);

        // 테스트 Rq에 사용자 지정
        ((TestRq) rq).setActor(testMember);
    }

    @Test
    @DisplayName("POST /api/v1/orders : 주문 생성 성공")
    @WithMockUser(username = "user1@test.com", roles = "USER")
    void t1() throws Exception {
        // GIVEN
        ApiV1OrderController.OrderCreateReqBody requestBody = new ApiV1OrderController.OrderCreateReqBody(
                List.of(testProduct1.getId(), testProduct2.getId()),
                List.of(1, 2),
                "서울시 서초구"
        );
        String jsonBody = objectMapper.writeValueAsString(requestBody);

        // WHEN
        ResultActions resultActions = mvc.perform(
                        post("/api/v1/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(ApiV1OrderController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.data.address").value("서울시 서초구"))
                .andExpect(jsonPath("$.data.orderItems.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/v1/orders/{id} : 단건 주문 조회 성공")
    @WithMockUser(username = "user1@test.com", roles = "USER")
    void t2() throws Exception {
        // GIVEN
        Order order = orderService.create(
                testMember.getId(),
                List.of(testProduct1.getId()),
                List.of(3),
                "부산시 해운대구"
        );

        // WHEN
        ResultActions resultActions = mvc.perform(
                        get("/api/v1/orders/" + order.getId())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getItem"))
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.data.id").value(order.getId()))
                .andExpect(jsonPath("$.data.address").value("부산시 해운대구"))
                .andExpect(jsonPath("$.data.orderItems[0].productName").value("아이스 라떼"));
    }

    @Test
    @DisplayName("GET /api/v1/orders : 주문 목록 조회 성공")
    @WithMockUser(username = "user1@test.com", roles = "USER")
    void t3() throws Exception {
        // GIVEN
        orderService.create(testMember.getId(), List.of(testProduct1.getId()), List.of(1), "주소1");
        orderService.create(testMember.getId(), List.of(testProduct2.getId()), List.of(1), "주소2");

        // WHEN
        ResultActions resultActions = mvc.perform(
                        get("/api/v1/orders")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getItems"))
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("DELETE /api/v1/orders/{id} : 주문 삭제 성공")
    @WithMockUser(username = "user1@test.com", roles = "USER")
    void t4() throws Exception {
        // GIVEN
        Order order = orderService.create(
                testMember.getId(),
                List.of(testProduct1.getId()),
                List.of(1),
                "삭제될 주소"
        );

        // WHEN
        ResultActions resultActions = mvc.perform(
                        delete("/api/v1/orders/" + order.getId())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().methodName("delete"))
                .andExpect(jsonPath("$.resultCode").value("200-1"));


    }

    @Test
    @DisplayName("PUT /api/v1/orders/{id} : 주문 수정 성공")
    @WithMockUser(username = "user1@test.com", roles = "USER")
    void t5() throws Exception {
        // GIVEN: 수정할 주문을 미리 생성
        Order order = orderService.create(
                testMember.getId(),
                List.of(testProduct1.getId()), // 원래는 상품1 1개 주문
                List.of(1),
                "수정 전 주소"
        );

        // 수정할 내용: 상품1 5개, 상품2 10개, 주소 변경
        ApiV1OrderController.OrderModifyReqBody requestBody = new ApiV1OrderController.OrderModifyReqBody(
                List.of(testProduct1.getId(), testProduct2.getId()),
                List.of(5, 10),
                "수정 후 주소"
        );
        String jsonBody = objectMapper.writeValueAsString(requestBody);

        // WHEN
        ResultActions resultActions = mvc.perform(
                        put("/api/v1/orders/" + order.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().methodName("modify"))
                .andExpect(jsonPath("$.resultCode").value("200-1"));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        Rq testRq() {
            return new TestRq();
        }
    }

    static class TestRq extends Rq {
        private Member actor;

        public TestRq() {
            super(null, null); // 실제 req/resp는 필요 없으니 null
        }

        void setActor(Member actor) {
            this.actor = actor;
        }

        @Override
        public Member getActor() {
            return actor;
        }

        @Override
        public String getHeader(String name, String defaultValue) {
            return defaultValue;
        }

        @Override
        public void setHeader(String name, String value) {
        }

        @Override
        public String getCookieValue(String name, String defaultValue) {
            return defaultValue;
        }

        @Override
        public void setCookie(String name, String value) {
        }

        @Override
        public void deleteCookie(String name) {
        }
    }
}