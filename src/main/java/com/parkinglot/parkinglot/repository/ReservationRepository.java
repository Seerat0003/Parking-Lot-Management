package com.parkinglot.parkinglot.repository;

import com.parkinglot.parkinglot.model.Reservation;
import com.parkinglot.parkinglot.model.ReservationStatus;
import com.parkinglot.parkinglot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
    List<Reservation> findByStatusAndExpiryTimeBefore(ReservationStatus status, LocalDateTime time);
}
