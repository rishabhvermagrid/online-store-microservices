package com.rishabh.store.product.controller;

import com.rishabh.store.product.dto.response.ApiResponse;
import com.rishabh.store.product.dto.response.ProductResponse;
import com.rishabh.store.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("products")
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @GetMapping("/{uniqId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductByUniqId(@PathVariable("uniqId") String uniqId){
        ProductResponse productResponse = productService.getAvailableProductByUniqId(uniqId);
        return ResponseEntity.ok(new ApiResponse<>(true,"Products By UniqId: ",productResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductBySKU(@RequestParam("sku") String sku){
        List<ProductResponse> productResponseList = productService.getAvailableProductBySKU(sku);
        if(productResponseList.isEmpty()){
            return ResponseEntity.ok(new ApiResponse<>(true,"No products found with matching SKU : " + sku,productResponseList));
        }
        return ResponseEntity.ok(new ApiResponse<>(true,"Products By SKU",productResponseList));
    }
}
