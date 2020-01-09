package com.casestudy.shopping.service;

import com.casestudy.shopping.model.Product;

public interface ShoppingChartUpdateService {

    void addItem(Product p, int amount);

    void removeItem(Product p, int amount);

    double getTotalPrice();
}
