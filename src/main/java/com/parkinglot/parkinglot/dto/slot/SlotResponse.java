package com.parkinglot.parkinglot.dto.slot;

import com.parkinglot.parkinglot.model.SlotStatus;
import com.parkinglot.parkinglot.model.SlotType;

import java.time.LocalDateTime;

public record SlotResponse(
        Long id,
        Integer slotNumber,
        SlotStatus status,
        SlotType type,
        Long parkingAreaId,
        String parkingAreaName,
        Long venueId,
        String venueName,
        Long parkingLotId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
