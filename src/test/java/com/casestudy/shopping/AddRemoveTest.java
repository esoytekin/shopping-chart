package com.casestudy.shopping;

import com.casestudy.shopping.impl.DeliveryCostCalculatorImpl;
import com.casestudy.shopping.impl.ShoppingChartImpl;
import com.casestudy.shopping.model.Category;
import com.casestudy.shopping.model.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AddRemoveTest {

    private ShoppingChart shoppingChart;
    private Product apple;
    private Product almond;
    private Product water;

    @BeforeEach
    void setUp() {

        Category catFood = new Category("food");

        apple = new Product("Apple", 100.0, catFood);
        almond = new Product("Almond", 150.0, catFood);

        Category catBabyFood = new Category("Baby Food");
        catBabyFood.setParent(catFood);

        Category catBeverage = new Category("beverage");
        water = new Product("water",120.0, catBeverage);

        DeliveryCostCalculator deliveryCostCalculator = new DeliveryCostCalculatorImpl(Constants.COST_PER_DELIVERY, Constants.COST_PER_PRODUCT, Constants.FIXED_COST);
        shoppingChart = new ShoppingChartImpl(deliveryCostCalculator);

    }


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
