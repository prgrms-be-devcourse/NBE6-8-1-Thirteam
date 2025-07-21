package com.ll.domain.product.entity;

import com.ll.global.exception.ServiceException;
import com.ll.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    private String productName; // 상품명

    private int price; // 가격

    private String description; // 상품 설명

    private String productImage; // 상품 이미지 URL

    private int stock; // 재고 수량

    @Enumerated(EnumType.STRING)
    private ProductCategory category; // 카테고리

    @Enumerated(EnumType.STRING) // enum 타입 사용, 문자열로 저장
    private ProductStatus status; // 상품 상태 (SALE - 판매 중, SOLD_OUT - 품절, STOPPED - 판매 중지)


    public void increaseStock(int quantity) {
        stock += quantity;
    }

    public void decreaseStock(int quantity) {
        if (stock < quantity) {
            throw new ServiceException("400-1", "재고가 부족합니다.");
        }

        stock -= quantity;

        if(stock == 0) {
            status = ProductStatus.SOLD_OUT; // 재고가 0이 되면 품절 상태로 변경
        }
    }
}


