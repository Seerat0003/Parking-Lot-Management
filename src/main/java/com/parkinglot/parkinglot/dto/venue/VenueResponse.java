package com.parkinglot.parkinglot.dto.venue;

import com.parkinglot.parkinglot.model.VenueStatus;
import com.parkinglot.parkinglot.model.VenueType;

import java.time.LocalDateTime;

public record VenueResponse(
        Long id,
        String name,
        VenueType type,
        String description,
        String address,
        String city,
        String state,
        String country,
        Double latitude,
        Double longitude,
        Long ownerId,
        String ownerName,
        VenueStatus status,
        int parkingAreaCount,
        int totalSlots,
        int availableSlots,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
