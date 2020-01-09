package com.casestudy.shopping.service;

import com.casestudy.shopping.model.Campaign;
import com.casestudy.shopping.model.Coupon;

public interface ShoppingChartDiscountService {

    void initializeDiscounts();

    void applyDiscounts(Campaign... campaigns);

    void applyCoupon(Coupon coupon);

    double getCouponDiscounts();

    double getCampaignDiscount();
}
