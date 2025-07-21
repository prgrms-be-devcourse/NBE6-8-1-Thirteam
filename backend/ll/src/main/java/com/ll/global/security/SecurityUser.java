package com.ll.global.security;

import com.ll.domain.member.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class SecurityUser extends User {
    private int id;
    private String name;

    public SecurityUser(
            int id,
            String email,
            String password,
            String name,
            Collection<? extends GrantedAuthority> authorities
    ) {
        super(email, password, authorities);
        this.id = id;
        this.name = name;
    }
}
