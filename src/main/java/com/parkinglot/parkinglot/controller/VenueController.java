package com.parkinglot.parkinglot.controller;

import com.parkinglot.parkinglot.dto.pricing.PricingResponse;
import com.parkinglot.parkinglot.dto.pricing.SetPricingRequest;
import com.parkinglot.parkinglot.dto.venue.CreateVenueRequest;
import com.parkinglot.parkinglot.dto.venue.UpdateVenueRequest;
import com.parkinglot.parkinglot.dto.venue.VenueResponse;
import com.parkinglot.parkinglot.dto.venue.VenueSummaryResponse;
import com.parkinglot.parkinglot.model.VenueType;
import com.parkinglot.parkinglot.service.PricingService;
import com.parkinglot.parkinglot.service.VenueService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/venues")
public class VenueController {

    private final VenueService venueService;
    private final PricingService pricingService;

    public VenueController(VenueService venueService, PricingService pricingService) {
        this.venueService = venueService;
        this.pricingService = pricingService;
    }

    @PostMapping
    public VenueResponse createVenue(@Valid @RequestBody CreateVenueRequest request, Authentication authentication) {
        return venueService.createVenue(authentication.getName(), request);
    }

    @GetMapping("/my")
    public List<VenueResponse> getMyVenues(Authentication authentication) {
        return venueService.getMyVenues(authentication.getName());
    }

    @GetMapping("/my/{id}")
    public VenueResponse getMyVenue(@PathVariable Long id, Authentication authentication) {
        return venueService.getMyVenueById(authentication.getName(), id);
    }

    @PutMapping("/{id}")
    public VenueResponse updateVenue(@PathVariable Long id,
                                     @Valid @RequestBody UpdateVenueRequest request,
                                     Authentication authentication) {
        return venueService.updateVenue(authentication.getName(), id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteVenue(@PathVariable Long id, Authentication authentication) {
        venueService.deleteVenue(authentication.getName(), id);
    }

    @PostMapping("/{id}/publish")
    public VenueResponse publishVenue(@PathVariable Long id, Authentication authentication) {
        return venueService.publishVenue(authentication.getName(), id);
    }

    @PostMapping("/{id}/unpublish")
    public VenueResponse unpublishVenue(@PathVariable Long id, Authentication authentication) {
        return venueService.unpublishVenue(authentication.getName(), id);
    }

    @GetMapping
    public Page<VenueSummaryResponse> getPublishedVenues(@RequestParam(required = false) String name,
                                                         @RequestParam(required = false) String city,
                                                         @RequestParam(required = false) VenueType type,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "20") int size) {
        return venueService.getPublishedVenues(name, city, type, page, size);
    }

    @GetMapping("/{id}")
    public VenueResponse getPublishedVenue(@PathVariable Long id) {
        return venueService.getPublishedVenueById(id);
    }

    @PostMapping("/{venueId}/pricing")
    public PricingResponse setPricing(@PathVariable Long venueId,
                                      @Valid @RequestBody SetPricingRequest request,
                                      Authentication authentication) {
        return pricingService.setPricing(authentication.getName(), venueId, request);
    }

    @GetMapping("/{venueId}/pricing")
    public List<PricingResponse> getPricing(@PathVariable Long venueId, Authentication authentication) {
        return pricingService.getPricing(authentication.getName(), venueId);
    }
}
