package com.ll.domain.product.service;

import com.ll.domain.product.dto.CreateProductRequestDto;
import com.ll.domain.product.dto.MenuProductDto;
import com.ll.domain.product.dto.UpdateProductRequestDto;
import com.ll.domain.product.entity.Product;
import com.ll.domain.product.entity.ProductCategory;
import com.ll.domain.product.entity.ProductStatus;
import com.ll.domain.product.repository.ProductRepository;
import com.ll.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;


    @Value("${product.upload-dir}")
    private String dirName;

    public Product createProduct(CreateProductRequestDto productDto) {

        Product product = Product.builder()
                .productName(productDto.getProductName())
                .price(productDto.getPrice())
                .description(productDto.getDescription())
                .productImage(productDto.getImageUrl())
                .stock(productDto.getStock())
                .status(ProductStatus.valueOf(productDto.getStatus()))
                .category(ProductCategory.valueOf(productDto.getCategory()))
                .build();

        productRepository.save(product);
        return product;
    }
    // 이미지 파일 저장 메서드
//    private String saveFile(MultipartFile file) {
//            String uploadDir = System.getProperty("user.dir") + "/" + dirName;
//
//            Path uploadPath = Paths.get(uploadDir);
//            if (!Files.exists(uploadPath)) {
//                Files.createDirectories(uploadPath);
//            }
//
//            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//            Path filePath = uploadPath.resolve(fileName);
//            file.transferTo(filePath.toFile());
//            return "/productImages/" + fileName;
//        } catch (Exception e) {
//            throw new RuntimeException("파일 저장 실패", e);
//        }
//    }

    public List<MenuProductDto> getMenuProductsByCategory(String category) {
        List<Product> products;
        if (category.equals("ALL")) {
            products = productRepository.findAll();
        }else {
            products = productRepository.findByCategory(ProductCategory.valueOf(category));
        }

        return products.stream()
                //.sorted(Comparator.comparing(Product::getId)) // ID 오름차순 정렬
                .map(product -> {
                    MenuProductDto dto = new MenuProductDto();
                    dto.setId(product.getId());
                    dto.setProductName(product.getProductName());
                    dto.setPrice(product.getPrice());
                    dto.setCategory(product.getCategory().name());
                    dto.setProductImage(product.getProductImage());
                    return dto;
                })
                .toList();
    }

    public Product getProduct(int id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("없는 상품입니다. ID: " + id));
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Product deleteProduct(int id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("없는 상품입니다. ID: " + id));

        productRepository.delete(product);
        return product;
    }

    public Product updateProduct(UpdateProductRequestDto updateProductRequestDto) {
        Product product = productRepository.findById(updateProductRequestDto.getId())
                .orElseThrow(() -> new RuntimeException("없는 상품입니다. ID: " + updateProductRequestDto.getId()));

        product.setProductName(updateProductRequestDto.getProductName());
        product.setPrice(updateProductRequestDto.getPrice());
        product.setDescription(updateProductRequestDto.getDescription());
        product.setStock(updateProductRequestDto.getStock());
        product.setStatus(ProductStatus.valueOf(updateProductRequestDto.getStatus()));
        product.setCategory(ProductCategory.valueOf(updateProductRequestDto.getCategory()));
        product.setProductImage(updateProductRequestDto.getImageUrl());

        return productRepository.save(product);
    }

    public void increaseStock(int id, int quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ServiceException("404-1", "해당 상품이 존재하지 않습니다."));
        product.increaseStock(quantity);
    }

    public void decreaseStock(int id, int quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ServiceException("404-1", "해당 상품이 존재하지 않습니다."));
        product.decreaseStock(quantity);
    }
}

