package com.ll.domain.order.dto;

import com.ll.domain.order.entity.OrderItem;

public record OrderItemDto(
        int id,
        String productName,
        String productImage,
        int quantity,
        int productPrice,
        int totalPrice
) {
    public OrderItemDto(OrderItem orderItem) {
        this(
                orderItem.getId(),
                orderItem.getProduct().getProductName(),
                orderItem.getProduct().getProductImage(),
                orderItem.getQuantity(),
                orderItem.getProduct_price(),
                orderItem.getTotal_price()
        );
    }
}
