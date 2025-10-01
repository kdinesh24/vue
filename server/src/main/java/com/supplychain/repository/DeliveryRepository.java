package com.supplychain.repository;

import com.supplychain.model.Delivery;
import com.supplychain.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    boolean existsByShipment(Shipment shipment);
    Optional<Delivery> findByShipment(Shipment shipment);
}