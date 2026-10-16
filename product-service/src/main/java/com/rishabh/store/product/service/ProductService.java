package com.rishabh.store.product.service;

import com.rishabh.store.product.client.CatalogClient;
import com.rishabh.store.product.client.InventoryClient;
import com.rishabh.store.product.dto.response.ApiResponse;
import com.rishabh.store.product.dto.response.ProductAvailabilityResponse;
import com.rishabh.store.product.dto.response.ProductResponse;
import com.rishabh.store.product.exception.ProductUnavailableException;
import com.rishabh.store.product.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final CatalogClient catalogClient;
    private final InventoryClient inventoryClient;
    ProductService(CatalogClient catalogClient, InventoryClient inventoryClient){
        this.catalogClient = catalogClient;
        this.inventoryClient = inventoryClient;
    }

    @CircuitBreaker(name="productDependencies")
    @Retry(name = "productDependencies", fallbackMethod = "getAvailableProductByUniqIdFallback")
    public ProductResponse getAvailableProductByUniqId(String uniqId){
        ProductResponse productResponse =  catalogClient.getProductByUniqId(uniqId).data();
        ApiResponse<List<ProductAvailabilityResponse>> inventoryResponse= inventoryClient.getAvailability(List.of(uniqId));

        boolean available = inventoryResponse.data().stream()
                .anyMatch(item ->
                        item.uniqId().equals(uniqId)
                                && item.availability()
                );

        if (!available) {
            throw new ProductUnavailableException("Product is currently out of stock or unavailable");
        }

        return productResponse;
    }

    public List<ProductResponse> getAvailableProductBySKU(String sku){
        List<ProductResponse> products =
                catalogClient.getProductsBySku(sku).data();

//        List<String> uniqIds = new ArrayList<>();
//        for(ProductResponse productResponse : productResponseList){
//            uniqIds.add(productResponse.uniqId());
//        }

        List<String> uniqIds = products.stream()
                .map(product -> product.uniqId())
                .toList();

        ApiResponse<List<ProductAvailabilityResponse>> avalabilityResponseList =
                inventoryClient.getAvailability(uniqIds);

        Set<String> availableProductIds = avalabilityResponseList.data().stream()
                .filter(product -> product.availability())
                .map(product -> product.uniqId())
                .collect(Collectors.toSet());

        return products.stream()
                .filter(product -> availableProductIds.contains(product.uniqId()))
                .toList();
    }

    //fallback method
    public ProductResponse getAvailableProductByUniqIdFallback(String uniqId, Throwable throwable){
        if(throwable instanceof ProductUnavailableException exception){
            throw exception;
        }
        // if product is not available in catalog
        if (throwable instanceof feign.FeignException.NotFound) {
            throw new ProductUnavailableException("Product not found with id: " + uniqId);
        }
        throw new ServiceUnavailableException("Catalog or Inventory Service is unavailable");
    }
}
/*
How Both Work Together in Production:

   1. Attempt 1: product-service calls catalog-service. If a network glitch occurs, @Retry intercepts it.
   2. Attempt 2: Wait 500ms (backoff), retry. Still fails.
   3. Attempt 3: Wait 1000ms (exponential multiplier 2), retry. Still fails.
   4. Fallback: All 3 attempts exhausted ➔ getAvailableProductByUniqIdFallback(...) executes and returns a clean 503 response.
   5. Circuit Breaker: Records this failure. Once failures exceed failure-rate-threshold: 50, the circuit OPENS, fast-failing future calls immediately to protect the downstream
      service.

 */
