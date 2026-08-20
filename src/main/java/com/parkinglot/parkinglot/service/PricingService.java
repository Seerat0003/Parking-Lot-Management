package com.parkinglot.parkinglot.service;

import com.parkinglot.parkinglot.dto.pricing.PricingResponse;
import com.parkinglot.parkinglot.dto.pricing.SetPricingRequest;
import com.parkinglot.parkinglot.model.Slot;
import com.parkinglot.parkinglot.model.Venue;
import com.parkinglot.parkinglot.model.VenuePricing;
import com.parkinglot.parkinglot.repository.VenuePricingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PricingService {

    private final VenuePricingRepository venuePricingRepository;
    private final VenueService venueService;

    public PricingService(VenuePricingRepository venuePricingRepository, VenueService venueService) {
        this.venuePricingRepository = venuePricingRepository;
        this.venueService = venueService;
    }

    @Transactional
    public PricingResponse setPricing(String vendorEmail, Long venueId, SetPricingRequest request) {
        Venue venue = venueService.getOwnedVenue(vendorEmail, venueId);
        VenuePricing pricing = venuePricingRepository.findByVenueAndSlotType(venue, request.slotType())
                .orElseGet(VenuePricing::new);
        pricing.setVenue(venue);
        pricing.setSlotType(request.slotType());
        pricing.setPricePerHour(request.pricePerHour());
        return toResponse(venuePricingRepository.save(pricing));
    }

    @Transactional(readOnly = true)
    public List<PricingResponse> getPricing(String vendorEmail, Long venueId) {
        Venue venue = venueService.getOwnedVenue(vendorEmail, venueId);
        return venuePricingRepository.findByVenue(venue).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Double calculateAmount(Slot slot, LocalDateTime start, LocalDateTime end) {
        if (slot.getParkingArea() == null) {
            return 0.0;
        }

        Venue venue = slot.getParkingArea().getVenue();
        VenuePricing pricing = venuePricingRepository.findByVenueAndSlotType(venue, slot.getType())
                .orElseThrow(() -> new IllegalStateException("No pricing configured for slot type " + slot.getType()));

        LocalDateTime safeStart = start == null ? LocalDateTime.now() : start;
        LocalDateTime safeEnd = end == null || end.isBefore(safeStart) ? safeStart.plusHours(1) : end;
        long minutes = Math.max(1L, Duration.between(safeStart, safeEnd).toMinutes());
        long billableHours = Math.max(1L, (long) Math.ceil(minutes / 60.0));
        return pricing.getPricePerHour() * billableHours;
    }

    private PricingResponse toResponse(VenuePricing pricing) {
        return new PricingResponse(
                pricing.getId(),
                pricing.getVenue().getId(),
                pricing.getVenue().getName(),
                pricing.getSlotType(),
                pricing.getPricePerHour(),
                pricing.getCreatedAt(),
                pricing.getUpdatedAt()
        );
    }
}
