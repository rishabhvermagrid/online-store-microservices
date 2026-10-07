package com.rishabh.store.product.client;

import com.rishabh.store.product.dto.response.ApiResponse;
import com.rishabh.store.product.dto.response.ProductAvailabilityResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("inventory-service")
public interface InventoryClient {

    @PostMapping("internal/inventory/availability")
    ApiResponse<List<ProductAvailabilityResponse>> getAvailability(@RequestBody List<String> uniqIds);
}
