package com.parkinglot.parkinglot.controller;

import com.parkinglot.parkinglot.dto.admin.AdminVenueResponse;
import com.parkinglot.parkinglot.dto.admin.VendorResponse;
import com.parkinglot.parkinglot.service.AdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/vendors")
    public List<VendorResponse> getVendors() {
        return adminService.getAllVendors();
    }

    @GetMapping("/venues")
    public List<AdminVenueResponse> getVenues() {
        return adminService.getAllVenues();
    }

    @PostMapping("/venues/{id}/suspend")
    public AdminVenueResponse suspendVenue(@PathVariable Long id) {
        return adminService.suspendVenue(id);
    }

    @PostMapping("/venues/{id}/restore")
    public AdminVenueResponse restoreVenue(@PathVariable Long id) {
        return adminService.restoreVenue(id);
    }

    @PostMapping("/vendors/{id}/suspend")
    public VendorResponse suspendVendor(@PathVariable Long id) {
        return adminService.suspendVendor(id);
    }

    @PostMapping("/vendors/{id}/restore")
    public VendorResponse restoreVendor(@PathVariable Long id) {
        return adminService.restoreVendor(id);
    }
}
