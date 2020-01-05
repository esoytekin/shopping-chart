package com.casestudy.shopping.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Campaign {
    private final Category category;
    private final int limit;
    private final DiscountType discountType;
    private final int discount;
}
