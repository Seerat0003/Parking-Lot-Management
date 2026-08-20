package com.parkinglot.parkinglot.controller;

import com.parkinglot.parkinglot.model.ParkingLot;
import com.parkinglot.parkinglot.repository.ParkingLotRepository;
import com.parkinglot.parkinglot.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parking-lots")
public class ParkingLotController {

    private final ParkingLotRepository parkingLotRepository;

    public ParkingLotController(ParkingLotRepository parkingLotRepository) {
        this.parkingLotRepository = parkingLotRepository;
    }

    @GetMapping
    public Page<ParkingLot> getAllParkingLots(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return parkingLotRepository.findAll(PageRequest.of(page, size));
    }
    
    @GetMapping("/{id}")
    public ParkingLot getParkingLotById(@PathVariable Long id) {
        return parkingLotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ParkingLot not found: " + id));
    }

    @PostMapping
    public ParkingLot createParkingLot(@Valid @RequestBody ParkingLot parkingLot) {
        return parkingLotRepository.save(parkingLot);
    }
}