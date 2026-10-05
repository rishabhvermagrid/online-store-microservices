package com.rishabh.store.catalog.model;

public record Product(
        String uniqId,
        String sku,
        String name,
        String description,
        Double salePrice,
        String category,
        String brand
) {
}
