package com.accenture.ecommerce.store.repository;

import com.accenture.ecommerce.store.domain.OrderItem;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data  repository for the OrderItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long>, JpaSpecificationExecutor<OrderItem> {
    Optional<OrderItem> findOneByIdAndOrderCustomerUserLogin(Long id, String s);
}
