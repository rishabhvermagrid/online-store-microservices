package com.rishabh.store.catalog.service;

import com.rishabh.store.catalog.model.Product;
import jakarta.annotation.PostConstruct;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CatalogService {
    /* for testing api's before using real data from CSV
    private final List<Product> products = List.of(
            new Product("id-101", "SKU-1", "Blue Shirt",
                    "Cotton shirt", 899.0, "Clothing", "Brand A"),

            new Product("id-102", "SKU-1", "Black Shirt",
                    "Casual shirt", 999.0, "Clothing", "Brand B")
    );
     */

    //in memory list, final so that its reference can not be changed later but we can add products
    private final List<Product> products = new ArrayList<>();


    //this method will run automatically when application starts after creating bean and injecting dependencies
    @PostConstruct
    public void loadProductsFromCsv() {
        try {

            // Finds products.csv inside src/main/resources
            ClassPathResource resource = new ClassPathResource("products.csv");

            // Opens the CSV file and automatically closes resources after reading
            try (
                    // Reads CSV file text using UTF-8 encoding
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(
                                    resource.getInputStream(),
                                    StandardCharsets.UTF_8
                            )
                    );

                    // Creates a parser to read CSV rows correctly
                    CSVParser csvParser = CSVFormat.DEFAULT.builder()
                            // first row as header
                            .setHeader()
                            // dont treat header as products data
                            .setSkipHeaderRecord(true)
                            // Allows header without case sensitivity
                            .setIgnoreHeaderCase(true)
                            .setTrim(true)

                            // Builds the CSV configuration and parses the file
                            .build()
                            .parse(reader)
            ) {
                for (CSVRecord record : csvParser) {
                    try {
                        String uniqId = record.get("uniq_id");
                        String sku = record.get("sku");
                        String name = record.get("name_title");
                        String description = record.get("description");
                        String salePriceStr = record.get("sale_price");
                        // Converts sale price String to Double;
                        // uses 0.0 when price is missing or blank
                        Double salePrice = (salePriceStr != null && !salePriceStr.isBlank())
                                ? Double.parseDouble(salePriceStr.trim())
                                : 0.0;
                        String category = record.get("category");
                        String brand = record.get("brand");

                        // Creates a Product object and adds it to the in-memory products list
                        products.add(new Product(
                                uniqId,
                                sku,
                                name,
                                description,
                                salePrice,
                                category,
                                brand
                        ));

                    } catch (Exception e) {
                    }
                }
            }
            System.out.println(
                    "Loaded " + products.size() + " products from CSV successfully."
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to load products.csv", e);
        }
    }
    // resource.getInputStream() -> open csv file as raw bytes
    // new InputStreamReader() -> Converts those raw bytes into readable text using UTF-8 encoding.

    public Product getProductByUniqId(String uniqId){
        return products.stream()
                .filter(product -> product.uniqId().equals(uniqId))
                .findFirst()
                .orElseThrow(()->new RuntimeException("Product not found"));
    }

    public List<Product> getProductsBySku(String sku){
        return products.stream()
                .filter( product -> product.sku().equalsIgnoreCase(sku))
                .toList();
    }
}
