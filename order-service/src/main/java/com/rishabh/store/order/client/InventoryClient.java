package com.rishabh.store.order.client;

import com.rishabh.store.order.dto.response.ApiResponse;
import com.rishabh.store.order.dto.response.ProductAvailabilityResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "inventory-service")
public interface InventoryClient {
    @PostMapping
    ApiResponse<List<ProductAvailabilityResponse>> getAvailability(@RequestBody List<String> uniqIds);
}
