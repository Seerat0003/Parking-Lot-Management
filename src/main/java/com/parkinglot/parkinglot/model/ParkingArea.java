package com.parkinglot.parkinglot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parking_areas", indexes = {
        @Index(name = "idx_parking_area_venue", columnList = "venue_id")
})
@Getter
@Setter
@NoArgsConstructor
public class ParkingArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Parking area name is required")
    private String name;

    private String floor;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParkingAreaStatus status = ParkingAreaStatus.ACTIVE;

    @Column(nullable = false)
    private Integer totalSlots = 0;

    @Column(nullable = false)
    private Integer availableSlots = 0;

    @ManyToOne(optional = false)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @OneToMany(mappedBy = "parkingArea", cascade = CascadeType.ALL)
    private List<Slot> slots = new ArrayList<>();

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
