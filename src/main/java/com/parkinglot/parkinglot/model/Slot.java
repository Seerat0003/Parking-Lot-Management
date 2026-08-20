package com.parkinglot.parkinglot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "slots", indexes = {
        @Index(name = "idx_slot_parking_lot", columnList = "parking_lot_id"),
        @Index(name = "idx_slot_parking_area", columnList = "parking_area_id"),
        @Index(name = "idx_slot_status", columnList = "status")
})
@Getter @Setter @NoArgsConstructor
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Slot number is required")
    private Integer slotNumber;

    @Enumerated(EnumType.STRING)
    private SlotStatus status = SlotStatus.AVAILABLE;
    
    @Enumerated(EnumType.STRING)
    private SlotType type = SlotType.REGULAR;

    @Version
    private Integer version;

    @ManyToOne
    @JoinColumn(name = "parking_lot_id")
    @JsonBackReference
    private ParkingLot parkingLot;

    @ManyToOne
    @JoinColumn(name = "parking_area_id")
    private ParkingArea parkingArea;

    @Column(updatable = false)
    private java.time.LocalDateTime createdAt;

    private java.time.LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }
}
