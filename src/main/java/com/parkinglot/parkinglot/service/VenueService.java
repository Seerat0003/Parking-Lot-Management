package com.parkinglot.parkinglot.service;

import com.parkinglot.parkinglot.dto.venue.CreateVenueRequest;
import com.parkinglot.parkinglot.dto.venue.UpdateVenueRequest;
import com.parkinglot.parkinglot.dto.venue.VenueResponse;
import com.parkinglot.parkinglot.dto.venue.VenueSummaryResponse;
import com.parkinglot.parkinglot.exception.ResourceNotFoundException;
import com.parkinglot.parkinglot.exception.UnauthorizedVenueAccessException;
import com.parkinglot.parkinglot.exception.VenueNotPublishedException;
import com.parkinglot.parkinglot.model.*;
import com.parkinglot.parkinglot.repository.UserRepository;
import com.parkinglot.parkinglot.repository.VenuePricingRepository;
import com.parkinglot.parkinglot.repository.VenueRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VenueService {

    private final VenueRepository venueRepository;
    private final VenuePricingRepository venuePricingRepository;
    private final UserRepository userRepository;

    public VenueService(VenueRepository venueRepository,
                        VenuePricingRepository venuePricingRepository,
                        UserRepository userRepository) {
        this.venueRepository = venueRepository;
        this.venuePricingRepository = venuePricingRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public VenueResponse createVenue(String vendorEmail, CreateVenueRequest request) {
        User vendor = requireActiveVendor(vendorEmail);
        Venue venue = new Venue();
        applyVenueDetails(venue, request.name(), request.type(), request.description(), request.address(),
                request.city(), request.state(), request.country(), request.latitude(), request.longitude());
        venue.setOwner(vendor);
        venue.setStatus(VenueStatus.DRAFT);
        return toVenueResponse(venueRepository.save(venue));
    }

    @Transactional(readOnly = true)
    public List<VenueResponse> getMyVenues(String vendorEmail) {
        User vendor = requireActiveVendor(vendorEmail);
        return venueRepository.findByOwner(vendor).stream()
                .map(this::toVenueResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public VenueResponse getMyVenueById(String vendorEmail, Long venueId) {
        return toVenueResponse(getOwnedVenue(vendorEmail, venueId));
    }

    @Transactional
    public VenueResponse updateVenue(String vendorEmail, Long venueId, UpdateVenueRequest request) {
        Venue venue = getOwnedVenue(vendorEmail, venueId);
        applyVenueDetails(venue, request.name(), request.type(), request.description(), request.address(),
                request.city(), request.state(), request.country(), request.latitude(), request.longitude());
        return toVenueResponse(venueRepository.save(venue));
    }

    @Transactional
    public void deleteVenue(String vendorEmail, Long venueId) {
        Venue venue = getOwnedVenue(vendorEmail, venueId);
        boolean hasSlots = venue.getParkingAreas().stream().anyMatch(area -> area.getTotalSlots() > 0);
        if (hasSlots) {
            throw new IllegalStateException("Delete parking areas and slots before deleting this venue");
        }
        venueRepository.delete(venue);
    }

    @Transactional
    public VenueResponse publishVenue(String vendorEmail, Long venueId) {
        Venue venue = getOwnedVenue(vendorEmail, venueId);
        if (venuePricingRepository.findByVenue(venue).isEmpty()) {
            throw new IllegalStateException("Set venue pricing before publishing");
        }
        if (!requireAtLeastOneParkingArea(venue)) {
            throw new IllegalStateException("Create at least one parking area before publishing");
        }
        venue.setStatus(VenueStatus.PUBLISHED);
        return toVenueResponse(venueRepository.save(venue));
    }

    @Transactional
    public VenueResponse unpublishVenue(String vendorEmail, Long venueId) {
        Venue venue = getOwnedVenue(vendorEmail, venueId);
        venue.setStatus(VenueStatus.UNPUBLISHED);
        return toVenueResponse(venueRepository.save(venue));
    }

    @Transactional(readOnly = true)
    public Page<VenueSummaryResponse> getPublishedVenues(String name, String city, VenueType type, int page, int size) {
        Page<Venue> venues = venueRepository.searchPublished(VenueStatus.PUBLISHED, blankToNull(city), blankToNull(name),
                type, PageRequest.of(page, size));
        List<VenueSummaryResponse> content = venues.getContent().stream()
                .map(this::toVenueSummaryResponse)
                .toList();
        return new PageImpl<>(content, venues.getPageable(), venues.getTotalElements());
    }

    @Transactional(readOnly = true)
    public VenueResponse getPublishedVenueById(Long venueId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found: " + venueId));
        ensureVenuePublished(venue);
        return toVenueResponse(venue);
    }

    @Transactional(readOnly = true)
    public Venue getOwnedVenue(String vendorEmail, Long venueId) {
        User vendor = requireActiveVendor(vendorEmail);
        return venueRepository.findByOwnerAndId(vendor, venueId)
                .orElseThrow(() -> new UnauthorizedVenueAccessException("You do not own venue: " + venueId));
    }

    @Transactional(readOnly = true)
    public Venue getVenueForRead(String userEmail, Long venueId) {
        User user = getRequiredUser(userEmail);
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found: " + venueId));

        if (user.getRole() == Role.ADMIN) {
            return venue;
        }
        if (user.getRole() == Role.VENDOR && venue.getOwner().getId().equals(user.getId())) {
            return venue;
        }

        ensureVenuePublished(venue);
        return venue;
    }

    @Transactional(readOnly = true)
    public VenueResponse toVenueResponse(Venue venue) {
        int totalSlots = venue.getParkingAreas().stream()
                .mapToInt(area -> area.getTotalSlots() == null ? 0 : area.getTotalSlots())
                .sum();
        int availableSlots = venue.getParkingAreas().stream()
                .mapToInt(area -> area.getAvailableSlots() == null ? 0 : area.getAvailableSlots())
                .sum();

        return new VenueResponse(
                venue.getId(),
                venue.getName(),
                venue.getType(),
                venue.getDescription(),
                venue.getAddress(),
                venue.getCity(),
                venue.getState(),
                venue.getCountry(),
                venue.getLatitude(),
                venue.getLongitude(),
                venue.getOwner().getId(),
                venue.getOwner().getName(),
                venue.getStatus(),
                venue.getParkingAreas().size(),
                totalSlots,
                availableSlots,
                venue.getCreatedAt(),
                venue.getUpdatedAt()
        );
    }

    private VenueSummaryResponse toVenueSummaryResponse(Venue venue) {
        int totalSlots = venue.getParkingAreas().stream()
                .mapToInt(area -> area.getTotalSlots() == null ? 0 : area.getTotalSlots())
                .sum();
        int availableSlots = venue.getParkingAreas().stream()
                .mapToInt(area -> area.getAvailableSlots() == null ? 0 : area.getAvailableSlots())
                .sum();

        return new VenueSummaryResponse(
                venue.getId(),
                venue.getName(),
                venue.getType(),
                venue.getCity(),
                venue.getState(),
                venue.getCountry(),
                venue.getParkingAreas().size(),
                totalSlots,
                availableSlots
        );
    }

    private void ensureVenuePublished(Venue venue) {
        if (venue.getStatus() != VenueStatus.PUBLISHED) {
            throw new VenueNotPublishedException("Venue is not published: " + venue.getId());
        }
    }

    private boolean requireAtLeastOneParkingArea(Venue venue) {
        return venue.getParkingAreas() != null && !venue.getParkingAreas().isEmpty();
    }

    private User requireActiveVendor(String vendorEmail) {
        User vendor = getRequiredUser(vendorEmail);
        if (vendor.getRole() != Role.VENDOR) {
            throw new UnauthorizedVenueAccessException("Vendor access required");
        }
        if (!vendor.isActive()) {
            throw new IllegalStateException("Your account is suspended");
        }
        return vendor;
    }

    private User getRequiredUser(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private void applyVenueDetails(Venue venue,
                                   String name,
                                   VenueType type,
                                   String description,
                                   String address,
                                   String city,
                                   String state,
                                   String country,
                                   Double latitude,
                                   Double longitude) {
        venue.setName(name);
        venue.setType(type);
        venue.setDescription(description);
        venue.setAddress(address);
        venue.setCity(city);
        venue.setState(state);
        venue.setCountry(country);
        venue.setLatitude(latitude);
        venue.setLongitude(longitude);
    }
}
