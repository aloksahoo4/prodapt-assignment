package org.example.model;

import org.example.enums.CustomerType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Order {
    private final String orderId;
    private final BigDecimal amount;
    private final LocalDate orderDate;
    private final CustomerType customerType;

    public Order(String orderId, BigDecimal amount,
                 LocalDate orderDate, CustomerType customerType) {
        this.orderId      = orderId;
        this.amount       = amount;
        this.orderDate    = orderDate;
        this.customerType = customerType;
    }

    public String        getOrderId()      { return orderId; }
    public BigDecimal    getAmount()       { return amount; }
    public LocalDate     getOrderDate()    { return orderDate; }
    public CustomerType  getCustomerType() { return customerType; }

    @Override
    public String toString() {
        return String.format("Order{id='%s', amount=%s, date=%s, type=%s}",
                orderId, amount, orderDate, customerType);
    }
}
