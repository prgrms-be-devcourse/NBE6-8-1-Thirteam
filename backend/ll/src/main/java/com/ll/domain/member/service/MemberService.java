package com.ll.domain.member.service;

import com.ll.domain.member.entity.Member;
import com.ll.domain.member.repository.MemberRepository;
import com.ll.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthTokenService authTokenService;
    private final PasswordEncoder passwordEncoder;

    // [추가] application.yml의 값을 주입받는 필드
    @Value("${app.admin.registration-code}")
    private String adminRegistrationCode;

    // ------- 로그인 기능 -------- //
    public Member addUserMember(String email, String password, String name, String address){
        String encodedPassword = passwordEncoder.encode(password);
        Member member = Member.builder()
                .email(email)
                .password(encodedPassword)
                .name(name)
                .address(address)
                .role(Member.Role.USER)
                .build();

        return memberRepository.save(member);
    }
    public Member addAdminMember(String email, String password, String name, String address, String adminCode){

        if (!adminRegistrationCode.equals(adminCode)) {
            throw new ServiceException("403-1", "관리자 등록 코드가 유효하지 않습니다.");
        }
        String encodedPassword = passwordEncoder.encode(password);
        Member member = Member.builder()
                .email(email)
                .password(encodedPassword)
                .name(name)
                .address(address)
                .role(Member.Role.ADMIN)
                .build();

        return memberRepository.save(member);
    }
    public void checkPassword(Member member, String password){
        if(!passwordEncoder.matches(password, member.getPassword()))
            throw new ServiceException("401-2", "비밀번호가 일치하지 않습니다.");
    }

    // ------- 일반 서비스 -------- //
    public long count() {return memberRepository.count();}
    public Optional<Member> findById(Integer id){return memberRepository.findById(id);}
    public List<Member> findAll(){return memberRepository.findAll();}
    public Optional<Member> findByEmail(String email){return memberRepository.findByEmail(email);}
    public Optional<Member> findByName(String name){return  memberRepository.findByName(name);}
    public void deleteById(Integer id){memberRepository.deleteById(id);}
    public void flush(){memberRepository.flush();}

    // ------- 인증, 인가 -------- //
    public Map<String, Object> payload(String accessToken) { return authTokenService.payload(accessToken); }
    public String genAccessToken(Member member) { return authTokenService.genAccessToken(member); }
    public Optional<Member> findByApiKey(String apiKey) { return memberRepository.findByApiKey(apiKey); }
}
