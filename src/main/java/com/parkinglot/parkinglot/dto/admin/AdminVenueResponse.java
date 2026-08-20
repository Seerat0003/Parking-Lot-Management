package com.parkinglot.parkinglot.dto.admin;

import com.parkinglot.parkinglot.model.VenueStatus;
import com.parkinglot.parkinglot.model.VenueType;

import java.time.LocalDateTime;

public record AdminVenueResponse(
        Long id,
        String name,
        VenueType type,
        String city,
        String country,
        VenueStatus status,
        Long ownerId,
        String ownerName,
        String ownerEmail,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
