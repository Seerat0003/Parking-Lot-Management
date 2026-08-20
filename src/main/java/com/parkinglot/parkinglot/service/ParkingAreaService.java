package com.parkinglot.parkinglot.service;

import com.parkinglot.parkinglot.dto.parkingarea.CreateParkingAreaRequest;
import com.parkinglot.parkinglot.dto.parkingarea.ParkingAreaResponse;
import com.parkinglot.parkinglot.dto.parkingarea.UpdateParkingAreaRequest;
import com.parkinglot.parkinglot.exception.ResourceNotFoundException;
import com.parkinglot.parkinglot.model.ParkingArea;
import com.parkinglot.parkinglot.model.Role;
import com.parkinglot.parkinglot.model.User;
import com.parkinglot.parkinglot.model.Venue;
import com.parkinglot.parkinglot.repository.ParkingAreaRepository;
import com.parkinglot.parkinglot.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParkingAreaService {

    private final ParkingAreaRepository parkingAreaRepository;
    private final VenueService venueService;
    private final UserRepository userRepository;

    public ParkingAreaService(ParkingAreaRepository parkingAreaRepository,
                              VenueService venueService,
                              UserRepository userRepository) {
        this.parkingAreaRepository = parkingAreaRepository;
        this.venueService = venueService;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ParkingAreaResponse> getParkingAreas(String userEmail, Long venueId) {
        Venue venue = venueService.getVenueForRead(userEmail, venueId);
        return parkingAreaRepository.findByVenue(venue).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ParkingAreaResponse createParkingArea(String vendorEmail, Long venueId, CreateParkingAreaRequest request) {
        Venue venue = venueService.getOwnedVenue(vendorEmail, venueId);
        ParkingArea area = new ParkingArea();
        area.setVenue(venue);
        area.setName(request.name());
        area.setFloor(request.floor());
        area.setDescription(request.description());
        return toResponse(parkingAreaRepository.save(area));
    }

    @Transactional
    public ParkingAreaResponse updateParkingArea(String vendorEmail, Long parkingAreaId, UpdateParkingAreaRequest request) {
        ParkingArea area = getOwnedParkingArea(vendorEmail, parkingAreaId);
        area.setName(request.name());
        area.setFloor(request.floor());
        area.setDescription(request.description());
        area.setStatus(request.status());
        return toResponse(parkingAreaRepository.save(area));
    }

    @Transactional
    public void deleteParkingArea(String vendorEmail, Long parkingAreaId) {
        ParkingArea area = getOwnedParkingArea(vendorEmail, parkingAreaId);
        if (area.getTotalSlots() > 0) {
            throw new IllegalStateException("Delete slots before deleting this parking area");
        }
        parkingAreaRepository.delete(area);
    }

    @Transactional(readOnly = true)
    public ParkingArea getOwnedParkingArea(String vendorEmail, Long parkingAreaId) {
        ParkingArea area = parkingAreaRepository.findById(parkingAreaId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking area not found: " + parkingAreaId));
        venueService.getOwnedVenue(vendorEmail, area.getVenue().getId());
        return area;
    }

    @Transactional(readOnly = true)
    public ParkingArea getParkingAreaForRead(String userEmail, Long parkingAreaId) {
        ParkingArea area = parkingAreaRepository.findById(parkingAreaId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking area not found: " + parkingAreaId));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));
        if (user.getRole() == Role.ADMIN) {
            return area;
        }
        venueService.getVenueForRead(userEmail, area.getVenue().getId());
        return area;
    }

    @Transactional(readOnly = true)
    public ParkingAreaResponse toResponse(ParkingArea area) {
        return new ParkingAreaResponse(
                area.getId(),
                area.getVenue().getId(),
                area.getVenue().getName(),
                area.getName(),
                area.getFloor(),
                area.getDescription(),
                area.getStatus(),
                area.getTotalSlots(),
                area.getAvailableSlots(),
                area.getCreatedAt(),
                area.getUpdatedAt()
        );
    }
}
