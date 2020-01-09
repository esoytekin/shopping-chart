package com.casestudy.shopping;

import com.casestudy.shopping.impl.DeliveryCostCalculatorImpl;
import com.casestudy.shopping.impl.ShoppingChartImpl;
import com.casestudy.shopping.model.Campaign;
import com.casestudy.shopping.model.Category;
import com.casestudy.shopping.model.DiscountType;
import com.casestudy.shopping.model.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrinterTest {

    @Test
    @DisplayName("print chart content and assert the output")
    void testPrint(){

        Category catFood = new Category("food");

        Product apple = new Product("Apple", 100.0, catFood);
        Product almond = new Product("Almond", 150.0, catFood);

        Category catBabyFood = new Category("Baby Food");
        catBabyFood.setParent(catFood);

        Product babyMilk = new Product("Baby Milk", 200.0, catBabyFood);

        Category catBeverage = new Category("beverage");
        Product water = new Product("water", 120.0, catBeverage);

        DeliveryCostCalculator deliveryCostCalculator = new DeliveryCostCalculatorImpl(Constants.COST_PER_DELIVERY, Constants.COST_PER_PRODUCT, Constants.FIXED_COST);
        ShoppingChart shoppingChart = new ShoppingChartImpl(deliveryCostCalculator);

        Campaign campaignFood = new Campaign(catFood, 2, DiscountType.RATE, 10);


        shoppingChart.addItem(apple, 3);
        shoppingChart.addItem(almond, 2);
        shoppingChart.addItem(babyMilk,1);
        shoppingChart.addItem(water,2);

        shoppingChart.applyDiscounts(campaignFood);

        Matcher matcher = Pattern.compile("^(Category:\\s([a-zA-Z]+\\s?)+\\s*(Product:.*\\s*Quantity.*\\s*Unit price.*\\s*)+)+Total price:.*\\s*Total amount.*\\s*Delivery Cost.*\\s*Total Cost After All.*$").matcher(shoppingChart.print());

        Assertions.assertTrue(matcher.find());

    }
}
