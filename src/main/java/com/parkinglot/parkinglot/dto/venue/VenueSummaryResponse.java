package com.parkinglot.parkinglot.dto.venue;

import com.parkinglot.parkinglot.model.VenueType;

public record VenueSummaryResponse(
        Long id,
        String name,
        VenueType type,
        String city,
        String state,
        String country,
        int parkingAreaCount,
        int totalSlots,
        int availableSlots
) {
}
