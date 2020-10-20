package org.jhipster.ecommerce.store.service;

import org.jhipster.ecommerce.store.domain.Customer;
import org.jhipster.ecommerce.store.repository.CustomerRepository;
import org.jhipster.ecommerce.store.security.AuthoritiesConstants;
import org.jhipster.ecommerce.store.security.SecurityUtils;
import io.github.jhipster.service.filter.LongFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Customer}.
 */
@Service
@Transactional
public class CustomerService {

    private final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Save a customer.
     *
     * @param customer the entity to save.
     * @return the persisted entity.
     */
    public Customer save(Customer customer) {
        log.debug("Request to save Customer : {}", customer);
        return customerRepository.save(customer);
    }

    /**
     * Get all the customers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Customer> findAll(Pageable pageable) {
        log.debug("Request to get all Customers");
        if (SecurityUtils.isCurrentUserInRole (AuthoritiesConstants.ADMIN)) {
            return customerRepository.findAll(pageable);
        } else
            return customerRepository.findAllByUserLogin(SecurityUtils.getCurrentUserLogin().get(),
                pageable);
    }


    /**
     * Get one customer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Customer> findOne(Long id) {
        log.debug("Request to get Customer : {}", id);
        return customerRepository.findById(id);
    }

    /**
     * Get current customer login.
     *
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Customer> getCurrentCustomerLogin() {
        log.debug("Request to get current Customer login");
        LongFilter longFilter = new LongFilter();
        Page<Customer> customers = customerRepository.findAllByUserLogin(
            SecurityUtils.getCurrentUserLogin().get(),
            Pageable.unpaged());
        return customerRepository.findById(customers.iterator().next().getId());
    }

    /**
     * Delete the customer by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Customer : {}", id);
        customerRepository.deleteById(id);
    }
}
