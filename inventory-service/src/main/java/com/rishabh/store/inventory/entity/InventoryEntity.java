package com.rishabh.store.inventory.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "INVENTORY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryEntity {
    @Id
    @Column(name="UNIQ_ID", length = 64, nullable = false)
    private String uniqId;

    @Column(name="QUANTITY", nullable = false)
    private Integer quantity;
    @Column(name="AVAILABLE", nullable = false)
    private Boolean available;
}
