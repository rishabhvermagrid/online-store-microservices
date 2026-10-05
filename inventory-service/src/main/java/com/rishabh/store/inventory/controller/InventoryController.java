package com.rishabh.store.inventory.controller;

import com.rishabh.store.inventory.dto.response.ApiResponse;
import com.rishabh.store.inventory.dto.response.ProductAvailabilityResponse;
import com.rishabh.store.inventory.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/inventory")
public class InventoryController {
    private final InventoryService inventoryService;
    public InventoryController(InventoryService inventoryService){
        this.inventoryService = inventoryService;
    }

    @PostMapping("/availability")
    public ApiResponse<List<ProductAvailabilityResponse>> getAvailability(@RequestBody List<String> uniqIds){
        List<ProductAvailabilityResponse> availabilityResponses = inventoryService.getAvailability(uniqIds);
        return new ApiResponse<>(true,"Availability Fetched Successfully",availabilityResponses);
    }
}
