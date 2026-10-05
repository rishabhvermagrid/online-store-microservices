package com.rishabh.store.catalog.service;

import com.rishabh.store.catalog.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogService {
    private final List<Product> products = List.of(
            new Product("id-101", "SKU-1", "Blue Shirt",
                    "Cotton shirt", 899.0, "Clothing", "Brand A"),

            new Product("id-102", "SKU-1", "Black Shirt",
                    "Casual shirt", 999.0, "Clothing", "Brand B")
    );

    public Product getProductByUniqId(String uniqId){
        return products.stream()
                .filter(product -> product.uniqId().equals(uniqId))
                .findFirst()
                .orElseThrow(()->new RuntimeException("Product not found"));
    }

    public List<Product> getProductsBySku(String sku){
        return products.stream()
                .filter( product -> product.sku().equalsIgnoreCase(sku))
                .toList();
    }



}
