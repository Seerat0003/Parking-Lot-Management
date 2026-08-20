package com.parkinglot.parkinglot.dto.slot;

import com.parkinglot.parkinglot.model.SlotStatus;
import com.parkinglot.parkinglot.model.SlotType;
import jakarta.validation.constraints.NotNull;

public record UpdateSlotRequest(
        @NotNull(message = "Slot number is required")
        Integer slotNumber,
        @NotNull(message = "Slot type is required")
        SlotType type,
        @NotNull(message = "Slot status is required")
        SlotStatus status
) {
}
