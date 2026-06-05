package org.example.revenuecalculator;

import org.example.enums.CustomerType;
import org.example.model.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MonthlyRevenueCalculatorMain {

    public static void main(String[] args) {
        MonthlyRevenueCalculator calculator = new MonthlyRevenueCalculator();

        List<Order> orders = getDummyOrders();

        Map<YearMonth, BigDecimal> revenue = calculator.calculateMonthlyRevenue(orders);

        revenue.forEach((month, total) ->
                System.out.printf("%-12s : %,.2f%n", month, total));
    }

    //create dummy orders
    private static List<Order> getDummyOrders() {

        return Arrays.asList(
                // Jan 2024 - STANDARD
                new Order("O1", new BigDecimal("500.00"),
                        LocalDate.of(2024, 1, 10), CustomerType.STANDARD),

                // Jan 2024 - PREMIUM  -> 1000 * 0.90 = 900.00
                new Order("O2", new BigDecimal("1000.00"),
                        LocalDate.of(2024, 1, 20), CustomerType.PREMIUM),

                // Feb 2024 - STANDARD
                new Order("O3", new BigDecimal("750.00"),
                        LocalDate.of(2024, 2, 5),  CustomerType.STANDARD),


                new Order("O4", new BigDecimal("200.00"),
                        LocalDate.of(2024, 2, 15), CustomerType.PREMIUM),

                // --- Edge cases and negative cases must be ignored ---
                new Order("O5", null,
                        LocalDate.of(2024, 2, 1),  CustomerType.STANDARD),
                new Order("O6", new BigDecimal("-50"),
                        LocalDate.of(2024, 2, 1),  CustomerType.STANDARD),
                new Order("O7", new BigDecimal("300"),
                        null,  CustomerType.STANDARD),
                null  // null order itself
        );
    }
}
