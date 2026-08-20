package com.parkinglot.parkinglot.dto.reservation;

import jakarta.validation.constraints.NotNull;

public record CreateReservationRequest(
        @NotNull(message = "Slot id is required")
        Long slotId
) {
}
