package com.casestudy.shopping.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Product {
    private final String title;
    private final double price;
    private final Category category;
}
