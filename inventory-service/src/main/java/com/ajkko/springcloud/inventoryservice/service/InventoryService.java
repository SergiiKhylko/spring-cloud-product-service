package com.ajkko.springcloud.inventoryservice.service;

import com.ajkko.springcloud.inventoryservice.dto.mapper.InventoryMapper;
import com.ajkko.springcloud.inventoryservice.dto.response.InventoryResponse;
import com.ajkko.springcloud.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor @Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCode) {
        return inventoryMapper.map(inventoryRepository.findBySkuCodeIn(skuCode));
    }
}
