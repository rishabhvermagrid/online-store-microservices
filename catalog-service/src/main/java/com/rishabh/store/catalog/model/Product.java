package com.rishabh.store.catalog.model;

import com.rishabh.store.catalog.entity.ProductEntity;

public record Product(
        String uniqId,
        String sku,
        String name,
        String description,
        Double salePrice,
        String category,
        String brand
) {
    public static Product fromEntity(ProductEntity entity) {
                return new Product(
                        entity.getUniqId(),
                        entity.getSku(),
                        entity.getName(),
                        entity.getDescription(),
                        entity.getSalePrice(),
                        entity.getCategory(),
                        entity.getBrand()
                );
         }
}
