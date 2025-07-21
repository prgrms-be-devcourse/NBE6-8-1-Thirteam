package com.ll.domain.order.controller;

import com.ll.domain.order.dto.WishListDto;
import com.ll.domain.order.entity.WishList;
import com.ll.domain.order.service.WishListService;
import com.ll.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wishlist")
@Tag(name = "ApiV1WishListController", description = "장바구니 목록 컨트롤러")
public class ApiV1WishListController {
    private final WishListService wishListService;

    @GetMapping("/member/{memberId}")
    @Transactional(readOnly = true)
    @Operation(summary = "특정 회원의 wishList 목록 조회")
    public RsData<List<WishListDto>> getWishList(@PathVariable("memberId") int memberId) {
        List<WishList> wishLists = wishListService.getMemberWishList(memberId);

        List<WishListDto> dtoList = wishLists
                .stream()
                .map(WishListDto::new)
                .toList();

        return new RsData<>(
                "200-1",
                "위시리스트 목록을 조회했습니다.",
                dtoList
        );
    }

    //추가
    record AddWishListRequest(
            @NotNull(message = "회원 ID는 필수이다.")
            int memberId,
            @NotNull(message = "상품 ID는 필수이다.")
            int productId,
            @Min(value =1, message = "수량은 1 이상이어야 한다.")
            int quantity
    ) {
    }

    @PostMapping
    @Transactional
    @Operation(summary = "wishList 목록에 상품 추가")
    public RsData<WishListDto> create(@Valid @RequestBody
                                      AddWishListRequest req) {
        WishList wishList = wishListService.create(
                req.memberId(),
                req.productId(),
                req.quantity()
        );

        return new RsData<>(
                "201-1",
                "상품이 위시리스트에 담겼습니다.",
                new WishListDto(wishList)
        );
    }

    record SetWishListQuantityReqBody(
            int memberId,
            int productId,
            int newQuantity
    ) {}

    @PutMapping("/quantity")
    @Transactional
    @Operation(summary = "찜 목록(장바구니) 항목 수량 변경")
    public RsData<Void> setWishListQuantity(@RequestBody SetWishListQuantityReqBody req) {
        wishListService.setProductQuantityInWishList(
                req.memberId, req.productId, req.newQuantity
        );
        return new RsData<>(
                "200-1",
                "위시리스트 수량이 변경되었습니다."
        );
    }

    record SetWishListDeleteReqBody(
            int memberId,
            int productId
    ) {}

    @DeleteMapping
    @Transactional
    @Operation(summary = "위시리스트 상품 제거")
    public RsData<Void> delete(@RequestBody SetWishListDeleteReqBody req) {
        wishListService.removeWishListItem(
                req.memberId, req.productId
        );
        return new RsData<>(
                "200-1",
                "위시리스트가 삭제되었습니다."
        );
    }

    @DeleteMapping("/member/{memberId}")
    @Transactional
    @Operation(summary = "회원의 위시리스트 비우기")
    public RsData<Void> clearMemberWishList(@PathVariable int memberId) {
        wishListService.clearWishList(memberId);
        return new RsData<>(
                "200-1",
                "위시리스트가 모두 삭제되었습니다."
        );
    }
}
