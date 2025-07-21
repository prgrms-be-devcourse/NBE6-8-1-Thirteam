package com.ll.domain.product.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProductRequestDto {
    private int id;
    private String productName;
    private int price;
    private String description;
    private int stock;
    private String status;
    private String category;
    private String imageUrl;
}
