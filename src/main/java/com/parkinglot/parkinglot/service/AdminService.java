package com.parkinglot.parkinglot.service;

import com.parkinglot.parkinglot.dto.admin.AdminVenueResponse;
import com.parkinglot.parkinglot.dto.admin.VendorResponse;
import com.parkinglot.parkinglot.exception.ResourceNotFoundException;
import com.parkinglot.parkinglot.model.Role;
import com.parkinglot.parkinglot.model.User;
import com.parkinglot.parkinglot.model.Venue;
import com.parkinglot.parkinglot.model.VenueStatus;
import com.parkinglot.parkinglot.repository.UserRepository;
import com.parkinglot.parkinglot.repository.VenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final VenueRepository venueRepository;

    public AdminService(UserRepository userRepository, VenueRepository venueRepository) {
        this.userRepository = userRepository;
        this.venueRepository = venueRepository;
    }

    @Transactional(readOnly = true)
    public List<VendorResponse> getAllVendors() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.VENDOR)
                .map(this::toVendorResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdminVenueResponse> getAllVenues() {
        return venueRepository.findAll().stream()
                .map(this::toAdminVenueResponse)
                .toList();
    }

    @Transactional
    public AdminVenueResponse suspendVenue(Long venueId) {
        Venue venue = getVenue(venueId);
        venue.setStatus(VenueStatus.SUSPENDED);
        return toAdminVenueResponse(venueRepository.save(venue));
    }

    @Transactional
    public AdminVenueResponse restoreVenue(Long venueId) {
        Venue venue = getVenue(venueId);
        venue.setStatus(VenueStatus.UNPUBLISHED);
        return toAdminVenueResponse(venueRepository.save(venue));
    }

    @Transactional
    public VendorResponse suspendVendor(Long vendorId) {
        User vendor = getVendor(vendorId);
        vendor.setActive(false);
        userRepository.save(vendor);
        venueRepository.findByOwner(vendor).forEach(venue -> venue.setStatus(VenueStatus.SUSPENDED));
        return toVendorResponse(vendor);
    }

    @Transactional
    public VendorResponse restoreVendor(Long vendorId) {
        User vendor = getVendor(vendorId);
        vendor.setActive(true);
        return toVendorResponse(userRepository.save(vendor));
    }

    private User getVendor(Long vendorId) {
        User user = userRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found: " + vendorId));
        if (user.getRole() != Role.VENDOR) {
            throw new ResourceNotFoundException("Vendor not found: " + vendorId);
        }
        return user;
    }

    private Venue getVenue(Long venueId) {
        return venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found: " + venueId));
    }

    private VendorResponse toVendorResponse(User user) {
        return new VendorResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private AdminVenueResponse toAdminVenueResponse(Venue venue) {
        return new AdminVenueResponse(
                venue.getId(),
                venue.getName(),
                venue.getType(),
                venue.getCity(),
                venue.getCountry(),
                venue.getStatus(),
                venue.getOwner().getId(),
                venue.getOwner().getName(),
                venue.getOwner().getEmail(),
                venue.getCreatedAt(),
                venue.getUpdatedAt()
        );
    }
}
