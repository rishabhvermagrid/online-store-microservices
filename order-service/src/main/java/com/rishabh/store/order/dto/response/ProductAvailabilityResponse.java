package com.rishabh.store.order.dto.response;

public record ProductAvailabilityResponse(
        String uniqId,
        boolean availability
) {
}

