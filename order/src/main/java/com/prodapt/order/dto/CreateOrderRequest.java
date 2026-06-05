package com.prodapt.order.dto;

import com.prodapt.order.enums.CustomerType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateOrderRequest {

    @NotBlank(message = "customerName must not be blank")
    private String customerName;

    @NotNull(message = "customerType must not be null (STANDARD or PREMIUM)")
    private CustomerType customerType;

    @NotNull(message = "amount must not be null")
    @DecimalMin(value = "0.01", message = "amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "amount must have at most 2 decimal places")
    private BigDecimal amount;

    @NotNull(message = "orderDate must not be null")
    @PastOrPresent(message = "orderDate cannot be in the future")
    private LocalDate orderDate;

    public @NotBlank(message = "customerName must not be blank") String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(@NotBlank(message = "customerName must not be blank") String customerName) {
        this.customerName = customerName;
    }

    public @NotNull(message = "customerType must not be null (STANDARD or PREMIUM)") CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(@NotNull(message = "customerType must not be null (STANDARD or PREMIUM)") CustomerType customerType) {
        this.customerType = customerType;
    }

    public @NotNull(message = "amount must not be null") @DecimalMin(value = "0.01", message = "amount must be greater than 0") @Digits(integer = 10, fraction = 2, message = "amount must have at most 2 decimal places") BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(@NotNull(message = "amount must not be null") @DecimalMin(value = "0.01", message = "amount must be greater than 0") @Digits(integer = 10, fraction = 2, message = "amount must have at most 2 decimal places") BigDecimal amount) {
        this.amount = amount;
    }

    public @NotNull(message = "orderDate must not be null") @PastOrPresent(message = "orderDate cannot be in the future") LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(@NotNull(message = "orderDate must not be null") @PastOrPresent(message = "orderDate cannot be in the future") LocalDate orderDate) {
        this.orderDate = orderDate;
    }
}
