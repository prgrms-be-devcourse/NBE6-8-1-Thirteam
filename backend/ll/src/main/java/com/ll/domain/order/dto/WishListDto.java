package com.ll.domain.order.dto;

import com.ll.domain.order.entity.WishList;

import java.time.LocalDateTime;

public record WishListDto(
        int id,
        LocalDateTime createDate,
        LocalDateTime modifyDate,

        int productId,
        String productName,

        //프론트 장바구니 보고 추가한dto
        int productPrice,
        int quantity,
        int itemTotalPrice,

        int memberId
) {
    public WishListDto(WishList wishList) {
        this(
                wishList.getId(),
                wishList.getCreateDate(),
                wishList.getModifyDate(),

                // Product 정보 (null 체크)
                wishList.getProduct() != null ? wishList.getProduct().getId() : 0,
                wishList.getProduct() != null ? wishList.getProduct().getProductName() : "알 수 없음",

                //추가 부분.
                wishList.getProduct() != null ? wishList.getProduct().getPrice() : 0, // productPrice
                wishList.getQuantity(), // quantity
                wishList.getProduct() != null ? wishList.getProduct().getPrice() * wishList.getQuantity() : 0, // itemTotalPrice

                // Member 정보 (null 체크)
                wishList.getMember() != null ? wishList.getMember().getId() : 0
        );
    }
}
