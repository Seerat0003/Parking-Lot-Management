package com.parkinglot.parkinglot.dto.pricing;

import com.parkinglot.parkinglot.model.SlotType;

import java.time.LocalDateTime;

public record PricingResponse(
        Long id,
        Long venueId,
        String venueName,
        SlotType slotType,
        Double pricePerHour,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
