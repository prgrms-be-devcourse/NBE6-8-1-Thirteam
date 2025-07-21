package com.ll.domain.member.controller;

import com.ll.domain.member.dto.MemberDto;
import com.ll.domain.member.entity.Member;
import com.ll.domain.member.service.MemberService;
import com.ll.global.exception.ServiceException;
import com.ll.global.rq.Rq;
import com.ll.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@Tag(name = "ApiV1MemberController", description = "API 회원(사용자, 관리자) 컨트롤러")
public class ApiV1MemberController {
    private final MemberService memberService;
    private final Rq rq;

    record SignUpRequest(
            @Email
            @NotBlank
            String email,

            @NotBlank
            String password,

            @NotBlank
            String name,

            @NotBlank
            String address
    ) {
    }



    @PostMapping("/signup/user") // 사용자 회원가입
    @Operation(summary = "사용자 회원 가입")
    public RsData<MemberDto> signUpUser(@Valid @RequestBody SignUpRequest signUpRequest){
        Member member = memberService.addUserMember(
                signUpRequest.email(),
                signUpRequest.password(),
                signUpRequest.name(),
                signUpRequest.address()
        );

        return new RsData<>(
                "201-1",
                "%s 사용자님 환영합니다. 회원가입이 완료되었습니다.".formatted(member.getName()),
                new MemberDto(member)
        );
    }

    record AdminSignUpRequest(
            @Email @NotBlank String email,
            @NotBlank String password,
            @NotBlank String name,
            @NotBlank String address,
            @NotBlank String adminCode // 관리자 등록 코드 필드 추가
    ) {}

    @PostMapping("/signup/admin") // 관리자 회원 가입
    @Operation(summary = "관리자 회원 가입")
    public RsData<MemberDto> signUpAdmin(@Valid @RequestBody AdminSignUpRequest signUpRequest){
        Member member = memberService.addAdminMember(
                signUpRequest.email(),
                signUpRequest.password(),
                signUpRequest.name(),
                signUpRequest.address(),
                signUpRequest.adminCode()
        );

        return new RsData<>(
                "201-2",
                "%s 관리자님 환영합니다. 회원가입이 완료되었습니다.".formatted(member.getName()),
                new MemberDto(member)
        );
    }

    record LoginRequest (
            @Email
            @NotBlank
            String email,

            @NotBlank
            String password
    ) {
    }

    record LoginResponse (
            MemberDto item,
            String apiKey,
            String token
    ) {
    }

    @PostMapping("/login")
    @Operation(summary = "로그인")
    public RsData<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest){ // 로그인
        Member member = memberService.findByEmail(loginRequest.email())
                .orElseThrow(() -> new ServiceException("401-1", "가입되지 않은 이메일입니다."));

        memberService.checkPassword(
                member,
                loginRequest.password()
        );

        String accessToken = memberService.genAccessToken(member);

        rq.setCookie("apiKey", member.getApiKey());
        rq.setCookie("accessToken", accessToken);

        return new RsData<>(
                "200-1",
                "%s님 환영합니다.".formatted(member.getName()),
                new LoginResponse(
                        new MemberDto(member),
                        member.getApiKey(),
                        accessToken
                )
        );
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    public RsData<Void> logout(){
        rq.deleteCookie("apiKey");
        rq.deleteCookie("accessToken");

        return new RsData<>(
                "200-1",
                "로그아웃 되었습니다."
        );
    }

    @GetMapping("/me")
    @Operation(summary = "현재 로그인된 사용자 확인")
    public RsData<MemberDto> getMe(){
        int currentActorId = rq.getActor().getId();
        Member currentActor = memberService.findById(currentActorId).orElse(null);

        if(currentActor == null) {
            return new RsData<>(
                    "402-1",
                    "로그인이 필요합니다."
            );
        }

        if(!currentActor.isAdmin()) {
            MemberDto memberDto = new MemberDto(currentActor);
            return new RsData<>(
                    "202-1",
                    "현재 로그인된 사용자 정보입니다.",
                    memberDto
            );
        }

        if(currentActor.isAdmin()) {
            MemberDto memberDto = new MemberDto(currentActor);
            return new RsData<>(
                    "202-2",
                    "현재 로그인된 관리자 정보입니다.",
                    memberDto
            );
        }

        return new RsData<>(
                "402-2",
                "잘못된 로그인 정보입니다."
        );
    }

    @GetMapping("/admin") // 다건 조회
    @Operation(summary = "회원 목록 다건 조회")
    public List<MemberDto> getMembers(){ // id로 멤버 찾기
        List<Member> members = memberService.findAll();

        return members
                .stream()
                .map(MemberDto::new)
                .toList();
    }

    @GetMapping("/admin/{id}") // 단건 조회 ( id 기반 )
    @Operation(summary = "회원 목록 단건 조회(id 기반)")
    public MemberDto getMember(@PathVariable Integer id){ // id로 멤버 찾기
        Member member = memberService.findById(id).get();

        return new MemberDto(member);
    }
}
