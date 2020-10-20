package org.jhipster.ecommerce.store.web.rest;

import org.jhipster.ecommerce.store.StoreApp;
import org.jhipster.ecommerce.store.domain.Shipment;
import org.jhipster.ecommerce.store.domain.Invoice;
import org.jhipster.ecommerce.store.repository.ShipmentRepository;
import org.jhipster.ecommerce.store.service.ShipmentService;
import org.jhipster.ecommerce.store.service.dto.ShipmentCriteria;
import org.jhipster.ecommerce.store.service.ShipmentQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ShipmentResource} REST controller.
 */
@SpringBootTest(classes = StoreApp.class)
@AutoConfigureMockMvc
@WithMockUser(username="admin", authorities={"ROLE_ADMIN"}, password = "admin")
public class ShipmentResourceIT {

    private static final String DEFAULT_TRACKING_CODE = "AAAAAAAAAA";
    private static final String UPDATED_TRACKING_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_DETAILS = "BBBBBBBBBB";

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private ShipmentQueryService shipmentQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restShipmentMockMvc;

    private Shipment shipment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shipment createEntity(EntityManager em) {
        Shipment shipment = new Shipment()
            .trackingCode(DEFAULT_TRACKING_CODE)
            .date(DEFAULT_DATE)
            .details(DEFAULT_DETAILS);
        // Add required entity
        Invoice invoice;
        if (TestUtil.findAll(em, Invoice.class).isEmpty()) {
            invoice = InvoiceResourceIT.createEntity(em);
            em.persist(invoice);
            em.flush();
        } else {
            invoice = TestUtil.findAll(em, Invoice.class).get(0);
        }
        shipment.setInvoice(invoice);
        return shipment;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shipment createUpdatedEntity(EntityManager em) {
        Shipment shipment = new Shipment()
            .trackingCode(UPDATED_TRACKING_CODE)
            .date(UPDATED_DATE)
            .details(UPDATED_DETAILS);
        // Add required entity
        Invoice invoice;
        if (TestUtil.findAll(em, Invoice.class).isEmpty()) {
            invoice = InvoiceResourceIT.createUpdatedEntity(em);
            em.persist(invoice);
            em.flush();
        } else {
            invoice = TestUtil.findAll(em, Invoice.class).get(0);
        }
        shipment.setInvoice(invoice);
        return shipment;
    }

    @BeforeEach
    public void initTest() {
        shipment = createEntity(em);
    }

    @Test
    @Transactional
    public void createShipment() throws Exception {
        int databaseSizeBeforeCreate = shipmentRepository.findAll().size();
        // Create the Shipment
        restShipmentMockMvc.perform(post("/api/shipments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(shipment)))
            .andExpect(status().isCreated());

        // Validate the Shipment in the database
        List<Shipment> shipmentList = shipmentRepository.findAll();
        assertThat(shipmentList).hasSize(databaseSizeBeforeCreate + 1);
        Shipment testShipment = shipmentList.get(shipmentList.size() - 1);
        assertThat(testShipment.getTrackingCode()).isEqualTo(DEFAULT_TRACKING_CODE);
        assertThat(testShipment.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testShipment.getDetails()).isEqualTo(DEFAULT_DETAILS);
    }

    @Test
    @Transactional
    public void createShipmentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = shipmentRepository.findAll().size();

        // Create the Shipment with an existing ID
        shipment.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restShipmentMockMvc.perform(post("/api/shipments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(shipment)))
            .andExpect(status().isBadRequest());

        // Validate the Shipment in the database
        List<Shipment> shipmentList = shipmentRepository.findAll();
        assertThat(shipmentList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = shipmentRepository.findAll().size();
        // set the field null
        shipment.setDate(null);

        // Create the Shipment, which fails.


        restShipmentMockMvc.perform(post("/api/shipments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(shipment)))
            .andExpect(status().isBadRequest());

        List<Shipment> shipmentList = shipmentRepository.findAll();
        assertThat(shipmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllShipments() throws Exception {
        // Initialize the database
        shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList
        restShipmentMockMvc.perform(get("/api/shipments?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shipment.getId().intValue())))
            .andExpect(jsonPath("$.[*].trackingCode").value(hasItem(DEFAULT_TRACKING_CODE)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS)));
    }

    @Test
    @Transactional
    public void getShipment() throws Exception {
        // Initialize the database
        shipmentRepository.saveAndFlush(shipment);

        // Get the shipment
        restShipmentMockMvc.perform(get("/api/shipments/{id}", shipment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shipment.getId().intValue()))
            .andExpect(jsonPath("$.trackingCode").value(DEFAULT_TRACKING_CODE))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.details").value(DEFAULT_DETAILS));
    }


    @Test
    @Transactional
    public void getShipmentsByIdFiltering() throws Exception {
        // Initialize the database
        shipmentRepository.saveAndFlush(shipment);

        Long id = shipment.getId();

        defaultShipmentShouldBeFound("id.equals=" + id);
        defaultShipmentShouldNotBeFound("id.notEquals=" + id);

        defaultShipmentShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultShipmentShouldNotBeFound("id.greaterThan=" + id);

        defaultShipmentShouldBeFound("id.lessThanOrEqual=" + id);
        defaultShipmentShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllShipmentsByTrackingCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where trackingCode equals to DEFAULT_TRACKING_CODE
        defaultShipmentShouldBeFound("trackingCode.equals=" + DEFAULT_TRACKING_CODE);

        // Get all the shipmentList where trackingCode equals to UPDATED_TRACKING_CODE
        defaultShipmentShouldNotBeFound("trackingCode.equals=" + UPDATED_TRACKING_CODE);
    }

    @Test
    @Transactional
    public void getAllShipmentsByTrackingCodeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where trackingCode not equals to DEFAULT_TRACKING_CODE
        defaultShipmentShouldNotBeFound("trackingCode.notEquals=" + DEFAULT_TRACKING_CODE);

        // Get all the shipmentList where trackingCode not equals to UPDATED_TRACKING_CODE
        defaultShipmentShouldBeFound("trackingCode.notEquals=" + UPDATED_TRACKING_CODE);
    }

    @Test
    @Transactional
    public void getAllShipmentsByTrackingCodeIsInShouldWork() throws Exception {
        // Initialize the database
        shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where trackingCode in DEFAULT_TRACKING_CODE or UPDATED_TRACKING_CODE
        defaultShipmentShouldBeFound("trackingCode.in=" + DEFAULT_TRACKING_CODE + "," + UPDATED_TRACKING_CODE);

        // Get all the shipmentList where trackingCode equals to UPDATED_TRACKING_CODE
        defaultShipmentShouldNotBeFound("trackingCode.in=" + UPDATED_TRACKING_CODE);
    }

    @Test
    @Transactional
    public void getAllShipmentsByTrackingCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where trackingCode is not null
        defaultShipmentShouldBeFound("trackingCode.specified=true");

        // Get all the shipmentList where trackingCode is null
        defaultShipmentShouldNotBeFound("trackingCode.specified=false");
    }
                @Test
    @Transactional
    public void getAllShipmentsByTrackingCodeContainsSomething() throws Exception {
        // Initialize the database
        shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where trackingCode contains DEFAULT_TRACKING_CODE
        defaultShipmentShouldBeFound("trackingCode.contains=" + DEFAULT_TRACKING_CODE);

        // Get all the shipmentList where trackingCode contains UPDATED_TRACKING_CODE
        defaultShipmentShouldNotBeFound("trackingCode.contains=" + UPDATED_TRACKING_CODE);
    }

    @Test
    @Transactional
    public void getAllShipmentsByTrackingCodeNotContainsSomething() throws Exception {
        // Initialize the database
        shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where trackingCode does not contain DEFAULT_TRACKING_CODE
        defaultShipmentShouldNotBeFound("trackingCode.doesNotContain=" + DEFAULT_TRACKING_CODE);

        // Get all the shipmentList where trackingCode does not contain UPDATED_TRACKING_CODE
        defaultShipmentShouldBeFound("trackingCode.doesNotContain=" + UPDATED_TRACKING_CODE);
    }


    @Test
    @Transactional
    public void getAllShipmentsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where date equals to DEFAULT_DATE
        defaultShipmentShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the shipmentList where date equals to UPDATED_DATE
        defaultShipmentShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllShipmentsByDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where date not equals to DEFAULT_DATE
        defaultShipmentShouldNotBeFound("date.notEquals=" + DEFAULT_DATE);

        // Get all the shipmentList where date not equals to UPDATED_DATE
        defaultShipmentShouldBeFound("date.notEquals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllShipmentsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where date in DEFAULT_DATE or UPDATED_DATE
        defaultShipmentShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the shipmentList where date equals to UPDATED_DATE
        defaultShipmentShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllShipmentsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where date is not null
        defaultShipmentShouldBeFound("date.specified=true");

        // Get all the shipmentList where date is null
        defaultShipmentShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    public void getAllShipmentsByDetailsIsEqualToSomething() throws Exception {
        // Initialize the database
        shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where details equals to DEFAULT_DETAILS
        defaultShipmentShouldBeFound("details.equals=" + DEFAULT_DETAILS);

        // Get all the shipmentList where details equals to UPDATED_DETAILS
        defaultShipmentShouldNotBeFound("details.equals=" + UPDATED_DETAILS);
    }

    @Test
    @Transactional
    public void getAllShipmentsByDetailsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where details not equals to DEFAULT_DETAILS
        defaultShipmentShouldNotBeFound("details.notEquals=" + DEFAULT_DETAILS);

        // Get all the shipmentList where details not equals to UPDATED_DETAILS
        defaultShipmentShouldBeFound("details.notEquals=" + UPDATED_DETAILS);
    }

    @Test
    @Transactional
    public void getAllShipmentsByDetailsIsInShouldWork() throws Exception {
        // Initialize the database
        shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where details in DEFAULT_DETAILS or UPDATED_DETAILS
        defaultShipmentShouldBeFound("details.in=" + DEFAULT_DETAILS + "," + UPDATED_DETAILS);

        // Get all the shipmentList where details equals to UPDATED_DETAILS
        defaultShipmentShouldNotBeFound("details.in=" + UPDATED_DETAILS);
    }

    @Test
    @Transactional
    public void getAllShipmentsByDetailsIsNullOrNotNull() throws Exception {
        // Initialize the database
        shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where details is not null
        defaultShipmentShouldBeFound("details.specified=true");

        // Get all the shipmentList where details is null
        defaultShipmentShouldNotBeFound("details.specified=false");
    }
                @Test
    @Transactional
    public void getAllShipmentsByDetailsContainsSomething() throws Exception {
        // Initialize the database
        shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where details contains DEFAULT_DETAILS
        defaultShipmentShouldBeFound("details.contains=" + DEFAULT_DETAILS);

        // Get all the shipmentList where details contains UPDATED_DETAILS
        defaultShipmentShouldNotBeFound("details.contains=" + UPDATED_DETAILS);
    }

    @Test
    @Transactional
    public void getAllShipmentsByDetailsNotContainsSomething() throws Exception {
        // Initialize the database
        shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where details does not contain DEFAULT_DETAILS
        defaultShipmentShouldNotBeFound("details.doesNotContain=" + DEFAULT_DETAILS);

        // Get all the shipmentList where details does not contain UPDATED_DETAILS
        defaultShipmentShouldBeFound("details.doesNotContain=" + UPDATED_DETAILS);
    }


    @Test
    @Transactional
    public void getAllShipmentsByInvoiceIsEqualToSomething() throws Exception {
        // Get already existing entity
        Invoice invoice = shipment.getInvoice();
        shipmentRepository.saveAndFlush(shipment);
        Long invoiceId = invoice.getId();

        // Get all the shipmentList where invoice equals to invoiceId
        defaultShipmentShouldBeFound("invoiceId.equals=" + invoiceId);

        // Get all the shipmentList where invoice equals to invoiceId + 1
        defaultShipmentShouldNotBeFound("invoiceId.equals=" + (invoiceId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultShipmentShouldBeFound(String filter) throws Exception {
        restShipmentMockMvc.perform(get("/api/shipments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shipment.getId().intValue())))
            .andExpect(jsonPath("$.[*].trackingCode").value(hasItem(DEFAULT_TRACKING_CODE)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS)));

        // Check, that the count call also returns 1
        restShipmentMockMvc.perform(get("/api/shipments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultShipmentShouldNotBeFound(String filter) throws Exception {
        restShipmentMockMvc.perform(get("/api/shipments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restShipmentMockMvc.perform(get("/api/shipments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingShipment() throws Exception {
        // Get the shipment
        restShipmentMockMvc.perform(get("/api/shipments/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateShipment() throws Exception {
        // Initialize the database
        shipmentService.save(shipment);

        int databaseSizeBeforeUpdate = shipmentRepository.findAll().size();

        // Update the shipment
        Shipment updatedShipment = shipmentRepository.findById(shipment.getId()).get();
        // Disconnect from session so that the updates on updatedShipment are not directly saved in db
        em.detach(updatedShipment);
        updatedShipment
            .trackingCode(UPDATED_TRACKING_CODE)
            .date(UPDATED_DATE)
            .details(UPDATED_DETAILS);

        restShipmentMockMvc.perform(put("/api/shipments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedShipment)))
            .andExpect(status().isOk());

        // Validate the Shipment in the database
        List<Shipment> shipmentList = shipmentRepository.findAll();
        assertThat(shipmentList).hasSize(databaseSizeBeforeUpdate);
        Shipment testShipment = shipmentList.get(shipmentList.size() - 1);
        assertThat(testShipment.getTrackingCode()).isEqualTo(UPDATED_TRACKING_CODE);
        assertThat(testShipment.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testShipment.getDetails()).isEqualTo(UPDATED_DETAILS);
    }

    @Test
    @Transactional
    public void updateNonExistingShipment() throws Exception {
        int databaseSizeBeforeUpdate = shipmentRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShipmentMockMvc.perform(put("/api/shipments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(shipment)))
            .andExpect(status().isBadRequest());

        // Validate the Shipment in the database
        List<Shipment> shipmentList = shipmentRepository.findAll();
        assertThat(shipmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteShipment() throws Exception {
        // Initialize the database
        shipmentService.save(shipment);

        int databaseSizeBeforeDelete = shipmentRepository.findAll().size();

        // Delete the shipment
        restShipmentMockMvc.perform(delete("/api/shipments/{id}", shipment.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Shipment> shipmentList = shipmentRepository.findAll();
        assertThat(shipmentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
