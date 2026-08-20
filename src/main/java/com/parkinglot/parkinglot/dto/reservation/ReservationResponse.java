package com.parkinglot.parkinglot.dto.reservation;

import com.parkinglot.parkinglot.model.ReservationStatus;
import com.parkinglot.parkinglot.model.SlotType;

import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        Long userId,
        Long slotId,
        Integer slotNumber,
        SlotType slotType,
        Long parkingAreaId,
        String parkingAreaName,
        Long venueId,
        String venueName,
        ReservationStatus status,
        LocalDateTime reservationTime,
        LocalDateTime expiryTime
) {
}
