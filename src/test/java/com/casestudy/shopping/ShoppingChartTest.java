package com.casestudy.shopping;

import com.casestudy.shopping.model.*;
import org.junit.jupiter.api.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShoppingChartTest {

    private ShoppingChart shoppingChart;
    private Category catFood;
    private Product apple;
    private Product almond;
    private Product orange;
    private Category catBabyFood;
    private Product babyMilk;
    private Category catBeverage;
    private Product water;

    @BeforeEach
    void setUp() {

        catFood = new Category("food");

        apple = new Product("Apple", 100.0, catFood);
        almond = new Product("Almond", 150.0, catFood);
        orange = new Product("Orange", 150.0, catFood);

        catBabyFood = new Category("Baby Food") ;
        catBabyFood.setParent(catFood);;

        babyMilk = new Product("Baby Milk", 200.0, catBabyFood);

        catBeverage = new Category("beverage");
        water = new Product("water",120.0, catBeverage);

        DeliveryCostCalculator deliveryCostCalculator = new DeliveryCostCalculatorImpl(Constants.COST_PER_DELIVERY, Constants.COST_PER_PRODUCT, Constants.FIXED_COST);
        shoppingChart = new ShoppingChartImpl(deliveryCostCalculator);

    }

    @Nested
    @DisplayName("Add or Remove item from the chart")
    class AddRemoveItemTest {
        @Test
        @DisplayName("assertions on add item method")
        void testAddItem() {

            shoppingChart.addItem(apple, 3);
            shoppingChart.addItem(water, 2);

            Assertions.assertAll(
                    ()-> Assertions.assertEquals(2, shoppingChart.getNumberOfProducts()) ,
                    () -> Assertions.assertEquals(540, shoppingChart.getTotalAmountAfterDiscounts()),
                    () -> Assertions.assertEquals(2, shoppingChart.getNumberOfDistinctCategories())
            );

            shoppingChart.addItem(apple, 1);
            Assertions.assertAll(
                    ()-> Assertions.assertEquals(2, shoppingChart.getNumberOfProducts()) ,
                    () -> Assertions.assertEquals(640, shoppingChart.getTotalAmountAfterDiscounts()),
                    () -> Assertions.assertEquals(2, shoppingChart.getNumberOfDistinctCategories())
            );
        }

        @Test
        @DisplayName("assertions on remove item method")
        void testRemoveItem(){
            shoppingChart.addItem(apple, 3);
            shoppingChart.addItem(water, 2);
            shoppingChart.addItem(apple, 1);
            shoppingChart.addItem(almond, 1);

            Assertions.assertAll(
                    ()-> Assertions.assertEquals(3, shoppingChart.getNumberOfProducts()) ,
                    () -> Assertions.assertEquals(790, shoppingChart.getTotalAmountAfterDiscounts()),
                    () -> Assertions.assertEquals(2, shoppingChart.getNumberOfDistinctCategories())
            );

            shoppingChart.removeItem(apple, 1);

            Assertions.assertAll(
                    ()-> Assertions.assertEquals(3, shoppingChart.getNumberOfProducts()) ,
                    () -> Assertions.assertEquals(690, shoppingChart.getTotalAmountAfterDiscounts()),
                    () -> Assertions.assertEquals(2, shoppingChart.getNumberOfDistinctCategories())
            );

            shoppingChart.removeItem(apple, 3);

            Assertions.assertAll(
                    ()-> Assertions.assertEquals(2, shoppingChart.getNumberOfProducts()) ,
                    () -> Assertions.assertEquals(390, shoppingChart.getTotalAmountAfterDiscounts()),
                    () -> Assertions.assertEquals(2, shoppingChart.getNumberOfDistinctCategories())
            );

        }

        @Test
        @DisplayName("got an exception when remove an item from chart that does not exists")
        void testRemoveItemException(){

            IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> shoppingChart.removeItem(apple, 1));
            Assertions.assertEquals("item does not exist!", ex.getMessage());

        }

        @Test
        @DisplayName("got an exception when try to remove more items than in the chart")
        void testCantRemoveMoreThanExistingItem(){
            shoppingChart.addItem(apple, 2);
            IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> shoppingChart.removeItem(apple, 3));
            Assertions.assertEquals("amount does not match!", ex.getMessage());
        }

        @Test
        @DisplayName("remove product from chart map if no item left")
        void testProductRemovedFromChart(){
            shoppingChart.addItem(apple, 2);
            shoppingChart.removeItem(apple, 2);
            Assertions.assertAll(
                    () -> Assertions.assertEquals(0, shoppingChart.getNumberOfProducts()),
                    () -> Assertions.assertEquals(0, shoppingChart.getTotalAmountAfterDiscounts())
            );
        }

    }

    @Nested
    @DisplayName("apply discounts and campaigns to the chart")
    class DiscountTest {

        private Campaign campaignBeverageAmount;
        private Campaign campaignBeverageRate;
        private Campaign campaignFood;
        private Campaign campaignBabyFood;

        @BeforeEach
        void setUp() {
            campaignBeverageAmount = new Campaign(catBeverage, 2, DiscountType.AMOUNT, 20);
            campaignBeverageRate = new Campaign(catBeverage, 2, DiscountType.RATE, 20);
            campaignFood = new Campaign(catFood, 2, DiscountType.RATE, 10);
            campaignBabyFood = new Campaign(catBabyFood, 2, DiscountType.RATE, 10);
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

    @Nested
    @DisplayName("calculate delivery costs")
    class DeliveryCalculatorTest {

        @Test
        @DisplayName("delivery cost calculated according to chart")
        public void testCalculateFor() {

            shoppingChart.addItem(water, 3);
            shoppingChart.addItem(orange, 3);

            double v = shoppingChart.getDeliveryCost();
            Assertions.assertEquals(54.99, v);

        }

    }


    @Nested
    @DisplayName("print chart contents")
    class PrinterTest {

        private Campaign campaignFood;

        @BeforeEach
        void setUp() {
            campaignFood = new Campaign(catFood, 2, DiscountType.RATE, 10);
        }

        @Test
        @DisplayName("print chart content and assert the output")
        void testPrint(){
            shoppingChart.addItem(apple, 3);
            shoppingChart.addItem(almond, 2);
            shoppingChart.addItem(babyMilk,1);
            shoppingChart.addItem(water,2);

            shoppingChart.applyDiscounts(campaignFood);

            Matcher matcher = Pattern.compile("^(Category:\\s([a-zA-Z]+\\s?)+\\s*(Product:.*\\s*Quantity.*\\s*Unit price.*\\s*)+)+Total price:.*\\s*Total amount.*\\s*Delivery Cost.*\\s*Total Cost After All.*$").matcher(shoppingChart.print());

            Assertions.assertTrue(matcher.find());

        }

    }

}
