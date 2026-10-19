
package com.rishabh.store.catalog.service;

import com.rishabh.store.catalog.model.Product;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CatalogService {

    /*
     * For testing APIs before using real data from CSV
     *
     * private final List<Product> products = List.of(
     *         new Product("id-101", "SKU-1", "Blue Shirt",
     *                 "Cotton shirt", 899.0, "Clothing", "Brand A"),
     *
     *         new Product("id-102", "SKU-1", "Black Shirt",
     *                 "Casual shirt", 999.0, "Clothing", "Brand B")
     * );
     */

    // In-memory list; final means its reference cannot be changed later,
    // but we can still add products to the list.
    private final List<Product> products = new ArrayList<>();

    // This method runs automatically when the application starts,
    // after the bean is created and dependencies are injected.
    @PostConstruct
    public void loadProductsFromCsv() {

        // Finds products.csv inside src/main/resources
        ClassPathResource resource = new ClassPathResource("products.csv");

        try (
                // Reads CSV file text using UTF-8 encoding.
                // Resources are automatically closed after reading.
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                resource.getInputStream(),
                                StandardCharsets.UTF_8
                        )
                );

                // Creates a parser to read CSV rows correctly.
                CSVParser csvParser = CSVFormat.DEFAULT.builder()
                        // Uses the first row as column headers.
                        .setHeader()

                        // Does not treat the header row as product data.
                        .setSkipHeaderRecord(true)

                        // Allows header names to be accessed without
                        // case sensitivity.
                        .setIgnoreHeaderCase(true)

                        // Removes leading and trailing whitespace.
                        .setTrim(true)

                        // Builds the CSV configuration and parses the file.
                        .build()
                        .parse(reader)
        ) {
            // Iterates through each CSV record.
            for (CSVRecord csvRecord : csvParser) {
                processCsvRecordSafely(csvRecord);
            }

            // Logs the total number of products loaded successfully.
            log.info(
                    "Loaded {} products from CSV successfully.",
                    products.size()
            );

        } catch (Exception e) {
            // Throws an exception if the CSV file cannot be loaded or read.
            throw new RuntimeException("Failed to load products.csv", e);
        }
    }

    // Processes a CSV record and logs errors without stopping
    // the processing of subsequent records.
    private void processCsvRecordSafely(CSVRecord csvRecord) {
        try {
            processCsvRecord(csvRecord);
        } catch (IllegalArgumentException e) {
            // Logs the record number and error details
            // if a record cannot be processed.
            log.error(
                    "Failed to parse CSV record {}",
                    csvRecord.getRecordNumber(),
                    e
            );
        }
    }

    // Converts one CSV record into a Product object and adds it
    // to the in-memory products list.
    private void processCsvRecord(CSVRecord csvRecord) {

        // Reads product details from the corresponding CSV columns.
        String uniqId = csvRecord.get("uniq_id");
        String sku = csvRecord.get("sku");
        String name = csvRecord.get("name_title");
        String description = csvRecord.get("description");

        // Converts sale price from String to double.
        // Uses 0.0 when the price is missing or blank.
        String salePriceStr = csvRecord.get("sale_price");
        double salePrice = (salePriceStr != null && !salePriceStr.isBlank())
                ? Double.parseDouble(salePriceStr.trim())
                : 0.0;

        // Reads the product category and brand.
        String category = csvRecord.get("category");
        String brand = csvRecord.get("brand");

        // Creates a Product object and adds it to the in-memory list.
        products.add(new Product(
                uniqId,
                sku,
                name,
                description,
                salePrice,
                category,
                brand
        ));
    }

    // Finds a product using its unique ID.
    public Product getProductByUniqId(String uniqId) {
        return products.stream()
                .filter(product -> product.uniqId().equals(uniqId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found with id: " + uniqId
                ));
    }

    // Returns all products matching the given SKU.
    public List<Product> getProductsBySku(String sku) {
        return products.stream()
                .filter(product -> product.sku().equalsIgnoreCase(sku))
                .toList();
    }
}
