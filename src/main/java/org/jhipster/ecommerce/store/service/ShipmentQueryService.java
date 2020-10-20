package org.jhipster.ecommerce.store.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.jhipster.ecommerce.store.security.AuthoritiesConstants;
import org.jhipster.ecommerce.store.security.SecurityUtils;
import io.github.jhipster.service.filter.LongFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import org.jhipster.ecommerce.store.domain.Shipment;
import org.jhipster.ecommerce.store.domain.*; // for static metamodels
import org.jhipster.ecommerce.store.repository.ShipmentRepository;
import org.jhipster.ecommerce.store.service.dto.ShipmentCriteria;

/**
 * Service for executing complex queries for {@link Shipment} entities in the database.
 * The main input is a {@link ShipmentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Shipment} or a {@link Page} of {@link Shipment} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ShipmentQueryService extends QueryService<Shipment> {

    private final Logger log = LoggerFactory.getLogger(ShipmentQueryService.class);

    private final ShipmentRepository shipmentRepository;
    private final CustomerService customerService;

    public ShipmentQueryService(ShipmentRepository shipmentRepository, CustomerService customerService) {
        this.shipmentRepository = shipmentRepository;
        this.customerService = customerService;
    }

    /**
     * Return a {@link List} of {@link Shipment} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Shipment> findByCriteria(ShipmentCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Shipment> specification = createSpecification(criteria);
        return shipmentRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Shipment} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Shipment> findByCriteria(ShipmentCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Shipment> specification;
        if (SecurityUtils.isCurrentUserInRole (AuthoritiesConstants.ADMIN)) {
            specification = createSpecification(criteria);
        }
        else {
            LongFilter longFilter = new LongFilter();
            longFilter.setEquals(customerService.getCurrentCustomerLogin().get().getId());
            criteria.setCustomerId(longFilter);
            specification = createSpecification(criteria);
        }
        return shipmentRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ShipmentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Shipment> specification = createSpecification(criteria);
        return shipmentRepository.count(specification);
    }

    /**
     * Function to convert {@link ShipmentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Shipment> createSpecification(ShipmentCriteria criteria) {
        Specification<Shipment> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Shipment_.id));
            }
            if (criteria.getTrackingCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTrackingCode(), Shipment_.trackingCode));
            }
            if (criteria.getDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate(), Shipment_.date));
            }
            if (criteria.getDetails() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDetails(), Shipment_.details));
            }
            if (criteria.getInvoiceId() != null) {
                specification = specification.and(buildSpecification(criteria.getInvoiceId(),
                    root -> root.join(Shipment_.invoice, JoinType.LEFT).get(Invoice_.id)));
            }
            if (criteria.getCustomerId() != null) {
                specification = specification.and(buildSpecification(criteria.getCustomerId(),
                    root -> root.join(Shipment_.invoice, JoinType.LEFT).join(Invoice_.order, JoinType.LEFT).join(ProductOrder_.customer, JoinType.LEFT).get(Customer_.id)));
            }
        }
        return specification;
    }
}
