package com.parkinglot.parkinglot.dto.slot;

import com.parkinglot.parkinglot.model.SlotStatus;
import com.parkinglot.parkinglot.model.SlotType;
import jakarta.validation.constraints.NotNull;

public record CreateSlotRequest(
        @NotNull(message = "Slot number is required")
        Integer slotNumber,
        SlotType type,
        SlotStatus status
) {
}
