package com.parkinglot.parkinglot.controller;

import com.parkinglot.parkinglot.dto.slot.CreateSlotRequest;
import com.parkinglot.parkinglot.dto.slot.SlotResponse;
import com.parkinglot.parkinglot.dto.slot.UpdateSlotRequest;
import com.parkinglot.parkinglot.model.Slot;
import com.parkinglot.parkinglot.model.SlotStatus;
import com.parkinglot.parkinglot.model.ParkingLot;
import com.parkinglot.parkinglot.repository.SlotRepository;
import com.parkinglot.parkinglot.repository.ParkingLotRepository;
import com.parkinglot.parkinglot.exception.ResourceNotFoundException;
import com.parkinglot.parkinglot.service.SlotService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SlotController {

    private final SlotRepository slotRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final SlotService slotService;

    public SlotController(SlotRepository slotRepository, ParkingLotRepository parkingLotRepository, SlotService slotService) {
        this.slotRepository = slotRepository;
        this.parkingLotRepository = parkingLotRepository;
        this.slotService = slotService;
    }

    @GetMapping("/slots")
    public List<Slot> getSlots(@RequestParam(required = false) Long parkingLotId) {
        if (parkingLotId != null) {
            return slotRepository.findByParkingLotId(parkingLotId);
        }
        return slotRepository.findAll();
    }

    @PostMapping("/slots")
    public Slot createSlot(@Valid @RequestBody Slot slot) {
        if (slot.getParkingLot() != null && slot.getParkingLot().getId() != null) {
            ParkingLot parkingLot = parkingLotRepository.findById(slot.getParkingLot().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("ParkingLot not found: " + slot.getParkingLot().getId()));

            parkingLot.setTotalSlots(parkingLot.getTotalSlots() + 1);
            if (slot.getStatus() == null || slot.getStatus() == SlotStatus.AVAILABLE) {
                parkingLot.setAvailableSlots(parkingLot.getAvailableSlots() + 1);
            }
            parkingLotRepository.save(parkingLot);
            slot.setParkingLot(parkingLot);
        }
        return slotRepository.save(slot);
    }

    @GetMapping("/slots/available")
    public List<Slot> getAvailableSlots(@RequestParam(required = false) Long parkingLotId) {
        if (parkingLotId != null) {
            return slotRepository.findByParkingLotIdAndStatus(parkingLotId, SlotStatus.AVAILABLE);
        }
        return slotRepository.findAll().stream()
                .filter(s -> s.getStatus() == SlotStatus.AVAILABLE)
                .toList();
    }

    @GetMapping("/parking-areas/{areaId}/slots")
    public List<SlotResponse> getSlotsByArea(@PathVariable Long areaId, Authentication authentication) {
        return slotService.getSlotsByArea(authentication.getName(), areaId);
    }

    @GetMapping("/parking-areas/{areaId}/slots/available")
    public List<SlotResponse> getAvailableSlotsByArea(@PathVariable Long areaId, Authentication authentication) {
        return slotService.getAvailableSlotsByArea(authentication.getName(), areaId);
    }

    @PostMapping("/parking-areas/{areaId}/slots")
    public SlotResponse createSlotForArea(@PathVariable Long areaId,
                                          @Valid @RequestBody CreateSlotRequest request,
                                          Authentication authentication) {
        return slotService.createSlot(authentication.getName(), areaId, request);
    }

    @PutMapping("/slots/{id}")
    public SlotResponse updateSlot(@PathVariable Long id,
                                   @Valid @RequestBody UpdateSlotRequest request,
                                   Authentication authentication) {
        return slotService.updateSlot(authentication.getName(), id, request);
    }

    @DeleteMapping("/slots/{id}")
    public void deleteSlot(@PathVariable Long id, Authentication authentication) {
        slotService.deleteSlot(authentication.getName(), id);
    }
}
