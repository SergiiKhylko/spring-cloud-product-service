package com.ajkko.springcloud.inventoryservice.dto.mapper;

import com.ajkko.springcloud.inventoryservice.dto.response.InventoryResponse;
import com.ajkko.springcloud.inventoryservice.entity.Inventory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class InventoryMapper {
    public InventoryResponse map(Inventory inventory) {
        return InventoryResponse.builder()
                .skuCode(inventory.getSkuCode())
                .isInStock(inventory.getQuantity() > 0)
                .build();
    }

    public List<InventoryResponse> map(Collection<Inventory> inventories) {
        return inventories.stream().map(this::map).toList();
    }

}
