package com.casestudy.shopping;

import com.casestudy.shopping.impl.DeliveryCostCalculatorImpl;
import com.casestudy.shopping.impl.ShoppingChartImpl;
import com.casestudy.shopping.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DiscountTest {

    private ShoppingChart shoppingChart;
    private Campaign campaignBeverageAmount;
    private Campaign campaignBeverageRate;
    private Campaign campaignFood;
    private Campaign campaignBabyFood;
    private Product apple;
    private Product babyMilk;
    private Product water;
    private Product orange;

    @BeforeEach
    void setUp() {
        Category catBeverage = new Category("beverage");
        Category catFood = new Category("food");
        Category catBabyFood = new Category("Baby Food");
        catBabyFood.setParent(catFood);

        apple = new Product("Apple", 100.0, catFood);
        water = new Product("water",120.0, catBeverage);
        babyMilk = new Product("Baby Milk", 200.0, catBabyFood);
        orange = new Product("Orange", 150.0, catFood);

        campaignBeverageAmount = new Campaign(catBeverage, 2, DiscountType.AMOUNT, 20);
        campaignBeverageRate = new Campaign(catBeverage, 2, DiscountType.RATE, 20);
        campaignFood = new Campaign(catFood, 2, DiscountType.RATE, 10);
        campaignBabyFood = new Campaign(catBabyFood, 2, DiscountType.RATE, 10);

        DeliveryCostCalculator deliveryCostCalculator = new DeliveryCostCalculatorImpl(Constants.COST_PER_DELIVERY, Constants.COST_PER_PRODUCT, Constants.FIXED_COST);
        shoppingChart = new ShoppingChartImpl(deliveryCostCalculator);
    }

    @Test
    @DisplayName("no discount apply if category not found")
    void testApplyDiscountsNotApplied() {

        shoppingChart.addItem(apple, 2);

        shoppingChart.applyDiscounts(campaignBeverageAmount);
        Assertions.assertEquals(0,shoppingChart.getCampaignDiscount());
    }

    @Test
    @DisplayName("discount applied by amount if a category found")
    void testApplyDiscountsByAmount(){
        shoppingChart.addItem(water, 3);
        shoppingChart.applyDiscounts(campaignBeverageAmount);
        Assertions.assertEquals(20, shoppingChart.getCampaignDiscount());

    }

    @Test
    @DisplayName(" discount applied if there is only sub category item")
    void testApplyDiscountForSubCategoryOnly(){
        shoppingChart.addItem(babyMilk,2);
        shoppingChart.applyDiscounts(campaignFood);

        Assertions.assertEquals(40, shoppingChart.getCampaignDiscount());
    }

    @Test
    @DisplayName("same discount applies for category and subcategories")
    void testApplyDiscountForSubcategoryAndCategory(){
        shoppingChart.addItem(apple, 1);
        shoppingChart.addItem(babyMilk, 1);
        shoppingChart.applyDiscounts(campaignFood);

        Assertions.assertEquals(30, shoppingChart.getCampaignDiscount());

    }

    @Test
    @DisplayName("sub campaign only applies if there is sub category item")
    void testSubCampaignOnlyAppliedSubItem(){
        shoppingChart.addItem(apple, 2);
        shoppingChart.addItem(babyMilk, 2);

        shoppingChart.applyDiscounts(campaignBabyFood);

        Assertions.assertEquals(40, shoppingChart.getCampaignDiscount());
    }


    @Test
    @DisplayName("discount applied by rate if a category found")
    void testApplyDiscountsAppliedByRate(){
        shoppingChart.addItem(water, 3);
        shoppingChart.applyDiscounts(campaignBeverageRate);

        Assertions.assertEquals(72, shoppingChart.getCampaignDiscount());
    }

    @Test
    @DisplayName("max discount applied if found multiple")
    void testApplyDiscountMaxCampaign(){
        shoppingChart.addItem(water, 3);
        shoppingChart.addItem(orange, 3);
        shoppingChart.applyDiscounts(campaignBeverageRate, campaignBeverageAmount, campaignFood);
        Assertions.assertEquals(72, shoppingChart.getCampaignDiscount());
    }

    @Test
    @DisplayName("coupon not applied if no campaign discount applied")
    void testCouponNotApplied(){
        Coupon c = new Coupon(100, 20, DiscountType.AMOUNT);
        shoppingChart.addItem(apple, 2);
        shoppingChart.applyCoupon(c);
        Assertions.assertEquals(0, shoppingChart.getCouponDiscounts());
    }

    @Test
    @DisplayName("coupon applied if campaign applied along with coupon")
    void testCouponApplied() {
        Coupon c = new Coupon(100, 20, DiscountType.AMOUNT);
        shoppingChart.addItem(water, 3);
        shoppingChart.applyDiscounts(campaignBeverageAmount);
        shoppingChart.applyCoupon(c);
        Assertions.assertEquals(20.0, shoppingChart.getCouponDiscounts());

    }

    @Test
    @DisplayName("multiple coupons applied")
    void testCouponAppliedMultiple() {
        shoppingChart.addItem(water, 3);
        shoppingChart.applyDiscounts(campaignBeverageAmount);
        Coupon c = new Coupon(100, 20, DiscountType.AMOUNT);
        shoppingChart.applyCoupon(c);

        c = new Coupon(100, 20, DiscountType.RATE);
        shoppingChart.applyCoupon(c);
        Assertions.assertAll(
                () -> Assertions.assertEquals(20.0, shoppingChart.getCampaignDiscount()),
                () -> Assertions.assertEquals(84.0, shoppingChart.getCouponDiscounts())
        );
    }

    @Test
    @DisplayName("coupon not applied if limit is not enough")
    void testCouponNotAppliedLimit(){
        shoppingChart.addItem(apple, 3);
        shoppingChart.applyDiscounts(campaignFood);
        Coupon c = new Coupon(350, 20, DiscountType.AMOUNT);
        shoppingChart.applyCoupon(c);

        Assertions.assertEquals(0, shoppingChart.getCouponDiscounts());
    }
}
