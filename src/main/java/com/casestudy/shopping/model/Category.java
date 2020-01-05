package com.casestudy.shopping.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Category {
    private final String title;
    private Category parent;
}
