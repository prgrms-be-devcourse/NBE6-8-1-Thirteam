package com.ll.domain.order.service;

import com.ll.domain.member.entity.Member;
import com.ll.domain.member.repository.MemberRepository;
import com.ll.domain.order.entity.WishList;
import com.ll.domain.order.repository.WishListRepository;
import com.ll.domain.product.entity.Product;
import com.ll.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WishListService {
    private final WishListRepository wishListRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public WishList create(int memberId, int productId, int quantity){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원이다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품이다."));
        if(quantity<=0){
            throw new IllegalArgumentException("수량은 1 이상이어야 한다.");
        }

        // 이미 찜 목록에 있는지 확인
        Optional<WishList> existWishList = wishListRepository.findByMemberAndProduct(member, product);

        WishList wishList;
        if (existWishList.isPresent()) {
            wishList = existWishList.get();
            wishList.setQuantity(wishList.getQuantity() + quantity);
        } else {
            wishList = new WishList(member, product, quantity);
        }

        return wishListRepository.save(wishList);
    }
    //여긴끝

    public List<WishList> getMemberWishList(int memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원이다."));
        return wishListRepository.findByMember(member);
    }

    public Optional<WishList> findById(int id) {
        return wishListRepository.findById(id);
    }

    public void delete(WishList wishList) {
        wishListRepository.delete(wishList);
    }

    public void setProductQuantityInWishList(int memberId, int productId, int newQuantity) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));


        WishList wishList = wishListRepository.findByMemberAndProduct(member, product)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장바구니 항목입니다."));

        if (newQuantity == 0) {
            wishListRepository.delete(wishList);
        } else {
            wishList.setQuantity(newQuantity);
            wishListRepository.save(wishList);
        }
    }

    public void removeWishListItem(int memberId, int productId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        WishList wishList = wishListRepository.findByMemberAndProduct(member, product)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장바구니 항목입니다."));

        wishListRepository.delete(wishList);
    }

    public void clearWishList(int memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원이다."));
        List<WishList> memberWishLists = wishListRepository.findByMember(member);
        wishListRepository.deleteAll(memberWishLists);
    }
}
