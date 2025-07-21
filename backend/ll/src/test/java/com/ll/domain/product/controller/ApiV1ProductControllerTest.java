package com.ll.domain.product.controller;

import com.ll.domain.product.dto.MenuProductDto;
import com.ll.domain.product.entity.Product;
import com.ll.domain.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class ApiV1ProductControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProductService productService;

    @Value("${product.upload-dir}")
    private String dirName;

    @Test
    @DisplayName("상품 등록 방식")
    void 상품_등록() throws Exception {

        String jsonContent = """
        {
          "productName": "커피 01",
          "price": 4500,
          "description": "커피 01 설명",
          "stock": 500,
          "status": "SALE",
          "category": "COFFEE",
          "imageUrl": "images/Americano.png"
        }
        """;

        ResultActions resultActions = mvc.perform(
                post("/api/v1/products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
        ).andDo(print());

        resultActions
                .andExpect(status().isOk());
        // 필요에 따라 응답값 추가 검증

        String uploadDir = System.getProperty("user.dir") + "/" + dirName;
        Path uploadPath = Paths.get(uploadDir); // 테스트용 경로와 일치시킬 것
        if (Files.exists(uploadPath)) {
            // 폴더와 하위 파일 전체 삭제
            Files.walk(uploadPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @DisplayName("상품 리스트 조회 - 카테고리별")
    @ParameterizedTest
    @ValueSource(strings = {"ALL", "COFFEE", "TEA", "JUICE", "DESSERT"})
    void 상품_리스트_카테고리별_조회(String category) throws Exception {

        ResultActions resultActions = mvc.perform(
                get("/api/v1/products/category/{category}", category)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());

        List<MenuProductDto> menuProducts = productService.getMenuProductsByCategory(category);

        resultActions
                .andExpect(handler().handlerType(ApiV1ProductController.class))
                .andExpect(handler().methodName("getMenuProductsByCategory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(menuProducts.size()));

        for (int i = 0; i < menuProducts.size(); i++) {
            MenuProductDto dto = menuProducts.get(i);
            resultActions
                    .andExpect(jsonPath("$.data[%d].id".formatted(i)).value(dto.getId()))
                    .andExpect(jsonPath("$.data[%d].productName".formatted(i)).value(dto.getProductName()))
                    .andExpect(jsonPath("$.data[%d].price".formatted(i)).value(dto.getPrice()))
                    .andExpect(jsonPath("$.data[%d].category".formatted(i)).value(dto.getCategory()))
                    .andExpect(jsonPath("$.data[%d].productImage".formatted(i)).value(dto.getProductImage()));
        }
    }

    @DisplayName("단건_상품_상세_조회")
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16})
    void 단건_상품_상세_조회(int id) throws Exception {

        ResultActions resultActions = mvc.perform(
                get("/api/v1/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());

        Product product = productService.getProduct(id);

        resultActions
                .andExpect(handler().handlerType(ApiV1ProductController.class))
                .andExpect(handler().methodName("getProduct"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(product.getId()))
                .andExpect(jsonPath("$.data.productName").value(product.getProductName()))
                .andExpect(jsonPath("$.data.price").value(product.getPrice()))
                .andExpect(jsonPath("$.data.category").value(product.getCategory().name()))
                .andExpect(jsonPath("$.data.productImage").value(product.getProductImage()))
                .andExpect(jsonPath("$.data.description").value(product.getDescription()))
                .andExpect(jsonPath("$.data.stock").value(product.getStock()))
                .andExpect(jsonPath("$.data.status").value(product.getStatus().name()));
    }

    @Test
    @DisplayName("다건_상품_상세_조회")
    void 다건_상품_상세_조회() throws Exception {

        ResultActions resultActions = mvc.perform(
                get("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());

        List<Product> products = productService.getProducts();

        resultActions
                .andExpect(handler().handlerType(ApiV1ProductController.class))
                .andExpect(handler().methodName("getProducts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(products.size()));

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            resultActions
                    .andExpect(jsonPath("$.data[%d].id".formatted(i)).value(product.getId()))
                    .andExpect(jsonPath("$.data[%d].productName".formatted(i)).value(product.getProductName()))
                    .andExpect(jsonPath("$.data[%d].price".formatted(i)).value(product.getPrice()))
                    .andExpect(jsonPath("$.data[%d].category".formatted(i)).value(product.getCategory().name()))
                    .andExpect(jsonPath("$.data[%d].productImage".formatted(i)).value(product.getProductImage()))
                    .andExpect(jsonPath("$.data[%d].description".formatted(i)).value(product.getDescription()))
                    .andExpect(jsonPath("$.data[%d].stock".formatted(i)).value(product.getStock()))
                    .andExpect(jsonPath("$.data[%d].status".formatted(i)).value(product.getStatus().name()));
        }
    }

    @DisplayName("상품_수정")
    @Test
    void 상품_수정() throws Exception {

        ResultActions resultActions = mvc.perform(
                put("/api/v1/products/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": 1,
                                    "productName": "수정된 커피 01",
                                    "price": 10000,
                                    "description": "수정된 커피 01 설명",
                                    "stock": 10000,
                                    "status": "SOLD_OUT",
                                    "category": "TEA"
                                }
                                """)

        ).andDo(print());

        Product product = productService.getProduct(1);

        resultActions
                .andExpect(handler().handlerType(ApiV1ProductController.class))
                .andExpect(handler().methodName("updateProduct"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(product.getId()))
                .andExpect(jsonPath("$.data.productName").value(product.getProductName()))
                .andExpect(jsonPath("$.data.price").value(product.getPrice()))
                .andExpect(jsonPath("$.data.category").value(product.getCategory().name()))
                .andExpect(jsonPath("$.data.productImage").value(product.getProductImage()))
                .andExpect(jsonPath("$.data.description").value(product.getDescription()))
                .andExpect(jsonPath("$.data.stock").value(product.getStock()))
                .andExpect(jsonPath("$.data.status").value(product.getStatus().name()));

    }

    @DisplayName("단건_상품_삭제")
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16})
    void 단건_상품_삭제(int id) throws Exception {

        ResultActions resultActions = mvc.perform(
                delete("/api/v1/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ProductController.class))
                .andExpect(handler().methodName("deleteProduct"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.msg").value("%d번 상품을 삭제했습니다.".formatted(id)));
    }

}
