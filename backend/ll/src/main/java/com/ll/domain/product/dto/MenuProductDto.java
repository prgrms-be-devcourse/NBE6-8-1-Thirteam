package com.ll.domain.product.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuProductDto {
    private int id;
    private String productName;
    private int price;
    private String category;
    private String productImage;
}
