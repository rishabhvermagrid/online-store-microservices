package com.rishabh.store.inventory;

import com.rishabh.store.inventory.dto.response.ProductAvailabilityResponse;
import com.rishabh.store.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AppTest {

    @Autowired
    private InventoryService inventoryService;

    @Test
    void contextLoads() {
        assertNotNull(inventoryService);
    }

    @Test
    void testGetAvailability() {
        List<ProductAvailabilityResponse> responses =
                inventoryService.getAvailability(List.of("b6c0b6bea69c722939585baeac73c13d"));
        assertNotNull(responses);
        assertFalse(responses.isEmpty());
        assertNotNull(responses.get(0).uniqId());
    }
}
