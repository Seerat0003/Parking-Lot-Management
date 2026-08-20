package com.parkinglot.parkinglot.repository;

import com.parkinglot.parkinglot.model.Slot;
import com.parkinglot.parkinglot.model.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    List<Slot> findByParkingLotId(Long parkingLotId);
    List<Slot> findByParkingLotIdAndStatus(Long parkingLotId, SlotStatus status);
    List<Slot> findByParkingAreaId(Long areaId);
    List<Slot> findByParkingAreaIdAndStatus(Long areaId, SlotStatus status);
}
