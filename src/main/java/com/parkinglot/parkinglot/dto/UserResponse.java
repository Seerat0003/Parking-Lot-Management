package com.parkinglot.parkinglot.dto;

public record UserResponse(
        Long id,
        String name,
        String email,
        String role
) {
}
