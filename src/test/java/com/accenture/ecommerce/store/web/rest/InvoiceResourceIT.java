package com.accenture.ecommerce.store.web.rest;

import com.accenture.ecommerce.store.StoreApp;
import com.accenture.ecommerce.store.domain.Invoice;
import com.accenture.ecommerce.store.domain.Shipment;
import com.accenture.ecommerce.store.domain.ProductOrder;
import com.accenture.ecommerce.store.repository.InvoiceRepository;
import com.accenture.ecommerce.store.service.InvoiceService;
import com.accenture.ecommerce.store.service.dto.InvoiceCriteria;
import com.accenture.ecommerce.store.service.InvoiceQueryService;

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
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.accenture.ecommerce.store.domain.enumeration.InvoiceStatus;
import com.accenture.ecommerce.store.domain.enumeration.PaymentMethod;
/**
 * Integration tests for the {@link InvoiceResource} REST controller.
 */
@SpringBootTest(classes = StoreApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class InvoiceResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_DETAILS = "BBBBBBBBBB";

    private static final InvoiceStatus DEFAULT_STATUS = InvoiceStatus.PAID;
    private static final InvoiceStatus UPDATED_STATUS = InvoiceStatus.ISSUED;

    private static final PaymentMethod DEFAULT_PAYMENT_METHOD = PaymentMethod.CREDIT_CARD;
    private static final PaymentMethod UPDATED_PAYMENT_METHOD = PaymentMethod.CASH_ON_DELIVERY;

    private static final Instant DEFAULT_PAYMENT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PAYMENT_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_PAYMENT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_PAYMENT_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_PAYMENT_AMOUNT = new BigDecimal(1 - 1);

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private InvoiceQueryService invoiceQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInvoiceMockMvc;

    private Invoice invoice;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Invoice createEntity(EntityManager em) {
        Invoice invoice = new Invoice()
            .code(DEFAULT_CODE)
            .date(DEFAULT_DATE)
            .details(DEFAULT_DETAILS)
            .status(DEFAULT_STATUS)
            .paymentMethod(DEFAULT_PAYMENT_METHOD)
            .paymentDate(DEFAULT_PAYMENT_DATE)
            .paymentAmount(DEFAULT_PAYMENT_AMOUNT);
        // Add required entity
        ProductOrder productOrder;
        if (TestUtil.findAll(em, ProductOrder.class).isEmpty()) {
            productOrder = ProductOrderResourceIT.createEntity(em);
            em.persist(productOrder);
            em.flush();
        } else {
            productOrder = TestUtil.findAll(em, ProductOrder.class).get(0);
        }
        invoice.setOrder(productOrder);
        return invoice;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Invoice createUpdatedEntity(EntityManager em) {
        Invoice invoice = new Invoice()
            .code(UPDATED_CODE)
            .date(UPDATED_DATE)
            .details(UPDATED_DETAILS)
            .status(UPDATED_STATUS)
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .paymentDate(UPDATED_PAYMENT_DATE)
            .paymentAmount(UPDATED_PAYMENT_AMOUNT);
        // Add required entity
        ProductOrder productOrder;
        if (TestUtil.findAll(em, ProductOrder.class).isEmpty()) {
            productOrder = ProductOrderResourceIT.createUpdatedEntity(em);
            em.persist(productOrder);
            em.flush();
        } else {
            productOrder = TestUtil.findAll(em, ProductOrder.class).get(0);
        }
        invoice.setOrder(productOrder);
        return invoice;
    }

    @BeforeEach
    public void initTest() {
        invoice = createEntity(em);
    }

    @Test
    @Transactional
    public void createInvoice() throws Exception {
        int databaseSizeBeforeCreate = invoiceRepository.findAll().size();
        // Create the Invoice
        restInvoiceMockMvc.perform(post("/api/invoices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(invoice)))
            .andExpect(status().isCreated());

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeCreate + 1);
        Invoice testInvoice = invoiceList.get(invoiceList.size() - 1);
        assertThat(testInvoice.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testInvoice.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testInvoice.getDetails()).isEqualTo(DEFAULT_DETAILS);
        assertThat(testInvoice.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testInvoice.getPaymentMethod()).isEqualTo(DEFAULT_PAYMENT_METHOD);
        assertThat(testInvoice.getPaymentDate()).isEqualTo(DEFAULT_PAYMENT_DATE);
        assertThat(testInvoice.getPaymentAmount()).isEqualTo(DEFAULT_PAYMENT_AMOUNT);
    }

    @Test
    @Transactional
    public void createInvoiceWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = invoiceRepository.findAll().size();

        // Create the Invoice with an existing ID
        invoice.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restInvoiceMockMvc.perform(post("/api/invoices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(invoice)))
            .andExpect(status().isBadRequest());

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = invoiceRepository.findAll().size();
        // set the field null
        invoice.setCode(null);

        // Create the Invoice, which fails.


        restInvoiceMockMvc.perform(post("/api/invoices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(invoice)))
            .andExpect(status().isBadRequest());

        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = invoiceRepository.findAll().size();
        // set the field null
        invoice.setDate(null);

        // Create the Invoice, which fails.


        restInvoiceMockMvc.perform(post("/api/invoices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(invoice)))
            .andExpect(status().isBadRequest());

        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = invoiceRepository.findAll().size();
        // set the field null
        invoice.setStatus(null);

        // Create the Invoice, which fails.


        restInvoiceMockMvc.perform(post("/api/invoices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(invoice)))
            .andExpect(status().isBadRequest());

        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPaymentMethodIsRequired() throws Exception {
        int databaseSizeBeforeTest = invoiceRepository.findAll().size();
        // set the field null
        invoice.setPaymentMethod(null);

        // Create the Invoice, which fails.


        restInvoiceMockMvc.perform(post("/api/invoices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(invoice)))
            .andExpect(status().isBadRequest());

        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPaymentDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = invoiceRepository.findAll().size();
        // set the field null
        invoice.setPaymentDate(null);

        // Create the Invoice, which fails.


        restInvoiceMockMvc.perform(post("/api/invoices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(invoice)))
            .andExpect(status().isBadRequest());

        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPaymentAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = invoiceRepository.findAll().size();
        // set the field null
        invoice.setPaymentAmount(null);

        // Create the Invoice, which fails.


        restInvoiceMockMvc.perform(post("/api/invoices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(invoice)))
            .andExpect(status().isBadRequest());

        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllInvoices() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList
        restInvoiceMockMvc.perform(get("/api/invoices?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(invoice.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].paymentMethod").value(hasItem(DEFAULT_PAYMENT_METHOD.toString())))
            .andExpect(jsonPath("$.[*].paymentDate").value(hasItem(DEFAULT_PAYMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].paymentAmount").value(hasItem(DEFAULT_PAYMENT_AMOUNT.intValue())));
    }
    
    @Test
    @Transactional
    public void getInvoice() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get the invoice
        restInvoiceMockMvc.perform(get("/api/invoices/{id}", invoice.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(invoice.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.details").value(DEFAULT_DETAILS))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.paymentMethod").value(DEFAULT_PAYMENT_METHOD.toString()))
            .andExpect(jsonPath("$.paymentDate").value(DEFAULT_PAYMENT_DATE.toString()))
            .andExpect(jsonPath("$.paymentAmount").value(DEFAULT_PAYMENT_AMOUNT.intValue()));
    }


    @Test
    @Transactional
    public void getInvoicesByIdFiltering() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        Long id = invoice.getId();

        defaultInvoiceShouldBeFound("id.equals=" + id);
        defaultInvoiceShouldNotBeFound("id.notEquals=" + id);

        defaultInvoiceShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultInvoiceShouldNotBeFound("id.greaterThan=" + id);

        defaultInvoiceShouldBeFound("id.lessThanOrEqual=" + id);
        defaultInvoiceShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllInvoicesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where code equals to DEFAULT_CODE
        defaultInvoiceShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the invoiceList where code equals to UPDATED_CODE
        defaultInvoiceShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllInvoicesByCodeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where code not equals to DEFAULT_CODE
        defaultInvoiceShouldNotBeFound("code.notEquals=" + DEFAULT_CODE);

        // Get all the invoiceList where code not equals to UPDATED_CODE
        defaultInvoiceShouldBeFound("code.notEquals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllInvoicesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where code in DEFAULT_CODE or UPDATED_CODE
        defaultInvoiceShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the invoiceList where code equals to UPDATED_CODE
        defaultInvoiceShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllInvoicesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where code is not null
        defaultInvoiceShouldBeFound("code.specified=true");

        // Get all the invoiceList where code is null
        defaultInvoiceShouldNotBeFound("code.specified=false");
    }
                @Test
    @Transactional
    public void getAllInvoicesByCodeContainsSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where code contains DEFAULT_CODE
        defaultInvoiceShouldBeFound("code.contains=" + DEFAULT_CODE);

        // Get all the invoiceList where code contains UPDATED_CODE
        defaultInvoiceShouldNotBeFound("code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllInvoicesByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where code does not contain DEFAULT_CODE
        defaultInvoiceShouldNotBeFound("code.doesNotContain=" + DEFAULT_CODE);

        // Get all the invoiceList where code does not contain UPDATED_CODE
        defaultInvoiceShouldBeFound("code.doesNotContain=" + UPDATED_CODE);
    }


    @Test
    @Transactional
    public void getAllInvoicesByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where date equals to DEFAULT_DATE
        defaultInvoiceShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the invoiceList where date equals to UPDATED_DATE
        defaultInvoiceShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllInvoicesByDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where date not equals to DEFAULT_DATE
        defaultInvoiceShouldNotBeFound("date.notEquals=" + DEFAULT_DATE);

        // Get all the invoiceList where date not equals to UPDATED_DATE
        defaultInvoiceShouldBeFound("date.notEquals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllInvoicesByDateIsInShouldWork() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where date in DEFAULT_DATE or UPDATED_DATE
        defaultInvoiceShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the invoiceList where date equals to UPDATED_DATE
        defaultInvoiceShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllInvoicesByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where date is not null
        defaultInvoiceShouldBeFound("date.specified=true");

        // Get all the invoiceList where date is null
        defaultInvoiceShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    public void getAllInvoicesByDetailsIsEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where details equals to DEFAULT_DETAILS
        defaultInvoiceShouldBeFound("details.equals=" + DEFAULT_DETAILS);

        // Get all the invoiceList where details equals to UPDATED_DETAILS
        defaultInvoiceShouldNotBeFound("details.equals=" + UPDATED_DETAILS);
    }

    @Test
    @Transactional
    public void getAllInvoicesByDetailsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where details not equals to DEFAULT_DETAILS
        defaultInvoiceShouldNotBeFound("details.notEquals=" + DEFAULT_DETAILS);

        // Get all the invoiceList where details not equals to UPDATED_DETAILS
        defaultInvoiceShouldBeFound("details.notEquals=" + UPDATED_DETAILS);
    }

    @Test
    @Transactional
    public void getAllInvoicesByDetailsIsInShouldWork() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where details in DEFAULT_DETAILS or UPDATED_DETAILS
        defaultInvoiceShouldBeFound("details.in=" + DEFAULT_DETAILS + "," + UPDATED_DETAILS);

        // Get all the invoiceList where details equals to UPDATED_DETAILS
        defaultInvoiceShouldNotBeFound("details.in=" + UPDATED_DETAILS);
    }

    @Test
    @Transactional
    public void getAllInvoicesByDetailsIsNullOrNotNull() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where details is not null
        defaultInvoiceShouldBeFound("details.specified=true");

        // Get all the invoiceList where details is null
        defaultInvoiceShouldNotBeFound("details.specified=false");
    }
                @Test
    @Transactional
    public void getAllInvoicesByDetailsContainsSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where details contains DEFAULT_DETAILS
        defaultInvoiceShouldBeFound("details.contains=" + DEFAULT_DETAILS);

        // Get all the invoiceList where details contains UPDATED_DETAILS
        defaultInvoiceShouldNotBeFound("details.contains=" + UPDATED_DETAILS);
    }

    @Test
    @Transactional
    public void getAllInvoicesByDetailsNotContainsSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where details does not contain DEFAULT_DETAILS
        defaultInvoiceShouldNotBeFound("details.doesNotContain=" + DEFAULT_DETAILS);

        // Get all the invoiceList where details does not contain UPDATED_DETAILS
        defaultInvoiceShouldBeFound("details.doesNotContain=" + UPDATED_DETAILS);
    }


    @Test
    @Transactional
    public void getAllInvoicesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where status equals to DEFAULT_STATUS
        defaultInvoiceShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the invoiceList where status equals to UPDATED_STATUS
        defaultInvoiceShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllInvoicesByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where status not equals to DEFAULT_STATUS
        defaultInvoiceShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the invoiceList where status not equals to UPDATED_STATUS
        defaultInvoiceShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllInvoicesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultInvoiceShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the invoiceList where status equals to UPDATED_STATUS
        defaultInvoiceShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllInvoicesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where status is not null
        defaultInvoiceShouldBeFound("status.specified=true");

        // Get all the invoiceList where status is null
        defaultInvoiceShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllInvoicesByPaymentMethodIsEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paymentMethod equals to DEFAULT_PAYMENT_METHOD
        defaultInvoiceShouldBeFound("paymentMethod.equals=" + DEFAULT_PAYMENT_METHOD);

        // Get all the invoiceList where paymentMethod equals to UPDATED_PAYMENT_METHOD
        defaultInvoiceShouldNotBeFound("paymentMethod.equals=" + UPDATED_PAYMENT_METHOD);
    }

    @Test
    @Transactional
    public void getAllInvoicesByPaymentMethodIsNotEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paymentMethod not equals to DEFAULT_PAYMENT_METHOD
        defaultInvoiceShouldNotBeFound("paymentMethod.notEquals=" + DEFAULT_PAYMENT_METHOD);

        // Get all the invoiceList where paymentMethod not equals to UPDATED_PAYMENT_METHOD
        defaultInvoiceShouldBeFound("paymentMethod.notEquals=" + UPDATED_PAYMENT_METHOD);
    }

    @Test
    @Transactional
    public void getAllInvoicesByPaymentMethodIsInShouldWork() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paymentMethod in DEFAULT_PAYMENT_METHOD or UPDATED_PAYMENT_METHOD
        defaultInvoiceShouldBeFound("paymentMethod.in=" + DEFAULT_PAYMENT_METHOD + "," + UPDATED_PAYMENT_METHOD);

        // Get all the invoiceList where paymentMethod equals to UPDATED_PAYMENT_METHOD
        defaultInvoiceShouldNotBeFound("paymentMethod.in=" + UPDATED_PAYMENT_METHOD);
    }

    @Test
    @Transactional
    public void getAllInvoicesByPaymentMethodIsNullOrNotNull() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paymentMethod is not null
        defaultInvoiceShouldBeFound("paymentMethod.specified=true");

        // Get all the invoiceList where paymentMethod is null
        defaultInvoiceShouldNotBeFound("paymentMethod.specified=false");
    }

    @Test
    @Transactional
    public void getAllInvoicesByPaymentDateIsEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paymentDate equals to DEFAULT_PAYMENT_DATE
        defaultInvoiceShouldBeFound("paymentDate.equals=" + DEFAULT_PAYMENT_DATE);

        // Get all the invoiceList where paymentDate equals to UPDATED_PAYMENT_DATE
        defaultInvoiceShouldNotBeFound("paymentDate.equals=" + UPDATED_PAYMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllInvoicesByPaymentDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paymentDate not equals to DEFAULT_PAYMENT_DATE
        defaultInvoiceShouldNotBeFound("paymentDate.notEquals=" + DEFAULT_PAYMENT_DATE);

        // Get all the invoiceList where paymentDate not equals to UPDATED_PAYMENT_DATE
        defaultInvoiceShouldBeFound("paymentDate.notEquals=" + UPDATED_PAYMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllInvoicesByPaymentDateIsInShouldWork() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paymentDate in DEFAULT_PAYMENT_DATE or UPDATED_PAYMENT_DATE
        defaultInvoiceShouldBeFound("paymentDate.in=" + DEFAULT_PAYMENT_DATE + "," + UPDATED_PAYMENT_DATE);

        // Get all the invoiceList where paymentDate equals to UPDATED_PAYMENT_DATE
        defaultInvoiceShouldNotBeFound("paymentDate.in=" + UPDATED_PAYMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllInvoicesByPaymentDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paymentDate is not null
        defaultInvoiceShouldBeFound("paymentDate.specified=true");

        // Get all the invoiceList where paymentDate is null
        defaultInvoiceShouldNotBeFound("paymentDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllInvoicesByPaymentAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paymentAmount equals to DEFAULT_PAYMENT_AMOUNT
        defaultInvoiceShouldBeFound("paymentAmount.equals=" + DEFAULT_PAYMENT_AMOUNT);

        // Get all the invoiceList where paymentAmount equals to UPDATED_PAYMENT_AMOUNT
        defaultInvoiceShouldNotBeFound("paymentAmount.equals=" + UPDATED_PAYMENT_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllInvoicesByPaymentAmountIsNotEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paymentAmount not equals to DEFAULT_PAYMENT_AMOUNT
        defaultInvoiceShouldNotBeFound("paymentAmount.notEquals=" + DEFAULT_PAYMENT_AMOUNT);

        // Get all the invoiceList where paymentAmount not equals to UPDATED_PAYMENT_AMOUNT
        defaultInvoiceShouldBeFound("paymentAmount.notEquals=" + UPDATED_PAYMENT_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllInvoicesByPaymentAmountIsInShouldWork() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paymentAmount in DEFAULT_PAYMENT_AMOUNT or UPDATED_PAYMENT_AMOUNT
        defaultInvoiceShouldBeFound("paymentAmount.in=" + DEFAULT_PAYMENT_AMOUNT + "," + UPDATED_PAYMENT_AMOUNT);

        // Get all the invoiceList where paymentAmount equals to UPDATED_PAYMENT_AMOUNT
        defaultInvoiceShouldNotBeFound("paymentAmount.in=" + UPDATED_PAYMENT_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllInvoicesByPaymentAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paymentAmount is not null
        defaultInvoiceShouldBeFound("paymentAmount.specified=true");

        // Get all the invoiceList where paymentAmount is null
        defaultInvoiceShouldNotBeFound("paymentAmount.specified=false");
    }

    @Test
    @Transactional
    public void getAllInvoicesByPaymentAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paymentAmount is greater than or equal to DEFAULT_PAYMENT_AMOUNT
        defaultInvoiceShouldBeFound("paymentAmount.greaterThanOrEqual=" + DEFAULT_PAYMENT_AMOUNT);

        // Get all the invoiceList where paymentAmount is greater than or equal to UPDATED_PAYMENT_AMOUNT
        defaultInvoiceShouldNotBeFound("paymentAmount.greaterThanOrEqual=" + UPDATED_PAYMENT_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllInvoicesByPaymentAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paymentAmount is less than or equal to DEFAULT_PAYMENT_AMOUNT
        defaultInvoiceShouldBeFound("paymentAmount.lessThanOrEqual=" + DEFAULT_PAYMENT_AMOUNT);

        // Get all the invoiceList where paymentAmount is less than or equal to SMALLER_PAYMENT_AMOUNT
        defaultInvoiceShouldNotBeFound("paymentAmount.lessThanOrEqual=" + SMALLER_PAYMENT_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllInvoicesByPaymentAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paymentAmount is less than DEFAULT_PAYMENT_AMOUNT
        defaultInvoiceShouldNotBeFound("paymentAmount.lessThan=" + DEFAULT_PAYMENT_AMOUNT);

        // Get all the invoiceList where paymentAmount is less than UPDATED_PAYMENT_AMOUNT
        defaultInvoiceShouldBeFound("paymentAmount.lessThan=" + UPDATED_PAYMENT_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllInvoicesByPaymentAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paymentAmount is greater than DEFAULT_PAYMENT_AMOUNT
        defaultInvoiceShouldNotBeFound("paymentAmount.greaterThan=" + DEFAULT_PAYMENT_AMOUNT);

        // Get all the invoiceList where paymentAmount is greater than SMALLER_PAYMENT_AMOUNT
        defaultInvoiceShouldBeFound("paymentAmount.greaterThan=" + SMALLER_PAYMENT_AMOUNT);
    }


    @Test
    @Transactional
    public void getAllInvoicesByShipmentIsEqualToSomething() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);
        Shipment shipment = ShipmentResourceIT.createEntity(em);
        em.persist(shipment);
        em.flush();
        invoice.addShipment(shipment);
        invoiceRepository.saveAndFlush(invoice);
        Long shipmentId = shipment.getId();

        // Get all the invoiceList where shipment equals to shipmentId
        defaultInvoiceShouldBeFound("shipmentId.equals=" + shipmentId);

        // Get all the invoiceList where shipment equals to shipmentId + 1
        defaultInvoiceShouldNotBeFound("shipmentId.equals=" + (shipmentId + 1));
    }


    @Test
    @Transactional
    public void getAllInvoicesByOrderIsEqualToSomething() throws Exception {
        // Get already existing entity
        ProductOrder order = invoice.getOrder();
        invoiceRepository.saveAndFlush(invoice);
        Long orderId = order.getId();

        // Get all the invoiceList where order equals to orderId
        defaultInvoiceShouldBeFound("orderId.equals=" + orderId);

        // Get all the invoiceList where order equals to orderId + 1
        defaultInvoiceShouldNotBeFound("orderId.equals=" + (orderId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInvoiceShouldBeFound(String filter) throws Exception {
        restInvoiceMockMvc.perform(get("/api/invoices?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(invoice.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].paymentMethod").value(hasItem(DEFAULT_PAYMENT_METHOD.toString())))
            .andExpect(jsonPath("$.[*].paymentDate").value(hasItem(DEFAULT_PAYMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].paymentAmount").value(hasItem(DEFAULT_PAYMENT_AMOUNT.intValue())));

        // Check, that the count call also returns 1
        restInvoiceMockMvc.perform(get("/api/invoices/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInvoiceShouldNotBeFound(String filter) throws Exception {
        restInvoiceMockMvc.perform(get("/api/invoices?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restInvoiceMockMvc.perform(get("/api/invoices/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingInvoice() throws Exception {
        // Get the invoice
        restInvoiceMockMvc.perform(get("/api/invoices/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateInvoice() throws Exception {
        // Initialize the database
        invoiceService.save(invoice);

        int databaseSizeBeforeUpdate = invoiceRepository.findAll().size();

        // Update the invoice
        Invoice updatedInvoice = invoiceRepository.findById(invoice.getId()).get();
        // Disconnect from session so that the updates on updatedInvoice are not directly saved in db
        em.detach(updatedInvoice);
        updatedInvoice
            .code(UPDATED_CODE)
            .date(UPDATED_DATE)
            .details(UPDATED_DETAILS)
            .status(UPDATED_STATUS)
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .paymentDate(UPDATED_PAYMENT_DATE)
            .paymentAmount(UPDATED_PAYMENT_AMOUNT);

        restInvoiceMockMvc.perform(put("/api/invoices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedInvoice)))
            .andExpect(status().isOk());

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeUpdate);
        Invoice testInvoice = invoiceList.get(invoiceList.size() - 1);
        assertThat(testInvoice.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testInvoice.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testInvoice.getDetails()).isEqualTo(UPDATED_DETAILS);
        assertThat(testInvoice.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testInvoice.getPaymentMethod()).isEqualTo(UPDATED_PAYMENT_METHOD);
        assertThat(testInvoice.getPaymentDate()).isEqualTo(UPDATED_PAYMENT_DATE);
        assertThat(testInvoice.getPaymentAmount()).isEqualTo(UPDATED_PAYMENT_AMOUNT);
    }

    @Test
    @Transactional
    public void updateNonExistingInvoice() throws Exception {
        int databaseSizeBeforeUpdate = invoiceRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInvoiceMockMvc.perform(put("/api/invoices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(invoice)))
            .andExpect(status().isBadRequest());

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteInvoice() throws Exception {
        // Initialize the database
        invoiceService.save(invoice);

        int databaseSizeBeforeDelete = invoiceRepository.findAll().size();

        // Delete the invoice
        restInvoiceMockMvc.perform(delete("/api/invoices/{id}", invoice.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
