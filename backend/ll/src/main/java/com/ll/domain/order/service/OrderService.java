package com.ll.domain.order.service;

import com.ll.domain.member.entity.Member;
import com.ll.domain.member.repository.MemberRepository;
import com.ll.domain.order.entity.Order;
import com.ll.domain.order.entity.OrderItem;
import com.ll.domain.order.repository.OrderRepository;
import com.ll.domain.product.entity.Product;
import com.ll.domain.product.repository.ProductRepository;
import com.ll.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j //로그확인용
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public List<Order> findAll() {
        return orderRepository.findAllWithItems();
    }

    public Optional<Order> findById(int id) {
        return orderRepository.findByIdWithItems(id);
    }


    public void delete(Order order) {
        // 주문 삭제 시 재고 복원 해야하는지 여부는 비즈니스 로직에 따라 결정
        //restoreStock(order);
        orderRepository.delete(order);
    }

    public Order create(int memberId, List<Integer> productIds,  List<Integer> quantities, String address) {
        validateProductInput(productIds, quantities);

        Member member = memberRepository.findById(memberId).
                orElseThrow(() -> new ServiceException("404-1", "해당 회원이 존재하지 않습니다."));

        Order order = new Order();
        order.setMember(member);
        order.setOrder_status(Order.OrderStatus.ORDERED);

        fillOrderWithItems(order, productIds, quantities, address);

        return orderRepository.save(order);
    }

    public Order modify(Order order, List<Integer> productIds, List<Integer> quantities, String address) {
        // 배송 완료된 주문은 수정 불가
        if (order.getOrder_status() == Order.OrderStatus.DELIVERED) {
            throw new ServiceException("400-5", "배송 완료된 주문은 수정할 수 없습니다.");
        }

        validateProductInput(productIds, quantities);

        restoreStock(order);
        order.clearOrderItems();

        fillOrderWithItems(order, productIds, quantities, address);

        return orderRepository.save(order);
    }

    private void validateProductInput(List<Integer> productIds, List<Integer> quantities) {
        if (productIds.size() != quantities.size()) {
            throw new ServiceException("400-4", "상품 ID 목록과 수량 목록의 크기가 일치하지 않습니다.");
        }
    }

    private void fillOrderWithItems(Order order, List<Integer> productIds, List<Integer> quantities, String address) {
        for (int i = 0; i < productIds.size(); i++) {
            Product product = productRepository.findById(productIds.get(i))
                    .orElseThrow(() -> new ServiceException("404-2", "해당 상품이 존재하지 않습니다."));
            int quantity = quantities.get(i);

            checkStock(product, quantity);
            product.decreaseStock(quantity);

            OrderItem orderItem = new OrderItem(order, product, quantity);
            order.addOrderItem(orderItem);
        }
        order.setAddress(address);
        order.updateTotalPrice();
    }

    private void checkStock(Product product, int quantity) {
        if (product.getStock() < quantity) {
            throw new ServiceException("400-6", "상품 %s의 재고가 부족합니다.".formatted(product.getProductName()));
        }
    }

    public void restoreStock(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.increaseStock(item.getQuantity());
        }
    }

    public List<Order> findByMember(Member member) {
        return orderRepository.findByMemberIdWithItems(member.getId());
    }



    @Scheduled(cron = "0 0 14 * * *", zone = "Asia/Seoul")
    @Transactional
    public void updateOrderStatusToDelivered() {
        orderRepository.bulkUpdateOrderStatusToDelivered();
    }
}
