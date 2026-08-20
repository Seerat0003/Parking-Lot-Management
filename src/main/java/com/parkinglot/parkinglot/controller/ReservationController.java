package com.parkinglot.parkinglot.controller;

import com.parkinglot.parkinglot.dto.reservation.CreateReservationRequest;
import com.parkinglot.parkinglot.dto.reservation.ReservationResponse;
import com.parkinglot.parkinglot.model.Reservation;
import com.parkinglot.parkinglot.model.User;
import com.parkinglot.parkinglot.repository.ReservationRepository;
import com.parkinglot.parkinglot.repository.UserRepository;
import com.parkinglot.parkinglot.service.ReservationService;
import com.parkinglot.parkinglot.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    public ReservationController(ReservationService reservationService, UserRepository userRepository, ReservationRepository reservationRepository) {
        this.reservationService = reservationService;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
    }

    @GetMapping
    public List<ReservationResponse> getMyReservations(Authentication authentication) {
        return getMyReservationsAtAlias(authentication);
    }

    @GetMapping("/my")
    public List<ReservationResponse> getMyReservationsAtAlias(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + authentication.getName()));
        return reservationRepository.findByUser(user).stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    public ReservationResponse createReservation(@RequestBody(required = false) @Valid CreateReservationRequest request,
                                                 @RequestParam(required = false) Long slotId,
                                                 Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        Long resolvedSlotId = request != null ? request.slotId() : slotId;
        if (resolvedSlotId == null) {
            throw new IllegalStateException("Slot id is required");
        }
        return toResponse(reservationService.reserveSlot(user, resolvedSlotId));
    }

    @GetMapping("/{id}")
    public ReservationResponse getReservation(@PathVariable Long id, Authentication authentication) {
        return toResponse(reservationService.getReservationForUser(id, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ReservationResponse cancelReservation(@PathVariable Long id, Authentication authentication) {
        return toResponse(reservationService.cancelReservation(id, authentication.getName()));
    }

    private ReservationResponse toResponse(Reservation reservation) {
        Long parkingAreaId = reservation.getSlot().getParkingArea() == null ? null : reservation.getSlot().getParkingArea().getId();
        String parkingAreaName = reservation.getSlot().getParkingArea() == null ? null : reservation.getSlot().getParkingArea().getName();
        Long venueId = reservation.getSlot().getParkingArea() == null ? null : reservation.getSlot().getParkingArea().getVenue().getId();
        String venueName = reservation.getSlot().getParkingArea() == null ? null : reservation.getSlot().getParkingArea().getVenue().getName();

        return new ReservationResponse(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getSlot().getId(),
                reservation.getSlot().getSlotNumber(),
                reservation.getSlot().getType(),
                parkingAreaId,
                parkingAreaName,
                venueId,
                venueName,
                reservation.getStatus(),
                reservation.getReservationTime(),
                reservation.getExpiryTime()
        );
    }
}
