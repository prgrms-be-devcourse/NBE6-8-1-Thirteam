package com.ll.domain.order.repository;

import com.ll.domain.member.entity.Member;
import com.ll.domain.order.entity.WishList;
import com.ll.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishListRepository extends JpaRepository<WishList, Integer> {

    List<WishList> findByMember(Member member);

    Optional<WishList> findByMemberAndProduct(Member member, Product product);


}
