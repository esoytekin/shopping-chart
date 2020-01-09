package com.casestudy.shopping.service.impl;

import com.casestudy.shopping.model.Category;
import com.casestudy.shopping.model.Product;
import com.casestudy.shopping.service.ShoppingChartUpdateService;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ShoppingChartUpdateServiceImpl implements ShoppingChartUpdateService {

    private final Map<Category, Map<Product, Integer>> chart;
    private double totalPrice;

    @Override
    public synchronized void addItem(Product p, int amount) {

        Map<Product, Integer> productMap = chart.getOrDefault(p.getCategory(), new HashMap<>());
        Integer productAmount = productMap.getOrDefault(p, 0);
        productMap.put(p, productAmount + amount);
        chart.putIfAbsent(p.getCategory(), productMap);
        totalPrice += amount * p.getPrice();


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

    }

    @Override
    public double getTotalPrice() {
        return totalPrice;
    }
}
