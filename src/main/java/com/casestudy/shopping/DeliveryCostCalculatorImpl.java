package com.casestudy.shopping;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeliveryCostCalculatorImpl implements DeliveryCostCalculator {

    private final double costPerDelivery;
    private final double costPerProduct;
    private final double fixedCost;

    @Override
    public double calculateFor(ShoppingChartImpl chart) {

        var numberOfProducts = chart.getNumberOfProducts();
        var numberOfDistinctCategories = chart.getNumberOfDistinctCategories();

        return costPerDelivery * numberOfDistinctCategories + costPerProduct * numberOfProducts + fixedCost;
    }


}
