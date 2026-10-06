package com.rishabh.store.catalog.controller;

import com.rishabh.store.catalog.dto.response.ApiResponse;
import com.rishabh.store.catalog.model.Product;
import com.rishabh.store.catalog.service.CatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/internal/catalog/products")
public class CatalogController {
    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService){
        this.catalogService = catalogService;
    }

    @GetMapping("/{uniqId}")
    public ResponseEntity<ApiResponse<Product>> getProductByUniqId(@PathVariable("uniqId") String uniqId){
        return ResponseEntity.ok(new ApiResponse<Product>(true,"Product fetched Successfully",catalogService.getProductByUniqId(uniqId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getProductsBySku(@RequestParam("sku") String sku){
        List<Product> products = catalogService.getProductsBySku(sku);
        if(products.isEmpty()){
            return ResponseEntity.ok(new ApiResponse<>(true,"No products found for SKU: "+ sku,products));
        }
        return ResponseEntity.ok(new ApiResponse<>(true,"Products fetched successfully",products));
    }
}
