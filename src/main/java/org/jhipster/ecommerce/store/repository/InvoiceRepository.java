package org.jhipster.ecommerce.store.repository;

import org.jhipster.ecommerce.store.domain.Invoice;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data  repository for the Invoice entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {
    Optional<Invoice> findOneByIdAndOrderCustomerUserLogin(Long id, String s);
}
