package com.parkinglot.parkinglot.dto.payment;

import com.parkinglot.parkinglot.model.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long reservationId,
        Double amount,
        PaymentStatus status,
        String transactionId,
        LocalDateTime paymentTime
) {
}
