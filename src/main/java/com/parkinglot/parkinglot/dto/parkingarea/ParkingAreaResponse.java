package com.parkinglot.parkinglot.dto.parkingarea;

import com.parkinglot.parkinglot.model.ParkingAreaStatus;

import java.time.LocalDateTime;

public record ParkingAreaResponse(
        Long id,
        Long venueId,
        String venueName,
        String name,
        String floor,
        String description,
        ParkingAreaStatus status,
        int totalSlots,
        int availableSlots,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
