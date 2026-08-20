package com.parkinglot.parkinglot.repository;

import com.parkinglot.parkinglot.model.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {
}