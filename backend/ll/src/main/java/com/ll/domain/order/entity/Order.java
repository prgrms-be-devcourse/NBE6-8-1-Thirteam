package com.ll.domain.order.entity;

import com.ll.domain.member.entity.Member;
import com.ll.global.exception.ServiceException;
import com.ll.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "orders")
public class Order extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 300)
    private String address;

    private int total_price; // 총 주문 금액

    @Enumerated(EnumType.STRING) // Enum의 이름을 DB에 문자열로 저장하도록 설정
    private OrderStatus order_status; // 원래 Enum 이름 유지

    public enum OrderStatus {
        ORDERED,    // 주문 완료
        DELIVERED   // 배송 완료
    }

    @OneToMany(mappedBy = "order", fetch = LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        if(orderItem.getOrder() != this) {
            orderItem.setOrder(this); // 양방향 관계 보장
        }
    }

    public void clearOrderItems() {
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(null); // 연관관계 해제
        }
        orderItems.clear();
    }

    public int calculateTotalPrice() {
        return orderItems.stream()
                .mapToInt(OrderItem::getTotal_price)
                .sum();
    }

    public void updateTotalPrice() {
        this.total_price = calculateTotalPrice();
    }

    public void checkActorCanModify(Member actor) {
        if (!this.member.equals(actor)) {
            throw new ServiceException("403-1", "해당 주문을 수정할 권한이 없습니다.");
        }
    }

    public void checkActorCanDelete(Member actor) {
        if (!this.member.equals(actor)) {
            throw new ServiceException("403-2", "해당 주문을 삭제할 권한이 없습니다.");
        }
    }
}
