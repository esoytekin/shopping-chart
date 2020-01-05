package com.casestudy.shopping;

import com.casestudy.shopping.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ShoppingChartImpl implements ShoppingChart {

    private Map<Category, Map<Product, Integer>> chart = new HashMap<>();
    private double campaignDiscount;
    private List<Double> couponDiscounts = new ArrayList<>();
    private double totalPrice;

    private final DeliveryCostCalculator deliveryCostCalculator;

    public ShoppingChartImpl(DeliveryCostCalculator deliveryCostCalculator) {
        this.deliveryCostCalculator = deliveryCostCalculator;
    }

    @Override
    public synchronized void addItem(Product p, int amount) {

        Map<Product, Integer> productMap = chart.getOrDefault(p.getCategory(), new HashMap<>());
        Integer productAmount = productMap.getOrDefault(p, 0);
        productMap.put(p, productAmount + amount);
        chart.putIfAbsent(p.getCategory(), productMap);
        totalPrice += amount * p.getPrice();

        couponDiscounts = new ArrayList<>();
        campaignDiscount = 0;

    }

    @Override
    public synchronized void removeItem(Product p, int amount) {

        Map<Product, Integer> productMap = chart.getOrDefault(p.getCategory(), new HashMap<>());

        if (productMap.get(p) == null) {
            throw new IllegalArgumentException("item does not exist!");
        }

        Integer oldAmount = productMap.get(p);

        if (amount > oldAmount) {
            throw new IllegalArgumentException("amount does not match!");
        }
        int newAmount = oldAmount - amount;

        if (newAmount == 0) {
            productMap.remove(p);
        } else {
            productMap.put(p, newAmount);
        }

        if (productMap.size() == 0) {
            chart.remove(p.getCategory());
        }

        totalPrice -= amount * p.getPrice();

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

        double priceAfterDiscounts = totalPrice - getCampaignDiscount() - getCouponDiscounts();

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
    public double getTotalAmountAfterDiscounts() {
        return totalPrice - campaignDiscount - getCouponDiscounts();
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

    @Override
    public int getNumberOfProducts() {
        return chart.values().stream().mapToInt(m -> m.values().size()).sum();
    }

    @Override
    public int getNumberOfDistinctCategories() {
        return chart.size();
    }

    @Override
    public double getDeliveryCost() {
        return deliveryCostCalculator.calculateFor(this);
    }

    @Override
    public String print(){

        StringBuilder builder = new StringBuilder();

        chart.forEach((c, m) -> {
            builder.append(String.format("Category: %s%n", c.getTitle()));
            builder.append("\n");
            AtomicInteger index = new AtomicInteger();
            m.forEach((p,a) -> {

                index.getAndIncrement();

                builder.append(String.format("\tProduct: %s%n", p.getTitle()));
                builder.append(String.format("\tQuantity: %d%n", a));
                builder.append(String.format("\tUnit price: %.2f%n", p.getPrice()));

                if (index.get() < m.size()) {
                    builder.append("\n");
                }
            });
            builder.append("\n");
        });

        builder.append(String.format("Total price: %.2f%n", totalPrice));

        double totalAmountAfterDiscounts = getTotalAmountAfterDiscounts();
        builder.append(String.format("Total amount after discount: %.2f%n", totalAmountAfterDiscounts));

        double deliveryCost = getDeliveryCost();
        builder.append(String.format("Delivery Cost %.2f%n", deliveryCost));
        builder.append(String.format("Total Cost After All: %.2f%n", totalAmountAfterDiscounts + deliveryCost));
        return builder.toString();

    }

}
