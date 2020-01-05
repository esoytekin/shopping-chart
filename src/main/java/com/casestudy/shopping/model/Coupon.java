package com.casestudy.shopping.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Coupon {
    private final int amountLimit;
    private final int discount;
    private final DiscountType discountType;

}
