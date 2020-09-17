package com.accenture.ecommerce.store.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import com.accenture.ecommerce.store.security.AuthoritiesConstants;
import com.accenture.ecommerce.store.security.SecurityUtils;
import io.github.jhipster.service.filter.LongFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.accenture.ecommerce.store.domain.Invoice;
import com.accenture.ecommerce.store.domain.*; // for static metamodels
import com.accenture.ecommerce.store.repository.InvoiceRepository;
import com.accenture.ecommerce.store.service.dto.InvoiceCriteria;

/**
 * Service for executing complex queries for {@link Invoice} entities in the database.
 * The main input is a {@link InvoiceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Invoice} or a {@link Page} of {@link Invoice} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class InvoiceQueryService extends QueryService<Invoice> {

    private final Logger log = LoggerFactory.getLogger(InvoiceQueryService.class);

    private final InvoiceRepository invoiceRepository;
    private final CustomerService customerService;

    public InvoiceQueryService(InvoiceRepository invoiceRepository, CustomerService customerService) {
        this.invoiceRepository = invoiceRepository;
        this.customerService = customerService;
    }

    /**
     * Return a {@link List} of {@link Invoice} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Invoice> findByCriteria(InvoiceCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Invoice> specification = createSpecification(criteria);
        return invoiceRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Invoice} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Invoice> findByCriteria(InvoiceCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Invoice> specification;
        if (SecurityUtils.isCurrentUserInRole (AuthoritiesConstants.ADMIN)) {
            specification = createSpecification(criteria);
        }
        else {
            LongFilter longFilter = new LongFilter();
            longFilter.setEquals(customerService.getCurrentCustomerLogin().get().getId());
            criteria.setCustomerId(longFilter);
            specification = createSpecification(criteria);
        }
        return invoiceRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(InvoiceCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Invoice> specification = createSpecification(criteria);
        return invoiceRepository.count(specification);
    }

    /**
     * Function to convert {@link InvoiceCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Invoice> createSpecification(InvoiceCriteria criteria) {
        Specification<Invoice> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Invoice_.id));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), Invoice_.code));
            }
            if (criteria.getDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate(), Invoice_.date));
            }
            if (criteria.getDetails() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDetails(), Invoice_.details));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Invoice_.status));
            }
            if (criteria.getPaymentMethod() != null) {
                specification = specification.and(buildSpecification(criteria.getPaymentMethod(), Invoice_.paymentMethod));
            }
            if (criteria.getPaymentDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPaymentDate(), Invoice_.paymentDate));
            }
            if (criteria.getPaymentAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPaymentAmount(), Invoice_.paymentAmount));
            }
            if (criteria.getShipmentId() != null) {
                specification = specification.and(buildSpecification(criteria.getShipmentId(),
                    root -> root.join(Invoice_.shipments, JoinType.LEFT).get(Shipment_.id)));
            }
            if (criteria.getOrderId() != null) {
                specification = specification.and(buildSpecification(criteria.getOrderId(),
                    root -> root.join(Invoice_.order, JoinType.LEFT).get(ProductOrder_.id)));
            }
            if (criteria.getCustomerId() != null) {
                specification = specification.and(buildSpecification(criteria.getCustomerId(),
                    root -> root.join(Invoice_.order, JoinType.LEFT).join(ProductOrder_.customer, JoinType.LEFT).get(Customer_.id)));
            }
        }
        return specification;
    }
}
