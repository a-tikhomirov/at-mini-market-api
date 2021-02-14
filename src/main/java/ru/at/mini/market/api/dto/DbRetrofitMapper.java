package ru.at.mini.market.api.dto;

import ru.at.mini.market.api.base.enums.Category;
import ru.at.mini.market.api.db.model.Products;

import java.util.ArrayList;
import java.util.List;

public class DbRetrofitMapper {
    public static List<Product> getProductsListFromDbProducts(List<Products> dbProductsList) {
        List<Product> productList = new ArrayList<>();
        dbProductsList.forEach(p -> productList.add(getRetrofitProductFromDbProduct(p)));
        return productList;
    }

    public static Product getRetrofitProductFromDbProduct(Products product) {
        return new Product()
                .withId(Math.toIntExact(product.getId()))
                .withTitle(product.getTitle())
                .withPrice(product.getPrice())
                .withCategoryTitle(Category.getCategoryById(Math.toIntExact(product.getCategory_id())).title);
    }
}
