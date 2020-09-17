package com.accenture.ecommerce.store.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import com.accenture.ecommerce.store.domain.enumeration.InvoiceStatus;
import com.accenture.ecommerce.store.domain.enumeration.PaymentMethod;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.BigDecimalFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link com.accenture.ecommerce.store.domain.Invoice} entity. This class is used
 * in {@link com.accenture.ecommerce.store.web.rest.InvoiceResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /invoices?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class InvoiceCriteria implements Serializable, Criteria {
    /**
     * Class for filtering InvoiceStatus
     */
    public static class InvoiceStatusFilter extends Filter<InvoiceStatus> {

        public InvoiceStatusFilter() {
        }

        public InvoiceStatusFilter(InvoiceStatusFilter filter) {
            super(filter);
        }

        @Override
        public InvoiceStatusFilter copy() {
            return new InvoiceStatusFilter(this);
        }

    }
    /**
     * Class for filtering PaymentMethod
     */
    public static class PaymentMethodFilter extends Filter<PaymentMethod> {

        public PaymentMethodFilter() {
        }

        public PaymentMethodFilter(PaymentMethodFilter filter) {
            super(filter);
        }

        @Override
        public PaymentMethodFilter copy() {
            return new PaymentMethodFilter(this);
        }

    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private InstantFilter date;

    private StringFilter details;

    private InvoiceStatusFilter status;

    private PaymentMethodFilter paymentMethod;

    private InstantFilter paymentDate;

    private BigDecimalFilter paymentAmount;

    private LongFilter shipmentId;

    private LongFilter orderId;

    private LongFilter customerId;

    public InvoiceCriteria() {
    }

    public InvoiceCriteria(InvoiceCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.code = other.code == null ? null : other.code.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.details = other.details == null ? null : other.details.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.paymentMethod = other.paymentMethod == null ? null : other.paymentMethod.copy();
        this.paymentDate = other.paymentDate == null ? null : other.paymentDate.copy();
        this.paymentAmount = other.paymentAmount == null ? null : other.paymentAmount.copy();
        this.shipmentId = other.shipmentId == null ? null : other.shipmentId.copy();
        this.orderId = other.orderId == null ? null : other.orderId.copy();
        this.customerId = other.customerId == null ? null : other.customerId.copy();
    }

    @Override
    public InvoiceCriteria copy() {
        return new InvoiceCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getCode() {
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public InstantFilter getDate() {
        return date;
    }

    public void setDate(InstantFilter date) {
        this.date = date;
    }

    public StringFilter getDetails() {
        return details;
    }

    public void setDetails(StringFilter details) {
        this.details = details;
    }

    public InvoiceStatusFilter getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatusFilter status) {
        this.status = status;
    }

    public PaymentMethodFilter getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethodFilter paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public InstantFilter getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(InstantFilter paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimalFilter getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimalFilter paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public LongFilter getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(LongFilter shipmentId) {
        this.shipmentId = shipmentId;
    }

    public LongFilter getOrderId() {
        return orderId;
    }

    public void setOrderId(LongFilter orderId) {
        this.orderId = orderId;
    }

    public LongFilter getCustomerId() { return customerId; }

    public void setCustomerId(LongFilter customerId) { this.customerId = customerId; }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final InvoiceCriteria that = (InvoiceCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(date, that.date) &&
            Objects.equals(details, that.details) &&
            Objects.equals(status, that.status) &&
            Objects.equals(paymentMethod, that.paymentMethod) &&
            Objects.equals(paymentDate, that.paymentDate) &&
            Objects.equals(paymentAmount, that.paymentAmount) &&
            Objects.equals(shipmentId, that.shipmentId) &&
            Objects.equals(orderId, that.orderId) &&
            Objects.equals(customerId, that.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        code,
        date,
        details,
        status,
        paymentMethod,
        paymentDate,
        paymentAmount,
        shipmentId,
        orderId,
        customerId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InvoiceCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (code != null ? "code=" + code + ", " : "") +
                (date != null ? "date=" + date + ", " : "") +
                (details != null ? "details=" + details + ", " : "") +
                (status != null ? "status=" + status + ", " : "") +
                (paymentMethod != null ? "paymentMethod=" + paymentMethod + ", " : "") +
                (paymentDate != null ? "paymentDate=" + paymentDate + ", " : "") +
                (paymentAmount != null ? "paymentAmount=" + paymentAmount + ", " : "") +
                (shipmentId != null ? "shipmentId=" + shipmentId + ", " : "") +
                (orderId != null ? "orderId=" + orderId + ", " : "") +
                (customerId != null ? "customerId=" + customerId + ", " : "") +
            "}";
    }

}
