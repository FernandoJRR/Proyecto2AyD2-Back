package com.ayd.inventory_service.stock.models;

import com.ayd.inventory_service.warehouse.models.Warehouse;
import com.ayd.shared.models.Auditor;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Stock extends Auditor {
    private String productId;

    private Integer quantity;

    private Integer minimumStock;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    public Integer sumQuantity(Integer quantity) {
        this.quantity += quantity;
        return this.quantity;
    }

    public Integer subQuantity(Integer quantity) {
        this.quantity -= quantity;
        return this.quantity;
    }
}
