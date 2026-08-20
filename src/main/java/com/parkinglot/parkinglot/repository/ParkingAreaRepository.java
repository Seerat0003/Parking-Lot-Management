package com.parkinglot.parkinglot.repository;

import com.parkinglot.parkinglot.model.ParkingArea;
import com.parkinglot.parkinglot.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParkingAreaRepository extends JpaRepository<ParkingArea, Long> {

    List<ParkingArea> findByVenue(Venue venue);

    Optional<ParkingArea> findByVenueAndId(Venue venue, Long id);
}
