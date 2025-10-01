package com.supplychain.controller;

import com.supplychain.model.Shipment;
import com.supplychain.model.Delivery;
import com.supplychain.repository.ShipmentRepository;
import com.supplychain.repository.DeliveryRepository;
// import com.supplychain.service.KafkaProducerService; // Comment this out
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/shipments")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class ShipmentController {

    private final ShipmentRepository shipmentRepository;
    private final DeliveryRepository deliveryRepository;
    // private final KafkaProducerService kafkaProducerService; // Comment this out

    @Autowired
    public ShipmentController(ShipmentRepository shipmentRepository, DeliveryRepository deliveryRepository) {
        this.shipmentRepository = shipmentRepository;
        this.deliveryRepository = deliveryRepository;
        // this.kafkaProducerService = kafkaProducerService; // Comment this out
    }

    @GetMapping
    public ResponseEntity<List<Shipment>> getAllShipments() {
        try {
            List<Shipment> shipments = shipmentRepository.findAll();
            return ResponseEntity.ok(shipments);
        } catch (Exception e) {
            System.err.println("Error fetching shipments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shipment> getShipmentById(@PathVariable Long id) {
        try {
            Optional<Shipment> shipment = shipmentRepository.findById(id);
            if (shipment.isPresent()) {
                return ResponseEntity.ok(shipment.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error fetching shipment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Shipment> createShipment(@RequestBody Shipment shipment) {
        try {
            System.out.println("Received shipment: " + shipment);
            Shipment savedShipment = shipmentRepository.save(shipment);
            
            // Kafka event publishing - commented out for now
            // String message = "Shipment created with ID: " + savedShipment.getShipmentId();
            // kafkaProducerService.sendMessage("shipment-events", message);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedShipment);
        } catch (Exception e) {
            System.err.println("Error creating shipment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Shipment> updateShipment(@PathVariable Long id, @RequestBody Shipment shipmentDetails) {
        try {
            Optional<Shipment> existingShipment = shipmentRepository.findById(id);
            if (existingShipment.isPresent()) {
                Shipment shipment = existingShipment.get();
                String oldStatus = shipment.getStatus(); // Store old status
                
                shipment.setOrigin(shipmentDetails.getOrigin());
                shipment.setDestination(shipmentDetails.getDestination());
                shipment.setStatus(shipmentDetails.getStatus());
                shipment.setEstimatedDelivery(shipmentDetails.getEstimatedDelivery());
                
                // Set route and vendor if provided
                if (shipmentDetails.getAssignedRoute() != null) {
                    shipment.setAssignedRoute(shipmentDetails.getAssignedRoute());
                }
                if (shipmentDetails.getAssignedVendor() != null) {
                    shipment.setAssignedVendor(shipmentDetails.getAssignedVendor());
                }
                
                Shipment updatedShipment = shipmentRepository.save(shipment);

                // Automatically create delivery record when shipment status changes to "Delivered"
                if ("Delivered".equals(shipmentDetails.getStatus()) && 
                    !"Delivered".equals(oldStatus)) {
                    
                    // Check if delivery record already exists for this shipment
                    boolean deliveryExists = deliveryRepository.findAll().stream()
                        .anyMatch(d -> d.getShipment() != null && 
                                      d.getShipment().getShipmentId().equals(id));
                    
                    if (!deliveryExists) {
                        Delivery delivery = new Delivery();
                        delivery.setShipment(updatedShipment);
                        delivery.setActualDeliveryDate(updatedShipment.getEstimatedDelivery());
                        delivery.setRecipient("Customer at " + updatedShipment.getDestination());
                        deliveryRepository.save(delivery);
                    }
                }

                // Note: For now, shipment updates work normally
                // Delivery synchronization is handled by the improved DeliveryController.getAllDeliveries() method
                // which filters deliveries to only show those with "Delivered" status

                // Kafka event publishing - commented out for now
                // String message = "Shipment " + id + " updated. New status: " + updatedShipment.getStatus();
                // kafkaProducerService.sendMessage("shipment-events", message);
                
                return ResponseEntity.ok(updatedShipment);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error updating shipment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipment(@PathVariable Long id) {
        try {
            Optional<Shipment> shipment = shipmentRepository.findById(id);
            if (shipment.isPresent()) {
                shipmentRepository.delete(shipment.get());

                // Kafka event publishing - commented out for now
                // String message = "Shipment deleted with ID: " + id;
                // kafkaProducerService.sendMessage("shipment-events", message);

                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error deleting shipment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}