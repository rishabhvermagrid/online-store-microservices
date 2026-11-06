package com.rishabh.store.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ORDER_ITEMS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ORDER_ID", nullable = false)
  private OrderEntity order;

  @Column(name = "PRODUCT_ID", length = 64, nullable = false)
  private String productId;

  @Column(name = "SKU", length = 64, nullable = false)
  private String sku;

  @Column(name = "QUANTITY", nullable = false)
  private Integer quantity;

  @Column(name = "PRICE", precision = 12, scale = 2, nullable = false)
  private BigDecimal price;
}
