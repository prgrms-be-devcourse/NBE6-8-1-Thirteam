package com.ll.domain.order.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ll.domain.product.entity.Product;
import com.ll.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "order_items")
public class OrderItem extends BaseEntity {

    private int quantity;

    private int product_price; // 주문 당시 상품 가격

    private int total_price;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;


    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public OrderItem(Order order, Product product, int quantity){
        setOrder(order);
        this.product = product;
        this.quantity = quantity;
        this.product_price = product.getPrice();
        this.total_price = product_price * quantity;
    }
}
