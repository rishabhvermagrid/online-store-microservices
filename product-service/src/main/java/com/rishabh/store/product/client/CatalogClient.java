package com.rishabh.store.product.client;

import com.rishabh.store.product.dto.response.ApiResponse;
import com.rishabh.store.product.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="catalog-service")
public interface CatalogClient {

    @GetMapping("/internal/catalog/products/{uniqId}")
    ApiResponse<ProductResponse> getProductByUniqId(@PathVariable("uniqId") String uniqId);

    //method name no need to match with catalog controller code, it fetched data using the api only
    //ResponseEntity is only the HTTP wrapper. Feign automatically reads the response body, which is: ApiResponse
    @GetMapping("/internal/catalog/products")
    ApiResponse<List<ProductResponse>> getProductsBySku(@RequestParam("sku") String sku);

}
