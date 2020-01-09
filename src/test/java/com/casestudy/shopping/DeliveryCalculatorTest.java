package com.casestudy.shopping;

import com.casestudy.shopping.impl.DeliveryCostCalculatorImpl;
import com.casestudy.shopping.impl.ShoppingChartImpl;
import com.casestudy.shopping.model.Category;
import com.casestudy.shopping.model.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DeliveryCalculatorTest {

    @Test
    @DisplayName("delivery cost calculated according to chart")
    public void testCalculateFor() {

        Category catFood = new Category("food");

        Product orange = new Product("Orange", 150.0, catFood);

        Category catBabyFood = new Category("Baby Food");
        catBabyFood.setParent(catFood);

        Category catBeverage = new Category("beverage");
        Product water = new Product("water", 120.0, catBeverage);

        DeliveryCostCalculator deliveryCostCalculator = new DeliveryCostCalculatorImpl(Constants.COST_PER_DELIVERY, Constants.COST_PER_PRODUCT, Constants.FIXED_COST);

        ShoppingChart shoppingChart = new ShoppingChartImpl(deliveryCostCalculator);
        shoppingChart.addItem(water, 3);
        shoppingChart.addItem(orange, 3);

        double v = shoppingChart.getDeliveryCost();
        Assertions.assertEquals(54.99, v);

    }
}
