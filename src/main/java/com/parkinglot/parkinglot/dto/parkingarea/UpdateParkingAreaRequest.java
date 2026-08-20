package com.parkinglot.parkinglot.dto.parkingarea;

import com.parkinglot.parkinglot.model.ParkingAreaStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateParkingAreaRequest(
        @NotBlank(message = "Parking area name is required")
        String name,
        String floor,
        String description,
        @NotNull(message = "Parking area status is required")
        ParkingAreaStatus status
) {
}
