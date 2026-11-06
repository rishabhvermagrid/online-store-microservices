package com.rishabh.store.catalog.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name="PRODUCTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity {
    @Id
    @Column(name = "UNIQ_ID", length = 64, nullable = false)
    private String uniqId;
    @Column(name = "SKU", length = 64, nullable = false)
    private String sku;
    @Column(name = "NAME_TITLE", length = 500, nullable = false)
    private String name;
    @Lob
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "SALE_PRICE")
    private Double salePrice;
    @Column(name = "CATEGORY", length = 150)
    private String category;
    @Column(name = "BRAND", length = 150)
    private String brand;
}
