package com.parkinglot.parkinglot.dto.parkingarea;

import jakarta.validation.constraints.NotBlank;

public record CreateParkingAreaRequest(
        @NotBlank(message = "Parking area name is required")
        String name,
        String floor,
        String description
) {
}
