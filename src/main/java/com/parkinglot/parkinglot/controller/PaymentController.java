package com.parkinglot.parkinglot.controller;

import com.parkinglot.parkinglot.dto.payment.CreatePaymentRequest;
import com.parkinglot.parkinglot.dto.payment.PaymentResponse;
import com.parkinglot.parkinglot.model.Payment;
import com.parkinglot.parkinglot.service.PaymentService;
import com.parkinglot.parkinglot.repository.PaymentRepository;
import com.parkinglot.parkinglot.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    public PaymentController(PaymentService paymentService, PaymentRepository paymentRepository) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
    }

    @PostMapping
    public PaymentResponse createPayment(@RequestBody(required = false) @Valid CreatePaymentRequest request,
                                         @RequestParam(required = false) Long reservationId,
                                         Authentication authentication) {
        Long resolvedReservationId = request != null ? request.reservationId() : reservationId;
        if (resolvedReservationId == null) {
            throw new IllegalStateException("Reservation id is required");
        }
        return toResponse(paymentService.processPayment(resolvedReservationId, authentication.getName()));
    }

    @GetMapping("/my")
    public List<PaymentResponse> getMyPayments(Authentication authentication) {
        return paymentService.getPaymentsByUser(authentication.getName()).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public PaymentResponse getPayment(@PathVariable Long id, Authentication authentication) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id));
        if (!payment.getReservation().getUser().getEmail().equals(authentication.getName())) {
            throw new ResourceNotFoundException("Payment not found: " + id);
        }
        return toResponse(payment);
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getReservation().getId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getTransactionId(),
                payment.getPaymentTime()
        );
    }
}
