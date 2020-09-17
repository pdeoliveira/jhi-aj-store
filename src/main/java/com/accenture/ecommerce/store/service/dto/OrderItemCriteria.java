package com.accenture.ecommerce.store.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import com.accenture.ecommerce.store.domain.enumeration.OrderItemStatus;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.BigDecimalFilter;

/**
 * Criteria class for the {@link com.accenture.ecommerce.store.domain.OrderItem} entity. This class is used
 * in {@link com.accenture.ecommerce.store.web.rest.OrderItemResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /order-items?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class OrderItemCriteria implements Serializable, Criteria {
    /**
     * Class for filtering OrderItemStatus
     */
    public static class OrderItemStatusFilter extends Filter<OrderItemStatus> {

        public OrderItemStatusFilter() {
        }

        public OrderItemStatusFilter(OrderItemStatusFilter filter) {
            super(filter);
        }

        @Override
        public OrderItemStatusFilter copy() {
            return new OrderItemStatusFilter(this);
        }

    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter quantity;

    private BigDecimalFilter totalPrice;

    private OrderItemStatusFilter status;

    private LongFilter productId;

    private LongFilter orderId;

    private LongFilter customerId;

    public OrderItemCriteria() {
    }

    public OrderItemCriteria(OrderItemCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.quantity = other.quantity == null ? null : other.quantity.copy();
        this.totalPrice = other.totalPrice == null ? null : other.totalPrice.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.productId = other.productId == null ? null : other.productId.copy();
        this.orderId = other.orderId == null ? null : other.orderId.copy();
        this.customerId = other.customerId == null ? null : other.customerId.copy();
    }

    @Override
    public OrderItemCriteria copy() {
        return new OrderItemCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public IntegerFilter getQuantity() {
        return quantity;
    }

    public void setQuantity(IntegerFilter quantity) {
        this.quantity = quantity;
    }

    public BigDecimalFilter getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimalFilter totalPrice) {
        this.totalPrice = totalPrice;
    }

    public OrderItemStatusFilter getStatus() {
        return status;
    }

    public void setStatus(OrderItemStatusFilter status) {
        this.status = status;
    }

    public LongFilter getProductId() {
        return productId;
    }

    public void setProductId(LongFilter productId) {
        this.productId = productId;
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
        final OrderItemCriteria that = (OrderItemCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(quantity, that.quantity) &&
            Objects.equals(totalPrice, that.totalPrice) &&
            Objects.equals(status, that.status) &&
            Objects.equals(productId, that.productId) &&
            Objects.equals(orderId, that.orderId) &&
            Objects.equals(customerId, that.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        quantity,
        totalPrice,
        status,
        productId,
        orderId,
        customerId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderItemCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (quantity != null ? "quantity=" + quantity + ", " : "") +
                (totalPrice != null ? "totalPrice=" + totalPrice + ", " : "") +
                (status != null ? "status=" + status + ", " : "") +
                (productId != null ? "productId=" + productId + ", " : "") +
                (orderId != null ? "orderId=" + orderId + ", " : "") +
                (customerId != null ? "customerId=" + customerId + ", " : "") +
            "}";
    }

}
