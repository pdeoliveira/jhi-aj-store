package com.accenture.ecommerce.store.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link com.accenture.ecommerce.store.domain.Shipment} entity. This class is used
 * in {@link com.accenture.ecommerce.store.web.rest.ShipmentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /shipments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ShipmentCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter trackingCode;

    private InstantFilter date;

    private StringFilter details;

    private LongFilter invoiceId;

    public ShipmentCriteria() {
    }

    public ShipmentCriteria(ShipmentCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.trackingCode = other.trackingCode == null ? null : other.trackingCode.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.details = other.details == null ? null : other.details.copy();
        this.invoiceId = other.invoiceId == null ? null : other.invoiceId.copy();
    }

    @Override
    public ShipmentCriteria copy() {
        return new ShipmentCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(StringFilter trackingCode) {
        this.trackingCode = trackingCode;
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

    public LongFilter getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(LongFilter invoiceId) {
        this.invoiceId = invoiceId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ShipmentCriteria that = (ShipmentCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(trackingCode, that.trackingCode) &&
            Objects.equals(date, that.date) &&
            Objects.equals(details, that.details) &&
            Objects.equals(invoiceId, that.invoiceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        trackingCode,
        date,
        details,
        invoiceId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShipmentCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (trackingCode != null ? "trackingCode=" + trackingCode + ", " : "") +
                (date != null ? "date=" + date + ", " : "") +
                (details != null ? "details=" + details + ", " : "") +
                (invoiceId != null ? "invoiceId=" + invoiceId + ", " : "") +
            "}";
    }

}
