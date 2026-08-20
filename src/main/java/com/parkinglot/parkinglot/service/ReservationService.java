package com.parkinglot.parkinglot.service;

import com.parkinglot.parkinglot.exception.ResourceNotFoundException;
import com.parkinglot.parkinglot.exception.VenueNotPublishedException;
import com.parkinglot.parkinglot.model.*;
import com.parkinglot.parkinglot.repository.ReservationRepository;
import com.parkinglot.parkinglot.repository.SlotRepository;
import com.parkinglot.parkinglot.repository.ParkingLotRepository;
import com.parkinglot.parkinglot.repository.ParkingAreaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.parkinglot.parkinglot.exception.SlotNotAvailableException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SlotRepository slotRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final ParkingAreaRepository parkingAreaRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              SlotRepository slotRepository,
                              ParkingLotRepository parkingLotRepository,
                              ParkingAreaRepository parkingAreaRepository) {
        this.reservationRepository = reservationRepository;
        this.slotRepository = slotRepository;
        this.parkingLotRepository = parkingLotRepository;
        this.parkingAreaRepository = parkingAreaRepository;
    }

    @Transactional
    public Reservation reserveSlot(User user, Long slotId) {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found: " + slotId));

        if (slot.getParkingArea() != null && slot.getParkingArea().getVenue().getStatus() != VenueStatus.PUBLISHED) {
            throw new VenueNotPublishedException("Venue is not published for slot: " + slotId);
        }

        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new SlotNotAvailableException("Slot " + slotId + " is not available");
        }

        slot.setStatus(SlotStatus.RESERVED);
        if (slot.getParkingArea() != null) {
            ParkingArea area = slot.getParkingArea();
            area.setAvailableSlots(Math.max(0, area.getAvailableSlots() - 1));
            parkingAreaRepository.save(area);
        }
        if (slot.getParkingLot() != null) {
            slot.getParkingLot().setAvailableSlots(slot.getParkingLot().getAvailableSlots() - 1);
            parkingLotRepository.save(slot.getParkingLot());
        }
        slotRepository.save(slot);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setSlot(slot);
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setReservationTime(LocalDateTime.now());
        reservation.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        //reservation.setExpiryTime(LocalDateTime.now().plusSeconds(20));

        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation cancelReservation(Long reservationId, String userEmail) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + reservationId));

        if (!reservation.getUser().getEmail().equals(userEmail)) {
            throw new ResourceNotFoundException("Reservation not found: " + reservationId);
        }

        if (reservation.getStatus() != ReservationStatus.ACTIVE && reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Reservation cannot be cancelled in its current state");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        Slot slot = reservation.getSlot();
        slot.setStatus(SlotStatus.AVAILABLE);
        if (slot.getParkingArea() != null) {
            ParkingArea area = slot.getParkingArea();
            area.setAvailableSlots(area.getAvailableSlots() + 1);
            parkingAreaRepository.save(area);
        }
        if (slot.getParkingLot() != null) {
            slot.getParkingLot().setAvailableSlots(slot.getParkingLot().getAvailableSlots() + 1);
            parkingLotRepository.save(slot.getParkingLot());
        }
        slotRepository.save(slot);

        return reservation;
    }

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void expireOldReservations() {
        List<Reservation> activeReservations = reservationRepository.findByStatusAndExpiryTimeBefore(
                ReservationStatus.ACTIVE,
                LocalDateTime.now()
        );

        for (Reservation reservation : activeReservations) {
            reservation.setStatus(ReservationStatus.EXPIRED);
            reservationRepository.save(reservation);

            Slot slot = reservation.getSlot();
            slot.setStatus(SlotStatus.AVAILABLE);
            if (slot.getParkingArea() != null) {
                ParkingArea area = slot.getParkingArea();
                area.setAvailableSlots(area.getAvailableSlots() + 1);
                parkingAreaRepository.save(area);
            }
            if (slot.getParkingLot() != null) {
                slot.getParkingLot().setAvailableSlots(slot.getParkingLot().getAvailableSlots() + 1);
                parkingLotRepository.save(slot.getParkingLot());
            }
            slotRepository.save(slot);
        }
    }

    @Transactional(readOnly = true)
    public Reservation getReservationForUser(Long reservationId, String userEmail) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + reservationId));
        if (!reservation.getUser().getEmail().equals(userEmail)) {
            throw new ResourceNotFoundException("Reservation not found: " + reservationId);
        }
        return reservation;
    }
}
