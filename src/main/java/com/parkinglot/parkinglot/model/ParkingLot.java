package com.parkinglot.parkinglot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

@Entity
@Table(name = "parking_lots")
@Getter @Setter @NoArgsConstructor
public class ParkingLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    private Integer totalSlots = 0;
    
    private Integer availableSlots = 0;

    @OneToMany(mappedBy = "parkingLot", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Slot> slots;
    
    public Integer getTotalSlots() {
        return totalSlots == null ? 0 : totalSlots;
    }
    
    public Integer getAvailableSlots() {
        return availableSlots == null ? 0 : availableSlots;
    }
}