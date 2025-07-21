package com.ll.domain.order.repository;

import com.ll.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    //N+1문제 해결위한 fetch join
    @Query("SELECT o FROM Order o JOIN FETCH o.orderItems oi JOIN FETCH oi.product WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") int id);

    @Query("SELECT o FROM Order o")
    List<Order> findAllWithItems();

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.orderItems oi " +
            "LEFT JOIN FETCH oi.product " +
            "WHERE o.member.id = :memberId AND o.order_status = 'ORDERED' " + // 필요하면 조건 추가
            "ORDER BY o.id DESC")
    List<Order> findByMemberIdWithItems(@Param("memberId") int memberId);


    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.order_status = 'DELIVERED' WHERE o.order_status = 'ORDERED'")
    void bulkUpdateOrderStatusToDelivered();
}
