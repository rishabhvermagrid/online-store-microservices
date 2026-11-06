package com.rishabh.store.catalog.service;

import com.rishabh.store.catalog.entity.ProductEntity;
import com.rishabh.store.catalog.model.Product;
import com.rishabh.store.catalog.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CatalogService {

    private final ProductRepository productRepository;

    // This method runs automatically when the application starts,
    // after the bean is created and dependencies are injected.
    @PostConstruct
    @Transactional
    public void seedDatabaseFromCsv() {
        // If data is already in Oracle DB, do not re-insert
        if (productRepository.count() > 0) {
            log.info("Oracle DB already contains {} products. Skipping CSV seed.", productRepository.count());
            return;
        }

        log.info("Oracle DB is empty. Seeding products from CSV...");
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
            List<ProductEntity> batch = new ArrayList<>();

            // Iterates through each CSV record.
            for (CSVRecord csvRecord : csvParser) {
                processCsvRecordSafely(csvRecord, batch);

                // Save in batches of 500 records to optimize DB throughput
                if (batch.size() >= 500) {
                    productRepository.saveAll(batch);
                    batch.clear();
                }
            }

            // Save any remaining records
            if (!batch.isEmpty()) {
                productRepository.saveAll(batch);
                batch.clear();
            }

            // Logs the total number of products loaded successfully.
            log.info(
                    "Loaded {} products from CSV successfully into Oracle DB.",
                    productRepository.count()
            );

        } catch (Exception e) {
            // Throws an exception if the CSV file cannot be loaded or read.
            throw new RuntimeException("Failed to load products.csv", e);
        }
    }

    // Processes a CSV record and logs errors without stopping
    // the processing of subsequent records.
    private void processCsvRecordSafely(CSVRecord csvRecord, List<ProductEntity> batch) {
        try {
            processCsvRecord(csvRecord, batch);
        } catch (Exception e) {
            // Logs the record number and error details
            // if a record cannot be processed.
            log.error(
                    "Failed to parse CSV record {}",
                    csvRecord.getRecordNumber(),
                    e
            );
        }
    }

    private void processCsvRecord(CSVRecord csvRecord, List<ProductEntity> batch) {

        // Reads product details from the corresponding CSV columns.
        String uniqId = csvRecord.get("uniq_id");
        String sku = csvRecord.get("sku");
        if (sku == null || sku.isBlank()
                || "null".equalsIgnoreCase(sku.trim())) {
            log.warn(
                    "Skipping CSV record {}: sku is missing",
                    csvRecord.getRecordNumber()
            );
            return;
        }
        String name = csvRecord.get("name_title");

        if (name == null || name.isBlank()
                || "null".equalsIgnoreCase(name.trim())) {
            log.warn(
                    "Skipping CSV record {}: name_title is missing",
                    csvRecord.getRecordNumber()
            );
            return;
        }
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
        batch.add(ProductEntity.builder()
                .uniqId(uniqId)
                .sku(sku)
                .name(name)
                .description(description)
                .salePrice(salePrice)
                .category(category)
                .brand(brand)
                .build());
    }

    // Finds a product using its unique ID.
    public Product getProductByUniqId(String uniqId) {
        return productRepository.findById(uniqId)
                .map(Product::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found with id: " + uniqId
                ));
    }

    // Returns all products matching the given SKU.
    public List<Product> getProductsBySku(String sku) {
        return productRepository.findBySkuIgnoreCase(sku).stream()
                .map(Product::fromEntity)
                .toList();
    }
}
