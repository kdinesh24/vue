package com.supplychain.controller;

import com.supplychain.model.Vendor;
import com.supplychain.repository.VendorRepository;
import com.supplychain.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vendors")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class VendorController {

    private final VendorRepository vendorRepository;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public VendorController(VendorRepository vendorRepository, KafkaProducerService kafkaProducerService) {
        this.vendorRepository = vendorRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @GetMapping
    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    @PostMapping
    public Vendor createVendor(@RequestBody Vendor vendor) {
        Vendor savedVendor = vendorRepository.save(vendor);
        String message = "Vendor created: ID=" + savedVendor.getVendorId() + ", Name=" + savedVendor.getName();
        kafkaProducerService.sendMessage("vendor-events", message);
        return savedVendor;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Vendor> getVendorById(@PathVariable Long id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found with id: " + id));
        return ResponseEntity.ok(vendor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vendor> updateVendor(@PathVariable Long id, @RequestBody Vendor vendorDetails) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found with id: " + id));

        vendor.setName(vendorDetails.getName());
        vendor.setContactInfo(vendorDetails.getContactInfo());
        vendor.setServiceType(vendorDetails.getServiceType());

        final Vendor updatedVendor = vendorRepository.save(vendor);
        String message = "Vendor updated: ID=" + id + ", Name=" + updatedVendor.getName();
        kafkaProducerService.sendMessage("vendor-events", message);
        return ResponseEntity.ok(updatedVendor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVendor(@PathVariable Long id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found with id: " + id));
        vendorRepository.delete(vendor);
        String message = "Vendor deleted: ID=" + id;
        kafkaProducerService.sendMessage("vendor-events", message);
        return ResponseEntity.noContent().build();
    }
}