package org.example.revenuecalculator;

import org.example.enums.CustomerType;
import org.example.model.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MonthlyRevenueCalculatorTest {

    private final MonthlyRevenueCalculator calculator = new MonthlyRevenueCalculator();

    private static Order order(String id, String amount, LocalDate date, CustomerType type) {
        return new Order(id, amount == null ? null : new BigDecimal(amount), date, type);
    }

    @Test
    @DisplayName("Returns empty map for a null order list")
    void returnsEmptyMapForNullList() {
        assertTrue(calculator.calculateMonthlyRevenue(null).isEmpty());
    }

    @Test
    @DisplayName("Returns empty map for an empty order list")
    void returnsEmptyMapForEmptyList() {
        assertTrue(calculator.calculateMonthlyRevenue(Collections.emptyList()).isEmpty());
    }

    @Test
    @DisplayName("Standard order revenue is the full amount, scaled to 2 decimals")
    void standardOrderUsesFullAmount() {
        List<Order> orders = List.of(
                order("O1", "500.00", LocalDate.of(2024, 1, 10), CustomerType.STANDARD));

        Map<YearMonth, BigDecimal> revenue = calculator.calculateMonthlyRevenue(orders);

        assertEquals(0, new BigDecimal("500.00")
                .compareTo(revenue.get(YearMonth.of(2024, 1))));
    }

    @Test
    @DisplayName("Premium order receives a 10% discount")
    void premiumOrderGetsTenPercentDiscount() {
        List<Order> orders = List.of(
                order("O1", "1000.00", LocalDate.of(2024, 1, 20), CustomerType.PREMIUM));

        Map<YearMonth, BigDecimal> revenue = calculator.calculateMonthlyRevenue(orders);

        assertEquals(0, new BigDecimal("900.00")
                .compareTo(revenue.get(YearMonth.of(2024, 1))));
    }

    @Test
    @DisplayName("Orders in the same month are summed together")
    void sumsOrdersWithinSameMonth() {
        List<Order> orders = List.of(
                order("O1", "500.00", LocalDate.of(2024, 1, 10), CustomerType.STANDARD),
                order("O2", "1000.00", LocalDate.of(2024, 1, 20), CustomerType.PREMIUM));

        Map<YearMonth, BigDecimal> revenue = calculator.calculateMonthlyRevenue(orders);

        // 500.00 + (1000.00 * 0.90) = 1400.00
        assertEquals(0, new BigDecimal("1400.00")
                .compareTo(revenue.get(YearMonth.of(2024, 1))));
    }

    @Test
    @DisplayName("Revenue is grouped per calendar month")
    void groupsRevenueByMonth() {
        List<Order> orders = List.of(
                order("O1", "500.00", LocalDate.of(2024, 1, 10), CustomerType.STANDARD),
                order("O2", "1000.00", LocalDate.of(2024, 1, 20), CustomerType.PREMIUM),
                order("O3", "750.00", LocalDate.of(2024, 2, 5), CustomerType.STANDARD),
                order("O4", "200.00", LocalDate.of(2024, 2, 15), CustomerType.PREMIUM));

        Map<YearMonth, BigDecimal> revenue = calculator.calculateMonthlyRevenue(orders);

        assertEquals(2, revenue.size());
        assertEquals(0, new BigDecimal("1400.00")
                .compareTo(revenue.get(YearMonth.of(2024, 1))));
        assertEquals(0, new BigDecimal("930.00")
                .compareTo(revenue.get(YearMonth.of(2024, 2))));
    }

    @Test
    @DisplayName("Same calendar month in different years is kept separate")
    void separatesSameMonthAcrossYears() {
        List<Order> orders = List.of(
                order("O1", "100.00", LocalDate.of(2024, 1, 10), CustomerType.STANDARD),
                order("O2", "200.00", LocalDate.of(2025, 1, 10), CustomerType.STANDARD));

        Map<YearMonth, BigDecimal> revenue = calculator.calculateMonthlyRevenue(orders);

        assertEquals(2, revenue.size());
        assertEquals(0, new BigDecimal("100.00")
                .compareTo(revenue.get(YearMonth.of(2024, 1))));
        assertEquals(0, new BigDecimal("200.00")
                .compareTo(revenue.get(YearMonth.of(2025, 1))));
    }

    @Test
    @DisplayName("Invalid orders (null order, null/negative/zero amount, null date, null type) are ignored")
    void ignoresInvalidOrders() {
        List<Order> orders = new ArrayList<>(Arrays.asList(
                order("Valid", "500.00", LocalDate.of(2024, 2, 10), CustomerType.STANDARD),
                order("NullAmount", null, LocalDate.of(2024, 2, 1), CustomerType.STANDARD),
                order("Negative", "-50", LocalDate.of(2024, 2, 1), CustomerType.STANDARD),
                order("Zero", "0", LocalDate.of(2024, 2, 1), CustomerType.STANDARD),
                order("NullDate", "300", null, CustomerType.STANDARD),
                order("NullType", "300", LocalDate.of(2024, 2, 1), null),
                null));

        Map<YearMonth, BigDecimal> revenue = calculator.calculateMonthlyRevenue(orders);

        assertEquals(1, revenue.size());
        assertEquals(0, new BigDecimal("500.00")
                .compareTo(revenue.get(YearMonth.of(2024, 2))));
    }

    @Test
    @DisplayName("Premium discount rounds half-up to 2 decimal places")
    void premiumDiscountRoundsHalfUp() {
        // 99.99 * 0.90 = 89.991 -> rounds to 89.99
        List<Order> orders = List.of(
                order("O1", "99.99", LocalDate.of(2024, 3, 1), CustomerType.PREMIUM));

        Map<YearMonth, BigDecimal> revenue = calculator.calculateMonthlyRevenue(orders);

        BigDecimal result = revenue.get(YearMonth.of(2024, 3));
        assertEquals(0, new BigDecimal("89.99").compareTo(result));
        assertEquals(2, result.scale());
    }

    @Test
    @DisplayName("Result map is sorted by YearMonth ascending")
    void resultIsSortedByMonthAscending() {
        List<Order> orders = List.of(
                order("O1", "100.00", LocalDate.of(2024, 3, 1), CustomerType.STANDARD),
                order("O2", "100.00", LocalDate.of(2024, 1, 1), CustomerType.STANDARD),
                order("O3", "100.00", LocalDate.of(2024, 2, 1), CustomerType.STANDARD));

        Map<YearMonth, BigDecimal> revenue = calculator.calculateMonthlyRevenue(orders);

        List<YearMonth> keys = new ArrayList<>(revenue.keySet());
        assertEquals(List.of(
                YearMonth.of(2024, 1),
                YearMonth.of(2024, 2),
                YearMonth.of(2024, 3)), keys);
    }

    @Test
    @DisplayName("List with only invalid orders yields an empty map")
    void allInvalidOrdersYieldEmptyMap() {
        List<Order> orders = new ArrayList<>(Arrays.asList(
                order("Negative", "-1", LocalDate.of(2024, 1, 1), CustomerType.STANDARD),
                (Order) null));

        Map<YearMonth, BigDecimal> revenue = calculator.calculateMonthlyRevenue(orders);

        assertTrue(revenue.isEmpty());
    }
}
