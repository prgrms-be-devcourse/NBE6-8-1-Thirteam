package com.ll.domain.member.entity;

import com.ll.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseEntity {
    @Column(unique = true)
    private String email;
    // String user_id;
    private String password;
    private String name;
    private String address;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(unique = true)
    private String apiKey;

    public enum Role {
        ADMIN, USER
    }

    public void modifyApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Builder
    public Member(String email, String password, String name, String address, Role role) {
        this.email = email; // id로 사용 예정
        // this.user_id = user_id; // 사용처 없음. identity 키의 경우 BaseEntity에서 이미 선언됨.
        this.password = password;
        this.name = name;
        this.address = address;
        this.role = role;
        this.apiKey = UUID.randomUUID().toString();
    }

    public Member(int id, String email, String name) {
        setId(id);
        this.email = email;
        this.name = name;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getAuthoritiesAsStringList()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    private List<String> getAuthoritiesAsStringList() {
        List<String> authorities = new ArrayList<>();

        if(isAdmin()) authorities.add("ROLE_ADMIN");

        return authorities;
    }
}
