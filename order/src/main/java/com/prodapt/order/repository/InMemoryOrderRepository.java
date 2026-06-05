package com.prodapt.order.repository;

import com.prodapt.order.model.Order;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryOrderRepository {
    private final Map<String, Order> store = new ConcurrentHashMap<>();

    public Order save(Order order) {
        store.put(order.getId(), order);
        return order;
    }

    public Optional<Order> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Order> findByYearMonth(YearMonth yearMonth) {
        return store.values().stream()
                .filter(o -> YearMonth.from(o.getOrderDate()).equals(yearMonth))
                .collect(Collectors.toList());
    }

    public Collection<Order> findAll() {
        return store.values();
    }
}
