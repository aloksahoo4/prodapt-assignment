package com.prodapt.order.service;

import com.prodapt.order.dto.CreateOrderRequest;
import com.prodapt.order.dto.OrderResponse;
import com.prodapt.order.exception.OrderNotFoundException;
import com.prodapt.order.enums.CustomerType;
import com.prodapt.order.repository.InMemoryOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderServiceTest {

    private OrderService service;

    @BeforeEach
    void setUp() {
        // Real in-memory repository: keeps the test honest about round-trip behaviour.
        service = new OrderService(new InMemoryOrderRepository());
    }

    private CreateOrderRequest request(String name, CustomerType type,
                                       BigDecimal amount, LocalDate date) {
        CreateOrderRequest r = new CreateOrderRequest();
        r.setCustomerName(name);
        r.setCustomerType(type);
        r.setAmount(amount);
        r.setOrderDate(date);
        return r;
    }

    @Test
    @DisplayName("createOrder persists the order and returns a populated response")
    void createOrderPopulatesResponse() {
        OrderResponse response = service.createOrder(
                request("Alice", CustomerType.PREMIUM,
                        new BigDecimal("250.00"), LocalDate.of(2024, 3, 1)));

        assertThat(response.getId()).isNotBlank();
        assertThat(response.getCustomerName()).isEqualTo("Alice");
        assertThat(response.getCustomerType()).isEqualTo(CustomerType.PREMIUM);
        assertThat(response.getOrderDate()).isEqualTo(LocalDate.of(2024, 3, 1));
        assertThat(response.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("createOrder normalises the amount to a scale of 2 (HALF_UP)")
    void createOrderScalesAmount() {
        OrderResponse halfUp = service.createOrder(
                request("Bob", CustomerType.STANDARD,
                        new BigDecimal("100.005"), LocalDate.of(2024, 1, 1)));
        OrderResponse padded = service.createOrder(
                request("Carol", CustomerType.STANDARD,
                        new BigDecimal("100.5"), LocalDate.of(2024, 1, 1)));

        assertThat(halfUp.getAmount()).isEqualByComparingTo("100.01");
        assertThat(halfUp.getAmount().scale()).isEqualTo(2);
        assertThat(padded.getAmount()).isEqualByComparingTo("100.50");
        assertThat(padded.getAmount().scale()).isEqualTo(2);
    }

    @Test
    @DisplayName("createOrder generates a unique id per order")
    void createOrderGeneratesUniqueIds() {
        OrderResponse first = service.createOrder(
                request("A", CustomerType.STANDARD, new BigDecimal("10.00"), LocalDate.of(2024, 1, 1)));
        OrderResponse second = service.createOrder(
                request("B", CustomerType.STANDARD, new BigDecimal("10.00"), LocalDate.of(2024, 1, 1)));

        assertThat(first.getId()).isNotEqualTo(second.getId());
    }

    @Test
    @DisplayName("getOrderById returns a previously created order")
    void getOrderByIdReturnsOrder() {
        OrderResponse created = service.createOrder(
                request("Alice", CustomerType.STANDARD,
                        new BigDecimal("99.00"), LocalDate.of(2024, 5, 5)));

        OrderResponse fetched = service.getOrderById(created.getId());

        assertThat(fetched.getId()).isEqualTo(created.getId());
        assertThat(fetched.getCustomerName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("getOrderById throws OrderNotFoundException for an unknown id")
    void getOrderByIdMissingThrows() {
        assertThatThrownBy(() -> service.getOrderById("does-not-exist"))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("does-not-exist");
    }

    @Test
    @DisplayName("getOrdersByMonth returns only orders for the requested month")
    void getOrdersByMonthFilters() {
        service.createOrder(request("Jan-1", CustomerType.STANDARD,
                new BigDecimal("10.00"), LocalDate.of(2024, 1, 3)));
        service.createOrder(request("Jan-2", CustomerType.STANDARD,
                new BigDecimal("20.00"), LocalDate.of(2024, 1, 25)));
        service.createOrder(request("Feb-1", CustomerType.STANDARD,
                new BigDecimal("30.00"), LocalDate.of(2024, 2, 10)));

        List<OrderResponse> january = service.getOrdersByMonth(java.time.YearMonth.of(2024, 1));

        assertThat(january)
                .extracting(OrderResponse::getCustomerName)
                .containsExactlyInAnyOrder("Jan-1", "Jan-2");
    }

    @Test
    @DisplayName("getOrdersByMonth returns an empty list when nothing matches")
    void getOrdersByMonthEmpty() {
        service.createOrder(request("Jan", CustomerType.STANDARD,
                new BigDecimal("10.00"), LocalDate.of(2024, 1, 3)));

        assertThat(service.getOrdersByMonth(java.time.YearMonth.of(2030, 6))).isEmpty();
    }
}
