package com.ll.domain.member.dto;

import com.ll.domain.member.entity.Member;
import lombok.NonNull;

public record MemberDto (
        @NonNull int id,
        @NonNull String name,
        @NonNull String address,
        @NonNull Member.Role role
) {
    public MemberDto(Member member) {
        this(
                member.getId(),
                member.getName(),
                member.getAddress(),
                member.getRole());
    }
}
