package org.jhipster.ecommerce.store.repository;

import org.jhipster.ecommerce.store.domain.Shipment;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data  repository for the Shipment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long>, JpaSpecificationExecutor<Shipment> {
    Optional<Shipment> findOneByIdAndInvoiceOrderCustomerUserLogin(Long id, String s);
}
