package com.casestudy.shopping.service.impl;

import com.casestudy.shopping.model.Category;
import com.casestudy.shopping.model.Product;
import com.casestudy.shopping.service.ShoppingChartReaderService;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class ShoppingChartReaderServiceImpl implements ShoppingChartReaderService {

    private final Map<Category, Map<Product, Integer>> chart;

    @Override
    public int getNumberOfProducts() {
        return chart.values().stream().mapToInt(m -> m.values().size()).sum();
    }

    @Override
    public int getNumberOfDistinctCategories() {
        return chart.size();
    }

}
