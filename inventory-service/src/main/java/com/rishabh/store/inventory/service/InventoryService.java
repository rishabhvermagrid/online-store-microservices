package com.rishabh.store.inventory.service;

import com.rishabh.store.inventory.dto.response.ProductAvailabilityResponse;
import com.rishabh.store.inventory.entity.InventoryEntity;
import com.rishabh.store.inventory.repository.InventoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @PostConstruct
    public void generateAvailabilityFromCsv() {
        if (inventoryRepository.count() > 0) {
            log.info("Oracle DB already contains {} inventory records. Skipping CSV seed.", inventoryRepository.count());
            return;
        }

        log.info("Seeding inventory data into Oracle DB from products.csv...");
        List<InventoryEntity> batch = new ArrayList<>();

        try {
            ClassPathResource resource = new ClassPathResource("products.csv");
            try (
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(
                                    resource.getInputStream(),
                                    StandardCharsets.UTF_8
                            )
                    );
                    CSVParser csvParser = CSVFormat.DEFAULT.builder()
                            .setHeader()
                            .setSkipHeaderRecord(true)
                            .setIgnoreHeaderCase(true)
                            .setTrim(true)
                            .build()
                            .parse(reader)
            ) {
                for (CSVRecord csvRecord : csvParser) {
                    String uniqId = csvRecord.get("uniq_id");

                    if (uniqId != null && !uniqId.isBlank()) {
                        int quantity = ThreadLocalRandom.current().nextInt(0, 50);
                        boolean available = quantity > 0;

                        batch.add(InventoryEntity.builder()
                                .uniqId(uniqId)
                                .quantity(quantity)
                                .available(available)
                                .build());

                        if (batch.size() >= 500) {
                            inventoryRepository.saveAll(batch);
                            batch.clear();
                        }
                    }
                }

                if (!batch.isEmpty()) {
                    inventoryRepository.saveAll(batch);
                    batch.clear();
                }
            }

            log.info("Successfully seeded {} inventory records into Oracle DB.", inventoryRepository.count());

        } catch (Exception exception) {
            log.error("Failed to seed product inventory", exception);
            throw new RuntimeException("Failed to seed product inventory", exception);
        }
    }

    public List<ProductAvailabilityResponse> getAvailability(List<String> uniqIds) {
        if (uniqIds == null || uniqIds.isEmpty()) {
            return List.of();
        }

        // Oracle IN clause is limited to 1000 items; chunk if larger
        if (uniqIds.size() <= 999) {
            return inventoryRepository.findByUniqIdIn(uniqIds).stream()
                    .map(entity -> new ProductAvailabilityResponse(entity.getUniqId(), entity.getAvailable()))
                    .toList();
        }

        List<ProductAvailabilityResponse> responses = new ArrayList<>();
        for (int i = 0; i < uniqIds.size(); i += 999) {
            List<String> subList = uniqIds.subList(i, Math.min(i + 999, uniqIds.size()));
            responses.addAll(inventoryRepository.findByUniqIdIn(subList).stream()
                    .map(entity -> new ProductAvailabilityResponse(entity.getUniqId(), entity.getAvailable()))
                    .toList());
        }
        return responses;
    }

}
