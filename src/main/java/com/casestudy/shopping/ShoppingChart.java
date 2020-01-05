package com.casestudy.shopping;

import com.casestudy.shopping.model.Campaign;
import com.casestudy.shopping.model.Coupon;
import com.casestudy.shopping.model.Product;

public interface ShoppingChart {

    void addItem(Product p, int amount);

    void removeItem(Product p, int amount);

    void applyDiscounts(Campaign... campaigns);

    void applyCoupon(Coupon coupon);

    double getTotalAmountAfterDiscounts();

    double getCouponDiscounts();

    double getCampaignDiscount();

    int getNumberOfProducts();

    int getNumberOfDistinctCategories();

    double getDeliveryCost();

    String print();
}
