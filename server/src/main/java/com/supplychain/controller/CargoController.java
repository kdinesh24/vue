package com.supplychain.controller;

import com.supplychain.dto.CargoCreateRequest;
import com.supplychain.model.Cargo;
import com.supplychain.model.Shipment;
import com.supplychain.repository.CargoRepository;
import com.supplychain.repository.ShipmentRepository;
import com.supplychain.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cargo")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}) // Allow frontend access
public class CargoController {

    private final CargoRepository cargoRepository;
    private final ShipmentRepository shipmentRepository;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public CargoController(CargoRepository cargoRepository, ShipmentRepository shipmentRepository, KafkaProducerService kafkaProducerService) {
        this.cargoRepository = cargoRepository;
        this.shipmentRepository = shipmentRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @GetMapping
    public List<Cargo> getAllCargo() {
        return cargoRepository.findAll();
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createCargo(@RequestBody CargoCreateRequest request) {
        try {
            System.out.println("Received cargo request: " + request);
            
            // Create new Cargo entity
            Cargo cargo = new Cargo();
            cargo.setType(request.getType());
            cargo.setWeight(request.getWeight());
            cargo.setValue(request.getValue());
            cargo.setVolume(request.getVolume());
            cargo.setWeightUnit(request.getWeightUnit());
            cargo.setDescription(request.getDescription());
            
            // Handle shipment relationship if provided
            if (request.getShipmentId() != null) {
                Shipment shipment = shipmentRepository.findById(request.getShipmentId())
                    .orElseThrow(() -> new RuntimeException("Shipment not found with id: " + request.getShipmentId()));
                cargo.setShipment(shipment);
            }
            
            Cargo savedCargo = cargoRepository.save(cargo);
            String message = "Cargo created: ID=" + savedCargo.getCargoId() + 
                           ", Type=" + savedCargo.getType() + 
                           ", Weight=" + savedCargo.getWeight() + "kg" + 
                           ", Value=$" + savedCargo.getValue();
            kafkaProducerService.sendMessage("cargo-events", message);
            return ResponseEntity.ok(savedCargo);
        } catch (Exception e) {
            System.err.println("Error creating cargo: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error creating cargo: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cargo> getCargoById(@PathVariable Long id) {
        Cargo cargo = cargoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cargo not found with id: " + id));
        return ResponseEntity.ok(cargo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cargo> updateCargo(@PathVariable Long id, @RequestBody Cargo cargoDetails) {
        Cargo cargo = cargoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cargo not found with id: " + id));

        cargo.setShipment(cargoDetails.getShipment());
        cargo.setType(cargoDetails.getType());
        cargo.setDescription(cargoDetails.getDescription());
        cargo.setValue(cargoDetails.getValue());

        final Cargo updatedCargo = cargoRepository.save(cargo);
        String message = "Cargo updated: ID=" + id + ", Type=" + updatedCargo.getType();
        kafkaProducerService.sendMessage("cargo-events", message);
        return ResponseEntity.ok(updatedCargo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCargo(@PathVariable Long id) {
        Cargo cargo = cargoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cargo not found with id: " + id));
        cargoRepository.delete(cargo);
        String message = "Cargo deleted: ID=" + id;
        kafkaProducerService.sendMessage("cargo-events", message);
        return ResponseEntity.noContent().build();
    }
}