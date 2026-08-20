package com.parkinglot.parkinglot.repository;

import com.parkinglot.parkinglot.model.SlotType;
import com.parkinglot.parkinglot.model.Venue;
import com.parkinglot.parkinglot.model.VenuePricing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VenuePricingRepository extends JpaRepository<VenuePricing, Long> {

    Optional<VenuePricing> findByVenueAndSlotType(Venue venue, SlotType slotType);

    List<VenuePricing> findByVenue(Venue venue);
}
