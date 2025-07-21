package com.ll.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.domain.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiV1MemberControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        mvc.perform(post("/api/v1/members/logout"));
    }

    @Test
    @DisplayName("사용자 회원가입 & 로그인 통합 테스트")
    void testUserSignUpAndLogin() throws Exception {
        Map<String, String> signUpRequest = Map.of(
                "email", "test@example.com",
                "password", "1234",
                "name","사용자 테스트 유저",
                "address", "서울"
        );

        mvc.perform(post("/api/v1/members/signup/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.msg").value("사용자 테스트 유저 사용자님 환영합니다. 회원가입이 완료되었습니다."));

        Map<String, String> loginRequest = Map.of(
                "email", "test@example.com",
                "password", "1234"
        );

        mvc.perform(post("/api/v1/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("사용자 테스트 유저님 환영합니다."))
                .andExpect(jsonPath("$.data.item.role").value("USER"));
    }

    @Test
    @DisplayName("관리자 회원가입 & 로그인 통합 테스트")
    void testAdminSignUpAndLogin() throws Exception {
        Map<String, String> signUpRequest = Map.of(
                "email", "test@example.com",
                "password", "1234",
                "name","관리자 테스트 유저",
                "address", "서울"
        );

        mvc.perform(post("/api/v1/members/signup/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("201-2"))
                .andExpect(jsonPath("$.msg").value("관리자 테스트 유저 관리자님 환영합니다. 회원가입이 완료되었습니다."));

        Map<String, String> loginRequest = Map.of(
                "email", "test@example.com",
                "password", "1234"
        );

        mvc.perform(post("/api/v1/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("관리자 테스트 유저님 환영합니다."))
                .andExpect(jsonPath("$.data.item.role").value("ADMIN"));
    }

    @Test
    @DisplayName("JWT 토큰 인증 테스트")
    void testJwt() throws Exception {
        // 회원가입
        Map<String, String> signUpRequest = Map.of(
                "email", "test@example.com",
                "password", "1234",
                "name", "테스트 유저",
                "address", "서울"
        );

        mvc.perform(post("/api/v1/members/signup/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.msg").value("테스트 유저 사용자님 환영합니다. 회원가입이 완료되었습니다."));

        Map<String, String> loginRequest = Map.of(
                "email", "test@example.com",
                "password", "1234"
        );

        MvcResult loginResult = mvc.perform(post("/api/v1/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("테스트 유저님 환영합니다."))
                .andExpect(jsonPath("$.data.item.role").value("USER"))
                .andReturn();

        String accessToken = loginResult.getResponse().getCookie("accessToken").getValue();
        String apiKey = loginResult.getResponse().getCookie("apiKey").getValue();

        // 로그인 이후 확인.
        mvc.perform(get("/api/v1/members/me")
                        .cookie(
                                new Cookie("accessToken", accessToken),
                                new Cookie("apiKey", apiKey)
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("202-1"))
                .andExpect(jsonPath("$.msg").value("현재 로그인된 사용자 정보입니다."))
                .andExpect(jsonPath("$.data.name").value("테스트 유저"))
                .andExpect(jsonPath("$.data.address").value("서울"))
                .andExpect(jsonPath("$.data.role").value("USER"));

        mvc.perform(post("/api/v1/members/logout"));
    }
}


