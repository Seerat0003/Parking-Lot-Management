package com.parkinglot.parkinglot.dto.payment;

import jakarta.validation.constraints.NotNull;

public record CreatePaymentRequest(
        @NotNull(message = "Reservation id is required")
        Long reservationId
) {
}
