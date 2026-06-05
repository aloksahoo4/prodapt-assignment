package com.prodapt.order.model;

import com.prodapt.order.enums.CustomerType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Order {

    private final String id;
    private final String customerName;
    private final CustomerType customerType;
    private final BigDecimal amount;
    private final LocalDate orderDate;
    private final LocalDateTime createdAt;

    public Order(String id, String customerName, CustomerType customerType, BigDecimal amount, LocalDate orderDate, LocalDateTime createdAt) {
        this.id = id;
        this.customerName = customerName;
        this.customerType = customerType;
        this.amount = amount;
        this.orderDate = orderDate;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
