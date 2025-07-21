package com.ll.domain.order.dto;

import com.ll.domain.order.entity.Order;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record OrderDto(
        int id,
        LocalDateTime createDate,
        LocalDateTime modifyDate,
        int totalPrice,
        String address,
        Order.OrderStatus order_status,
        List<OrderItemDto> orderItems
){
    public OrderDto(Order order) {
        this(
                order.getId(),
                order.getCreateDate(),
                order.getModifyDate(),
                order.getTotal_price(),
                order.getAddress(),
                order.getOrder_status(),

                order.getOrderItems()
                .stream()
                .map(OrderItemDto::new)
                .collect(Collectors.toList())
        );
    }
}