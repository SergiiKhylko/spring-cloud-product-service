package com.ajkko.springcloud.inventoryservice.bootstrap;

import com.ajkko.springcloud.inventoryservice.entity.Inventory;
import com.ajkko.springcloud.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (inventoryRepository.count() > 0) {
            return;
        }

        var iPhone13 = Inventory.builder().skuCode("iphone_13").quantity(100).build();
        var iPhone13red = Inventory.builder().skuCode("iphone_13_red").quantity(0).build();
        inventoryRepository.save(iPhone13);
        inventoryRepository.save(iPhone13red);
    }
}
