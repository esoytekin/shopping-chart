package com.casestudy.shopping.service.impl;

import com.casestudy.shopping.model.Category;
import com.casestudy.shopping.model.Product;
import com.casestudy.shopping.service.ShoppingChartDiscountService;
import com.casestudy.shopping.service.ShoppingChartReaderService;
import com.casestudy.shopping.service.ShoppingChartUpdateService;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class ShoppingChartReaderServiceImpl implements ShoppingChartReaderService {

    private final Map<Category, Map<Product, Integer>> chart;

    private final ShoppingChartUpdateService updater;
    private final ShoppingChartDiscountService discount;

    @Override
    public int getNumberOfProducts() {
        return chart.values().stream().mapToInt(m -> m.values().size()).sum();
    }

    @Override
    public int getNumberOfDistinctCategories() {
        return chart.size();
    }

    @Override
    public double getTotalAmountAfterDiscounts() {
        return updater.getTotalPrice() - discount.getCampaignDiscount() - discount.getCouponDiscounts();
    }

    @Override
    public double getTotalPrice() {
        return updater.getTotalPrice();
    }
}
