package com.parkinglot.parkinglot.repository;

import com.parkinglot.parkinglot.model.Payment;
import com.parkinglot.parkinglot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByReservation_User(User user);
}
