package com.supplychain.controller;

import com.supplychain.model.Cargo;
import com.supplychain.repository.CargoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cargo")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}) // Allow frontend access
public class CargoController {

    @Autowired
    private CargoRepository cargoRepository;

    @GetMapping
    public List<Cargo> getAllCargo() {
        return cargoRepository.findAll();
    }

    @PostMapping
    public Cargo createCargo(@RequestBody Cargo cargo) {
        return cargoRepository.save(cargo);
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
        return ResponseEntity.ok(updatedCargo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCargo(@PathVariable Long id) {
        Cargo cargo = cargoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cargo not found with id: " + id));
        cargoRepository.delete(cargo);
        return ResponseEntity.noContent().build();
    }
}