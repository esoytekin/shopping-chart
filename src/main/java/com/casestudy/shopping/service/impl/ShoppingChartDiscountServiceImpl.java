package com.casestudy.shopping.service.impl;

import com.casestudy.shopping.model.*;
import com.casestudy.shopping.service.ShoppingChartDiscountService;
import com.casestudy.shopping.service.ShoppingChartUpdateService;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ShoppingChartDiscountServiceImpl implements ShoppingChartDiscountService {

    private final Map<Category, Map<Product, Integer>> chart;

    private final ShoppingChartUpdateService updater;

    private List<Double> couponDiscounts = new ArrayList<>();
    private double campaignDiscount;

    @Override
    public void initializeDiscounts() {
        couponDiscounts = new ArrayList<>();
        campaignDiscount = 0;
    }

    @Override
    public void applyDiscounts(Campaign... campaigns) {

        double newCampaignDiscount = 0.0;

        for (Campaign campaign : campaigns) {

            Category category = campaign.getCategory();
            int limit = campaign.getLimit();
            DiscountType discountType = campaign.getDiscountType();
            int discount = campaign.getDiscount();

            Map<Product, Integer> discountMap = new HashMap<>(chart.getOrDefault(category, new HashMap<>()));
            List<Category> bySubCategory = getBySubCategory(category);
            bySubCategory.forEach(sc -> discountMap.putAll(chart.get(sc)));

            if (discountMap.isEmpty()) {
                continue;
            }

            // get items in discount category

            Integer sum = discountMap.values().stream().reduce(0, Integer::sum);

            if (sum >= limit) {

                // if discount type is amount discount is campaign.discount
                if (discountType == DiscountType.AMOUNT && discount > newCampaignDiscount) {
                    newCampaignDiscount = discount;
                } else {
                    // if discount type is rate
                    // discount is sum of campaign item prizes * discount /100

                    double calculatedDiscount = discountMap.entrySet().stream().mapToDouble(pe -> pe.getValue() * pe.getKey().getPrice()).sum() * discount / 100;

                    if (calculatedDiscount > newCampaignDiscount) {
                        newCampaignDiscount = calculatedDiscount;
                    }

                }

            }

        }

        setCampaignDiscount(newCampaignDiscount);

    }

    private List<Category> getBySubCategory(Category category){
        List<Category> subCategoryList = new ArrayList<>();
        chart.forEach((c,p)-> {
            if (c != category) {
                while (c.getParent() != null) {
                    if (c.getParent() == category) {
                        subCategoryList.add(c);
                        c = c.getParent();
                    }
                }
            }
        });

        return subCategoryList;

    }

    @Override
    public void applyCoupon(Coupon coupon) {

        if (getCampaignDiscount() <= 0) {
            return;
        }

        double priceAfterDiscounts = updater.getTotalPrice() - getCampaignDiscount() - getCouponDiscounts();

        if (priceAfterDiscounts < coupon.getAmountLimit()) {
            return;
        }

        if (coupon.getDiscountType() == DiscountType.AMOUNT) {
            double couponDiscount = coupon.getDiscount();
            couponDiscounts.add(couponDiscount);
        } else {
            double couponDiscount = priceAfterDiscounts * coupon.getDiscount() / 100;
            couponDiscounts.add(couponDiscount);
        }

    }

    @Override
    public double getCouponDiscounts() {
        return couponDiscounts.stream().mapToDouble(Double::doubleValue).sum();

    }

    private void setCampaignDiscount(double campaignDiscount) {
        this.campaignDiscount = campaignDiscount;
    }


    @Override
    public double getCampaignDiscount() {
        return campaignDiscount;

    }
}
