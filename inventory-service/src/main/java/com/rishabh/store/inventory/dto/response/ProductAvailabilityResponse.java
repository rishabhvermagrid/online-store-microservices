package com.rishabh.store.inventory.dto.response;

public record ProductAvailabilityResponse(
    String uniqId,
    boolean availability
){

}