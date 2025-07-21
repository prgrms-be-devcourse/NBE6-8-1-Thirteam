package com.ll.domain.member.repository;

import com.ll.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByName(String name);
    Optional<Member> findByApiKey(String apiKey);
}
