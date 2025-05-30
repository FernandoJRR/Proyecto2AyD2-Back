package com.ayd.inventory_service.stock.ports;

import java.util.List;

import com.ayd.shared.exceptions.*;
import com.ayd.sharedInventoryService.stock.dto.ModifyStockRequest;
import com.ayd.inventory_service.stock.models.Stock;

public interface ForModifiyStockPort {
    public Stock substractStockByProductIdAndWarehouseId(String productId, String warehouseId, Integer quantity)
            throws NotFoundException, IllegalStateException;

    public List<Stock> substractVariousStockByProductIdAndWarehouseId(List<ModifyStockRequest> modifyStockRequest)
            throws NotFoundException, IllegalStateException;
}
