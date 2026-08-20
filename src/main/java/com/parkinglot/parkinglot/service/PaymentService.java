package com.parkinglot.parkinglot.service;

import com.parkinglot.parkinglot.exception.ResourceNotFoundException;
import com.parkinglot.parkinglot.model.*;
import com.parkinglot.parkinglot.repository.PaymentRepository;
import com.parkinglot.parkinglot.repository.ReservationRepository;
import com.parkinglot.parkinglot.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final PricingService pricingService;

    public PaymentService(PaymentRepository paymentRepository,
                          ReservationRepository reservationRepository,
                          UserRepository userRepository,
                          PricingService pricingService) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.pricingService = pricingService;
    }

    @Transactional
    public Payment processPayment(Long reservationId, String userEmail) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + reservationId));

        if (!reservation.getUser().getEmail().equals(userEmail)) {
            throw new ResourceNotFoundException("Reservation not found: " + reservationId);
        }

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new IllegalStateException("Reservation is not in a payable state");
        }

        Double amount = pricingService.calculateAmount(
                reservation.getSlot(),
                reservation.getReservationTime(),
                reservation.getExpiryTime()
        );

        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setPaymentTime(LocalDateTime.now());

        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationRepository.save(reservation);

        return paymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));
        return paymentRepository.findByReservation_User(user);
    }
}
