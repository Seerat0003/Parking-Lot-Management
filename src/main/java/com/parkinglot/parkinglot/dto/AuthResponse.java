package com.parkinglot.parkinglot.dto;

public record AuthResponse(
        String token,
        String email,
        String role
) {
}
