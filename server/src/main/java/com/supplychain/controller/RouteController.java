package com.supplychain.controller;

import com.supplychain.model.Route;
import com.supplychain.repository.RouteRepository;
import com.supplychain.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/routes")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class RouteController {

    private final RouteRepository routeRepository;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public RouteController(RouteRepository routeRepository, KafkaProducerService kafkaProducerService) {
        this.routeRepository = routeRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @GetMapping
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    @PostMapping
    public Route createRoute(@RequestBody Route route) {
        Route savedRoute = routeRepository.save(route);
        String message = "Route created: ID=" + savedRoute.getRouteId() + ", From=" + savedRoute.getOriginPort() + " to " + savedRoute.getDestinationPort();
        kafkaProducerService.sendMessage("route-events", message);
        return savedRoute;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found with id: " + id));
        return ResponseEntity.ok(route);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Route> updateRoute(@PathVariable Long id, @RequestBody Route routeDetails) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found with id: " + id));

        route.setOriginPort(routeDetails.getOriginPort());
        route.setDestinationPort(routeDetails.getDestinationPort());
        route.setDuration(routeDetails.getDuration());
        route.setStatus(routeDetails.getStatus());

        final Route updatedRoute = routeRepository.save(route);
        String message = "Route updated: ID=" + id + ", Status=" + updatedRoute.getStatus();
        kafkaProducerService.sendMessage("route-events", message);
        return ResponseEntity.ok(updatedRoute);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found with id: " + id));
        routeRepository.delete(route);
        String message = "Route deleted: ID=" + id;
        kafkaProducerService.sendMessage("route-events", message);
        return ResponseEntity.noContent().build();
    }
}