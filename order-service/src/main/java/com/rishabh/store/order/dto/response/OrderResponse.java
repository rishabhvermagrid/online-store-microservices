package com.rishabh.store.order.dto.response;

import com.rishabh.store.order.entity.OrderEntity;
import com.rishabh.store.order.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        String orderId,
        String customerId,
        OrderStatus status,
        BigDecimal totalAmount,
        String shippingAddress,
        List<OrderItemResponse> items,
        Instant createdAt
) {
    public static OrderResponse fromEntity(OrderEntity entity) {
        List<OrderItemResponse> itemResponses = entity.getItems() == null
                ? List.of()
                : entity.getItems().stream()
                        .map(OrderItemResponse::fromEntity)
                        .toList();

        return new OrderResponse(
                entity.getOrderId(),
                entity.getCustomerId(),
                entity.getStatus(),
                entity.getTotalAmount(),
                entity.getShippingAddress(),
                itemResponses,
                entity.getCreatedAt()
        );
    }
}
