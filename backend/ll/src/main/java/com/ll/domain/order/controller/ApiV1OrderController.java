package com.ll.domain.order.controller;

import com.ll.domain.member.entity.Member;
import com.ll.domain.order.dto.OrderDto;
import com.ll.domain.order.entity.Order;
import com.ll.domain.order.service.OrderService;
import com.ll.global.rq.Rq;
import com.ll.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class ApiV1OrderController {
    private final OrderService orderService;
    private final Rq rq;

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "다건 조회")
    public RsData<List<OrderDto>> getItems(){
        List<Order> orders = orderService.findAll();
        List<OrderDto> dtoList = orders
                .stream().
                map(OrderDto::new)
                .toList();

        return new RsData<>(
                "200-1",
                "주문 목록을 조회했습니다.",
                dtoList);

    }

    @GetMapping("/{id}")
    @Operation(summary = "단건 조회")
    public RsData<OrderDto> getItem(@PathVariable int id) {
        Order order = orderService.findById(id).get();

        return new RsData<>(
                "200-1",
                "주문을 조회했습니다.",
                new OrderDto(order)
        );
    }


    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "삭제")
    public RsData<Void> delete(@PathVariable int id) {
        Member actor = rq.getActor();
        Order order = orderService.findById(id).get();

        order.checkActorCanDelete(actor);

        orderService.delete(order);

        return new RsData<>(
                "200-1",
                "%d번 주문이 삭제되었습니다.".formatted(id)
        );
    }

    record OrderCreateReqBody(
            @NotNull(message = "상품 ID 목록은 필수이다.")
            @Size(min = 1, message = "최소 하나의 상품은 포함해야 한다.")
            List<Integer> productIds, // 주문할 상품들의 ID 목록

            @NotNull(message = "수량 목록은 필수이다.")
            @Size(min = 1, message = "최소 하나의 수량은 포함해야 한다.")
            List<Integer> quantities, // 각 상품에 대한 수량 목록 (productIds와 순서 및 개수 일치)

            @NotBlank(message = "주소는 필수이다.")
            String address // 배송 주소
    ){}

    @PostMapping
    @Transactional
    @Operation(summary = "작성")
    public RsData<OrderDto> write(@Valid @RequestBody OrderCreateReqBody reqBody) {
        Member actor = rq.getActor();

        Order order = orderService.create(
                actor.getId(),
                reqBody.productIds(),
                reqBody.quantities(),
                reqBody.address()
        );
        return new RsData<>(
                "201-1",
                "%d번 주문이 작성되었습니다.".formatted(order.getId()),
                new OrderDto(order)
        );
    }

    record  OrderModifyReqBody(
            @NotNull(message = "상품 ID 목록은 필수이다.")
            @Size(min = 1, message = "최소 하나의 상품은 포함해야 한다.")
            List<Integer> productIds, // 주문할 상품들의 ID 목록

            @NotNull(message = "수량 목록은 필수이다.")
            @Size(min = 1, message = "최소 하나의 수량은 포함해야 한다.")
            List<Integer> quantities, // 각 상품에 대한 수량 목록 (productIds와 순서 및 개수 일치)

            @NotBlank(message = "주소는 필수이다.")
            String address // 배송 주소
    ) {
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "수정")
    public RsData<Void> modify(
            @PathVariable int id,
            @Valid @RequestBody OrderModifyReqBody reqBody
    ) {
        Member actor = rq.getActor();
        Order order = orderService.findById(id).get();

        order.checkActorCanModify(actor);

        orderService.modify(
                order,
                reqBody.productIds,
                reqBody.quantities,
                reqBody.address
        );

        return new RsData<>(
                "200-1",
                "%d번 주문이 수정되었습니다.".formatted(order.getId())
        );
    }

    @GetMapping("/my")
    @Transactional(readOnly = true)
    @Operation(summary = "사용자 주문 목록 조회")
    public RsData<List<OrderDto>> getMyOrders() {
        Member actor = rq.getActor();

        List<Order> myOrders = orderService.findByMember(actor);

        return new RsData<>(
                "200-1",
                "나의 주문 목록 조회 성공",
                myOrders.stream()
                        .map(OrderDto::new)
                        .toList()
        );
    }

}
