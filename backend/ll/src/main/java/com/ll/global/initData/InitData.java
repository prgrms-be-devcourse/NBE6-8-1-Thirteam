package com.ll.global.initData;


import com.ll.domain.member.service.MemberService;
import com.ll.domain.order.controller.ApiV1OrderController;
import com.ll.domain.order.entity.Order;
import com.ll.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class InitData {

    @Autowired
    @Lazy
    private InitData self;

    @Value("${product.upload-dir}")
    private String dirName;

    @Autowired
    private MemberService memberService;

    @Autowired
    private OrderService orderService ;

    @Bean
    ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            self.work1();
            self.work2();
        };
    }

    public void work1() {
        try {
            String uploadDir = System.getProperty("user.dir") + "/" + dirName;

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        }catch (Exception e) {
            throw new RuntimeException("이미지 파일 디렉토리 생성 실패", e);
        }
    }

    public void work2(){
        memberService.addUserMember("user1@test.com", "1234", "테스터1", "서울시 강남구");
        memberService.addUserMember("user2@test.com", "1234", "테스터2", "경기도 강남구");
        memberService.addUserMember("user3@test.com", "1234", "테스터3", "강원도 강남구");

        orderService.create(
                1,
                List.of(1, 1),
                List.of(1, 2),
                "서울시 서초구"
        );

        orderService.create(
                2,
                List.of(6, 7),
                List.of(7, 12),
                "경기도 서초구"
        );

        orderService.create(
                3,
                List.of(15, 16),
                List.of(12, 23),
                "강원도 서초구"
        );

        orderService.create(
                3,
                List.of(15, 16),
                List.of(12, 23),
                "강원도 서초구"
        );

        orderService.create(
                3,
                List.of(15, 16),
                List.of(12, 23),
                "강원도 서초구"
        );

        orderService.create(
                3,
                List.of(15, 16),
                List.of(12, 23),
                "강원도 서초구"
        );
    }

}
