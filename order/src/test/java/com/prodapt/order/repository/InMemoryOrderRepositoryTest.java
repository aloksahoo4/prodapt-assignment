package com.prodapt.order.repository;

import com.prodapt.order.enums.CustomerType;
import com.prodapt.order.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryOrderRepositoryTest {

    private InMemoryOrderRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOrderRepository();
    }

    private Order order(String id, LocalDate date) {
        return new Order(id, "Alice", CustomerType.STANDARD,
                new BigDecimal("100.00"), date, LocalDateTime.now());
    }

    @Test
    @DisplayName("save stores the order and returns it")
    void saveStoresOrder() {
        Order saved = repository.save(order("id-1", LocalDate.of(2024, 1, 10)));

        assertThat(saved.getId()).isEqualTo("id-1");
        assertThat(repository.findById("id-1")).contains(saved);
    }

    @Test
    @DisplayName("save with an existing id overwrites the previous order")
    void saveOverwritesExisting() {
        repository.save(order("id-1", LocalDate.of(2024, 1, 10)));
        Order updated = new Order("id-1", "Bob", CustomerType.PREMIUM,
                new BigDecimal("200.00"), LocalDate.of(2024, 1, 11), LocalDateTime.now());

        repository.save(updated);

        assertThat(repository.findById("id-1")).contains(updated);
        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("findById returns empty for an unknown id")
    void findByIdMissing() {
        assertThat(repository.findById("nope")).isEmpty();
    }

    @Test
    @DisplayName("findByYearMonth returns only orders within that month")
    void findByYearMonthFilters() {
        repository.save(order("jan-1", LocalDate.of(2024, 1, 5)));
        repository.save(order("jan-2", LocalDate.of(2024, 1, 28)));
        repository.save(order("feb-1", LocalDate.of(2024, 2, 1)));
        repository.save(order("otherYear", LocalDate.of(2025, 1, 5)));

        List<Order> january2024 = repository.findByYearMonth(YearMonth.of(2024, 1));

        assertThat(january2024)
                .extracting(Order::getId)
                .containsExactlyInAnyOrder("jan-1", "jan-2");
    }

    @Test
    @DisplayName("findByYearMonth returns empty list when no orders match")
    void findByYearMonthNoMatch() {
        repository.save(order("jan-1", LocalDate.of(2024, 1, 5)));

        assertThat(repository.findByYearMonth(YearMonth.of(2024, 12))).isEmpty();
    }

    @Test
    @DisplayName("findAll returns all stored orders")
    void findAllReturnsEverything() {
        repository.save(order("id-1", LocalDate.of(2024, 1, 10)));
        repository.save(order("id-2", LocalDate.of(2024, 2, 10)));

        assertThat(repository.findAll()).hasSize(2);
    }
}
