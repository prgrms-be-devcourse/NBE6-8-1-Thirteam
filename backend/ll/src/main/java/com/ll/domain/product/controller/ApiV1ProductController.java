package com.ll.domain.product.controller;

import com.ll.domain.product.dto.CreateProductRequestDto;
import com.ll.domain.product.dto.MenuProductDto;
import com.ll.domain.product.dto.UpdateProductRequestDto;
import com.ll.domain.product.entity.Product;
import com.ll.domain.product.service.ProductService;
import com.ll.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ApiV1ProductController {

    private final ProductService productService;

    @Transactional
    @PostMapping(value = "/create")
    @Operation(summary = "상품 등록", description = "새로운 상품을 등록합니다.\n" +
            "'contentType: MediaType.APPLICATION_JSON' 형식으로 요청해야 합니다.")
    public RsData<Product> createProduct(
            @RequestBody CreateProductRequestDto productDto
    ) {
        Product product = productService.createProduct(productDto);
        return new RsData<>("200-1", "상품이 등록되었습니다.", product);
    }

    @Transactional(readOnly = true)
    @GetMapping("/category/{category}")
    @Operation(summary = "카테고리별 메뉴 상품 조회", description = "특정 카테고리에 속하는 메뉴 상품 목록을 조회합니다.\n" +
    "'category'는 대문자로 입력해야 합니다. 현재 카테고리는 'ALL', 'COFFEE', 'TEA', 'JUICE 'DESSERT'가 있습니다." +
    " url 예시: /api/v1/products/category/ALL , /api/v1/products/category/COFFEE 등")
    public RsData<List<MenuProductDto>> getMenuProductsByCategory(@PathVariable String category) {
        List<MenuProductDto> menuProducts = productService.getMenuProductsByCategory(category);
        return new RsData<>("200-1", "%s 메뉴 상품 목록을 조회했습니다.".formatted(category), menuProducts);
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    @Operation(summary = "단건 상품 상세 조회", description = "특정 상품 상세 데이터를 조회합니다.\n" +
            " url 예시: /api/v1/products/1 , /api/v1/products/2 등")
    public RsData<Product> getProduct(@PathVariable int id) {
        Product product = productService.getProduct(id);
        return new RsData<>("200-1", "%d번 상품 목록을 조회했습니다.".formatted(id), product);
    }

    @Transactional(readOnly = true)
    @GetMapping
    @Operation(summary = "다건 상품 상세 조회", description = "모든 상품 상세 데이터를 조회합니다.")
    public RsData<List<Product>> getProducts() {
        List<Product> products = productService.getProducts();
        return new RsData<>("200-1", "상세 상품 목록을 조회했습니다.", products);
    }

    @Transactional
    @DeleteMapping("/{id}")
    @Operation(summary = "단건 상품 삭제", description = "단건 상품을 삭제합니다.")
    public RsData<Product> deleteProduct(@PathVariable int id) {
        Product product = productService.deleteProduct(id);
        return new RsData<>("200-1", "%d번 상품을 삭제했습니다.".formatted(id), product);
    }

    @Transactional
    @PutMapping("/update")
    @Operation(summary = "상품 수정", description = "단건 상품을 수정합니다.")
    public RsData<Product> updateProduct(@RequestBody UpdateProductRequestDto updateProductRequestDto) {
        Product product = productService.updateProduct(updateProductRequestDto);
        return new RsData<>("200-1", "%d번 상품을 수정했습니다.".formatted(updateProductRequestDto.getId()), product);
    }

    @Transactional
    @PatchMapping("/{id}/stock/increase")
    @Operation(summary = "재고 증가")
    public RsData<Void> increaseStock(
            @PathVariable int id,
            @RequestParam int quantity
    ) {
        productService.increaseStock(id, quantity);

        return new RsData<>(
                "200-1",
                "상품 ID %d의 재고가 %d만큼 증가했습니다.".formatted(id, quantity)
        );
    }

    @Transactional
    @PatchMapping("/{id}/stock/decrease")
    @Operation(summary = "재고 감소")
    public RsData<Void> decreaseStock(
            @PathVariable int id,
            @RequestParam int quantity
    ) {
        productService.decreaseStock(id, quantity);

        return new RsData<>(
                "200-2",
                "상품 ID %d의 재고가 %d만큼 감소했습니다.".formatted(id, quantity)
        );
    }

}

