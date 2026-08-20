package com.parkinglot.parkinglot.dto.admin;

import java.time.LocalDateTime;

public record VendorResponse(
        Long id,
        String name,
        String email,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
