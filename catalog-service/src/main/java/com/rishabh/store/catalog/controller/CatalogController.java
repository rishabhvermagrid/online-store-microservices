package com.rishabh.store.catalog.controller;

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
    public ResponseEntity<Product> getProductByUniqId(@PathVariable("uniqId") String uniqId){
        return ResponseEntity.ok(catalogService.getProductByUniqId(uniqId));
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProductsBySku(@RequestParam("sku") String sku){
        return ResponseEntity.ok(catalogService.getProductsBySku(sku));
    }

}
