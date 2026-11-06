package com.rishabh.store.order.dto.response;

import com.rishabh.store.order.entity.OrderItemEntity;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        String productId,
        String sku,
        Integer quantity,
        BigDecimal price
){
    public static OrderItemResponse fromEntity(OrderItemEntity entity){
        return new OrderItemResponse(
                entity.getId(),
                entity.getProductId(),
                entity.getSku(),
                entity.getQuantity(),
                entity.getPrice()
        );
    }
}
