package com.ll.global.security;

import com.ll.domain.member.entity.Member;
import com.ll.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("일치하는 이메일을 찾을 수 없습니다."));

        return new SecurityUser(
                member.getId(),
                member.getEmail(),
                "",
                member.getName(),
                member.getAuthorities()
        );
    }
}
