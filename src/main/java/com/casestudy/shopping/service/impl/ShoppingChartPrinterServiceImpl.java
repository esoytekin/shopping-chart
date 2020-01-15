package com.casestudy.shopping.service.impl;

import com.casestudy.shopping.ShoppingChart;
import com.casestudy.shopping.model.Category;
import com.casestudy.shopping.model.Product;
import com.casestudy.shopping.service.ShoppingChartPrinterService;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class ShoppingChartPrinterServiceImpl implements ShoppingChartPrinterService {

    private final ShoppingChart shoppingChart;

    private final Map<Category, Map<Product, Integer>> chart;

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

        builder.append(String.format("Total price: %.2f%n", shoppingChart.getTotalPrice()));

        double totalAmountAfterDiscounts = shoppingChart.getTotalAmountAfterDiscounts();
        builder.append(String.format("Total amount after discount: %.2f%n", totalAmountAfterDiscounts));

        double deliveryCost = shoppingChart.getDeliveryCost();
        builder.append(String.format("Delivery Cost %.2f%n", deliveryCost));
        builder.append(String.format("Total Cost After All: %.2f%n", totalAmountAfterDiscounts + deliveryCost));
        return builder.toString();

    }
}
