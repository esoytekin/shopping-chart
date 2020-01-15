package com.casestudy.shopping.service.impl;

import com.casestudy.shopping.DeliveryCostCalculator;
import com.casestudy.shopping.ShoppingChart;
import com.casestudy.shopping.service.ShoppingChartDeliveryService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ShoppingChartDeliveryServiceImpl implements ShoppingChartDeliveryService {

    private final ShoppingChart shoppingChart;

    private final DeliveryCostCalculator deliveryCostCalculator;

    @Override
    public double getDeliveryCost() {
        return deliveryCostCalculator.calculateFor(shoppingChart);
    }

}
