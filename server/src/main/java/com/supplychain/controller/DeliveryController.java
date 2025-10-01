package com.supplychain.controller;

import com.supplychain.model.Delivery;
import com.supplychain.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/deliveries")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class DeliveryController {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @GetMapping
    public List<Delivery> getAllDeliveries() {
        // Only return deliveries where the associated shipment is actually "Delivered"
        return deliveryRepository.findAll().stream()
                .filter(delivery -> delivery.getShipment() != null && 
                        "Delivered".equals(delivery.getShipment().getStatus()))
                .collect(Collectors.toList());
    }

    @GetMapping("/cleanup")
    public ResponseEntity<String> cleanupInconsistentDeliveries() {
        // Find and remove deliveries where shipment status is not "Delivered"
        List<Delivery> inconsistentDeliveries = deliveryRepository.findAll().stream()
                .filter(delivery -> delivery.getShipment() != null && 
                        !"Delivered".equals(delivery.getShipment().getStatus()))
                .collect(Collectors.toList());
        
        int deletedCount = inconsistentDeliveries.size();
        deliveryRepository.deleteAll(inconsistentDeliveries);
        
        return ResponseEntity.ok("Cleaned up " + deletedCount + " inconsistent delivery records");
    }

    @PostMapping
    public Delivery createDelivery(@RequestBody Delivery delivery) {
        return deliveryRepository.save(delivery);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Delivery> getDeliveryById(@PathVariable Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + id));
        return ResponseEntity.ok(delivery);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Delivery> updateDelivery(@PathVariable Long id, @RequestBody Delivery deliveryDetails) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + id));

        delivery.setShipment(deliveryDetails.getShipment());
        delivery.setActualDeliveryDate(deliveryDetails.getActualDeliveryDate());
        delivery.setRecipient(deliveryDetails.getRecipient());

        final Delivery updatedDelivery = deliveryRepository.save(delivery);
        return ResponseEntity.ok(updatedDelivery);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDelivery(@PathVariable Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + id));
        deliveryRepository.delete(delivery);
        return ResponseEntity.noContent().build();
    }
}