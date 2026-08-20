package com.parkinglot.parkinglot.service;

import com.parkinglot.parkinglot.dto.slot.CreateSlotRequest;
import com.parkinglot.parkinglot.dto.slot.SlotResponse;
import com.parkinglot.parkinglot.dto.slot.UpdateSlotRequest;
import com.parkinglot.parkinglot.exception.ResourceNotFoundException;
import com.parkinglot.parkinglot.model.ParkingArea;
import com.parkinglot.parkinglot.model.Slot;
import com.parkinglot.parkinglot.model.SlotStatus;
import com.parkinglot.parkinglot.model.SlotType;
import com.parkinglot.parkinglot.repository.ParkingAreaRepository;
import com.parkinglot.parkinglot.repository.SlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SlotService {

    private final SlotRepository slotRepository;
    private final ParkingAreaRepository parkingAreaRepository;
    private final ParkingAreaService parkingAreaService;

    public SlotService(SlotRepository slotRepository,
                       ParkingAreaRepository parkingAreaRepository,
                       ParkingAreaService parkingAreaService) {
        this.slotRepository = slotRepository;
        this.parkingAreaRepository = parkingAreaRepository;
        this.parkingAreaService = parkingAreaService;
    }

    @Transactional(readOnly = true)
    public List<SlotResponse> getSlotsByArea(String userEmail, Long areaId) {
        parkingAreaService.getParkingAreaForRead(userEmail, areaId);
        return slotRepository.findByParkingAreaId(areaId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SlotResponse> getAvailableSlotsByArea(String userEmail, Long areaId) {
        parkingAreaService.getParkingAreaForRead(userEmail, areaId);
        return slotRepository.findByParkingAreaIdAndStatus(areaId, SlotStatus.AVAILABLE).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public SlotResponse createSlot(String vendorEmail, Long areaId, CreateSlotRequest request) {
        ParkingArea area = parkingAreaService.getOwnedParkingArea(vendorEmail, areaId);
        Slot slot = new Slot();
        slot.setSlotNumber(request.slotNumber());
        slot.setType(request.type() == null ? SlotType.REGULAR : request.type());
        slot.setStatus(request.status() == null ? SlotStatus.AVAILABLE : request.status());
        slot.setParkingArea(area);
        Slot savedSlot = slotRepository.save(slot);
        incrementCounts(area, savedSlot.getStatus());
        parkingAreaRepository.save(area);
        return toResponse(savedSlot);
    }

    @Transactional
    public SlotResponse updateSlot(String vendorEmail, Long slotId, UpdateSlotRequest request) {
        Slot slot = getOwnedSlot(vendorEmail, slotId);
        SlotStatus previousStatus = slot.getStatus();
        slot.setSlotNumber(request.slotNumber());
        slot.setType(request.type());
        slot.setStatus(request.status());
        updateAvailabilityCount(slot.getParkingArea(), previousStatus, request.status());
        parkingAreaRepository.save(slot.getParkingArea());
        return toResponse(slotRepository.save(slot));
    }

    @Transactional
    public void deleteSlot(String vendorEmail, Long slotId) {
        Slot slot = getOwnedSlot(vendorEmail, slotId);
        if (slot.getStatus() == SlotStatus.RESERVED || slot.getStatus() == SlotStatus.OCCUPIED) {
            throw new IllegalStateException("Reserved or occupied slots cannot be deleted");
        }

        ParkingArea area = slot.getParkingArea();
        decrementCounts(area, slot.getStatus());
        parkingAreaRepository.save(area);
        slotRepository.delete(slot);
    }

    @Transactional(readOnly = true)
    public Slot getOwnedSlot(String vendorEmail, Long slotId) {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found: " + slotId));
        if (slot.getParkingArea() == null) {
            throw new IllegalStateException("Legacy slots cannot be managed through vendor slot APIs");
        }
        parkingAreaService.getOwnedParkingArea(vendorEmail, slot.getParkingArea().getId());
        return slot;
    }

    @Transactional(readOnly = true)
    public SlotResponse toResponse(Slot slot) {
        Long parkingAreaId = slot.getParkingArea() == null ? null : slot.getParkingArea().getId();
        String parkingAreaName = slot.getParkingArea() == null ? null : slot.getParkingArea().getName();
        Long venueId = slot.getParkingArea() == null ? null : slot.getParkingArea().getVenue().getId();
        String venueName = slot.getParkingArea() == null ? null : slot.getParkingArea().getVenue().getName();

        return new SlotResponse(
                slot.getId(),
                slot.getSlotNumber(),
                slot.getStatus(),
                slot.getType(),
                parkingAreaId,
                parkingAreaName,
                venueId,
                venueName,
                slot.getParkingLot() == null ? null : slot.getParkingLot().getId(),
                slot.getCreatedAt(),
                slot.getUpdatedAt()
        );
    }

    private void incrementCounts(ParkingArea area, SlotStatus status) {
        area.setTotalSlots(area.getTotalSlots() + 1);
        if (status == SlotStatus.AVAILABLE) {
            area.setAvailableSlots(area.getAvailableSlots() + 1);
        }
    }

    private void decrementCounts(ParkingArea area, SlotStatus status) {
        area.setTotalSlots(Math.max(0, area.getTotalSlots() - 1));
        if (status == SlotStatus.AVAILABLE) {
            area.setAvailableSlots(Math.max(0, area.getAvailableSlots() - 1));
        }
    }

    private void updateAvailabilityCount(ParkingArea area, SlotStatus previousStatus, SlotStatus nextStatus) {
        boolean wasAvailable = previousStatus == SlotStatus.AVAILABLE;
        boolean isAvailable = nextStatus == SlotStatus.AVAILABLE;
        if (wasAvailable && !isAvailable) {
            area.setAvailableSlots(Math.max(0, area.getAvailableSlots() - 1));
        } else if (!wasAvailable && isAvailable) {
            area.setAvailableSlots(area.getAvailableSlots() + 1);
        }
    }
}
