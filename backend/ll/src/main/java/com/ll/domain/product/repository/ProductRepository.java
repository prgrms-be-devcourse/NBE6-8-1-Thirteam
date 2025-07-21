package com.ll.domain.product.repository;

import com.ll.domain.product.entity.Product;
import com.ll.domain.product.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    // 카테고리별 상품 조회
    List<Product> findByCategory(ProductCategory category);
}
