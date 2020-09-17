package com.accenture.ecommerce.store.repository;

import com.accenture.ecommerce.store.domain.ProductOrder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data  repository for the ProductOrder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long>, JpaSpecificationExecutor<ProductOrder> {
    Page<ProductOrder> findAllByCustomerUserLogin(String s, Pageable page);

    Optional<ProductOrder> findOneByIdAndCustomerUserLogin(Long id, String s);
}
