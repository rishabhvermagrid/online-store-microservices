package com.rishabh.store.inventory.service;

import com.rishabh.store.inventory.dto.response.ProductAvailabilityResponse;
import jakarta.annotation.PostConstruct;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import org.apache.commons.csv.CSVRecord;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class InventoryService {
    //for now HashMap is ok but in future it's not ok to use, A normal HashMap is not thread-safe. If several threads modify it at the same time, it can cause inconsistent data or errors.
    private final Map<String,Boolean> availabilityByProductId = new ConcurrentHashMap<>();
    @PostConstruct
    public void generateAvailabilityFromCsv(){
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
                for (CSVRecord record : csvParser) {
                    String uniqId = record.get("uniq_id");

                    //Returns: the current thread's ThreadLocalRandom
                    if (uniqId != null && !uniqId.isBlank()) {
                        boolean available =
                                ThreadLocalRandom.current().nextBoolean();

                        availabilityByProductId.put(uniqId, available);
                    }
                }
            }

            System.out.println(
                    "Generated availability for "
                            + availabilityByProductId.size()
                            + " products."
            );

        } catch (Exception exception) {
            throw new RuntimeException(
                    "Failed to generate product availability",
                    exception
            );
        }
    }

    public List<ProductAvailabilityResponse> getAvailability(List<String> uniqIds){
        return uniqIds.stream()
                .filter(uniqId -> availabilityByProductId.containsKey(uniqId))
                .map(uniqId -> new ProductAvailabilityResponse(uniqId,availabilityByProductId.get(uniqId)))
                .toList();
    }

}
