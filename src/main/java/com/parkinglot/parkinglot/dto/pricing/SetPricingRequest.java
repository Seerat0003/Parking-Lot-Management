package com.parkinglot.parkinglot.dto.pricing;

import com.parkinglot.parkinglot.model.SlotType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SetPricingRequest(
        @NotNull(message = "Slot type is required")
        SlotType slotType,
        @NotNull(message = "Price per hour is required")
        @Positive(message = "Price per hour must be positive")
        Double pricePerHour
) {
}
