package com.parkinglot.parkinglot.dto.venue;

import com.parkinglot.parkinglot.model.VenueType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateVenueRequest(
        @NotBlank(message = "Name is required")
        String name,
        @NotNull(message = "Venue type is required")
        VenueType type,
        String description,
        @NotBlank(message = "Address is required")
        String address,
        @NotBlank(message = "City is required")
        String city,
        String state,
        @NotBlank(message = "Country is required")
        String country,
        Double latitude,
        Double longitude
) {
}
