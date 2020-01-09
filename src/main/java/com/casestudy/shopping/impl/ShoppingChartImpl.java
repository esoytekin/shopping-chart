package com.casestudy.shopping.impl;

import com.casestudy.shopping.DeliveryCostCalculator;
import com.casestudy.shopping.ShoppingChart;
import com.casestudy.shopping.model.Campaign;
import com.casestudy.shopping.model.Category;
import com.casestudy.shopping.model.Coupon;
import com.casestudy.shopping.model.Product;
import com.casestudy.shopping.service.*;
import com.casestudy.shopping.service.impl.*;

import java.util.HashMap;
import java.util.Map;

public class ShoppingChartImpl implements ShoppingChart {

    private final ShoppingChartUpdateService updater;
    private final ShoppingChartReaderService reader;
    private final ShoppingChartDiscountService discount;
    private final ShoppingChartDeliveryService delivery;
    private final ShoppingChartPrinterService printer;



    public ShoppingChartImpl(DeliveryCostCalculator deliveryCostCalculator) {

        Map<Category, Map<Product, Integer>> chart = new HashMap<>();

        this.updater =  new ShoppingChartUpdateServiceImpl(chart);
        this.discount = new ShoppingChartDiscountServiceImpl(chart, updater);
        this.reader  = new ShoppingChartReaderServiceImpl(chart, updater, discount);
        this.delivery = new ShoppingChartDeliveryServiceImpl(deliveryCostCalculator, this);
        this.printer = new ShoppingChartPrinterServiceImpl(chart, delivery, reader);

    }

    @Override
    public void addItem(Product p, int amount) {
        updater.addItem(p,amount);
        discount.initializeDiscounts();
    }

    @Override
    public void removeItem(Product p, int amount) {
        updater.removeItem(p,amount);
        discount.initializeDiscounts();
    }

    @Override
    public void initializeDiscounts() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void applyDiscounts(Campaign... campaigns) {
        discount.applyDiscounts(campaigns);
    }

    @Override
    public void applyCoupon(Coupon coupon) {
        discount.applyCoupon(coupon);
    }

    @Override
    public double getCouponDiscounts() {
        return discount.getCouponDiscounts();
    }

    @Override
    public double getTotalAmountAfterDiscounts() {
        return reader.getTotalAmountAfterDiscounts();
    }

    @Override
    public double getDeliveryCost() {
        return delivery.getDeliveryCost();
    }

    @Override
    public String print() {
        return printer.print();
    }

    @Override
    public int getNumberOfProducts() {
        return reader.getNumberOfProducts();
    }

    @Override
    public int getNumberOfDistinctCategories() {
        return reader.getNumberOfDistinctCategories();
    }

    @Override
    public double getTotalPrice() {
        return reader.getTotalPrice();
    }

    @Override
    public double getCampaignDiscount() {
        return discount.getCampaignDiscount();
    }
}
