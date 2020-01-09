package com.casestudy.shopping.service;

public interface ShoppingChartReaderService {

    int getNumberOfProducts();

    int getNumberOfDistinctCategories();

    double getTotalPrice();

    double getTotalAmountAfterDiscounts();
}
