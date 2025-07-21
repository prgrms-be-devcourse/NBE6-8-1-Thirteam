package com.ll.domain.order.entity;

import com.ll.domain.member.entity.Member;
import com.ll.domain.product.entity.Product;
import com.ll.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@Setter
public class WishList extends BaseEntity {


    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private int quantity;


    public WishList(Member member,Product product, int quantity) {
        this.member = member;
        this.product = product;
        this.quantity = quantity;
    }

}
