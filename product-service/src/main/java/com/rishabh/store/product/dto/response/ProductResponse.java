package com.rishabh.store.product.dto.response;

public record ProductResponse(
        String uniqId,
        String sku,
        String name,
        String description,
        Double salePrice,
        String category,
        String brand
) {
}
