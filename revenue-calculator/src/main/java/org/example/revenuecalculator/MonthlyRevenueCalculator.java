package org.example.revenuecalculator;

import org.example.enums.CustomerType;
import org.example.model.Order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class MonthlyRevenueCalculator {

    private static final BigDecimal PREMIUM_DISCOUNT = new BigDecimal("0.10");

    /**
     * process orders and calculates monthly revenue
     * @param orders
     * @return Map of monthly revenue
     */
    public Map<YearMonth, BigDecimal> calculateMonthlyRevenue(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<YearMonth, BigDecimal> revenue = orders.stream()
                .filter(this::isValidOrder)
                .collect(Collectors.toMap(
                        order -> YearMonth.from(order.getOrderDate()),
                        this::effectiveAmount,
                        BigDecimal::add
                ));

        return new TreeMap<>(revenue);
    }

    private boolean isValidOrder(Order order) {
        return order != null
                && order.getAmount() != null
                && order.getAmount().compareTo(BigDecimal.ZERO) > 0
                && order.getOrderDate() != null
                && order.getCustomerType() != null;
    }

    private BigDecimal effectiveAmount(Order order) {
        if (order.getCustomerType() == CustomerType.PREMIUM) {
            return order.getAmount()
                    .multiply(BigDecimal.ONE.subtract(PREMIUM_DISCOUNT))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return order.getAmount().setScale(2, RoundingMode.HALF_UP);
    }
}
