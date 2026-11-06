package com.rishabh.store.order.entity;

import com.rishabh.store.order.model.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ORDERS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {
    @Id
    @Column(name = "ORDER_ID", length = 64, nullable = false)
    private String orderId;
    @Column(name = "CUSTOMER_ID", length = 64, nullable = false)
    private String customerId;
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 32, nullable = false)
    private OrderStatus status;
    @Column(name = "TOTAL_AMOUNT", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalAmount;
    @Column(name = "SHIPPING_ADDRESS", length = 500, nullable = false)
    private String shippingAddress;
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private Instant createdAt;
    @Builder.Default
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<OrderItemEntity> items = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    public void addItem(OrderItemEntity item) {
        items.add(item);
        item.setOrder(this);
    }

}
