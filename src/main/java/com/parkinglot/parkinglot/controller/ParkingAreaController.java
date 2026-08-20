package com.parkinglot.parkinglot.controller;

import com.parkinglot.parkinglot.dto.parkingarea.CreateParkingAreaRequest;
import com.parkinglot.parkinglot.dto.parkingarea.ParkingAreaResponse;
import com.parkinglot.parkinglot.dto.parkingarea.UpdateParkingAreaRequest;
import com.parkinglot.parkinglot.service.ParkingAreaService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ParkingAreaController {

    private final ParkingAreaService parkingAreaService;

    public ParkingAreaController(ParkingAreaService parkingAreaService) {
        this.parkingAreaService = parkingAreaService;
    }

    @GetMapping("/venues/{venueId}/parking-areas")
    public List<ParkingAreaResponse> getParkingAreas(@PathVariable Long venueId, Authentication authentication) {
        return parkingAreaService.getParkingAreas(authentication.getName(), venueId);
    }

    @PostMapping("/venues/{venueId}/parking-areas")
    public ParkingAreaResponse createParkingArea(@PathVariable Long venueId,
                                                 @Valid @RequestBody CreateParkingAreaRequest request,
                                                 Authentication authentication) {
        return parkingAreaService.createParkingArea(authentication.getName(), venueId, request);
    }

    @PutMapping("/parking-areas/{id}")
    public ParkingAreaResponse updateParkingArea(@PathVariable Long id,
                                                 @Valid @RequestBody UpdateParkingAreaRequest request,
                                                 Authentication authentication) {
        return parkingAreaService.updateParkingArea(authentication.getName(), id, request);
    }

    @DeleteMapping("/parking-areas/{id}")
    public void deleteParkingArea(@PathVariable Long id, Authentication authentication) {
        parkingAreaService.deleteParkingArea(authentication.getName(), id);
    }
}
