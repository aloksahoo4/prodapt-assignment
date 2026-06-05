package com.prodapt.order.service;

import com.prodapt.order.dto.CreateOrderRequest;
import com.prodapt.order.dto.OrderResponse;
import com.prodapt.order.exception.OrderNotFoundException;
import com.prodapt.order.model.Order;
import com.prodapt.order.repository.InMemoryOrderRepository;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final InMemoryOrderRepository repository;

    public OrderService(InMemoryOrderRepository repository) {
        this.repository = repository;
    }

    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = new Order(
                UUID.randomUUID().toString(),
                request.getCustomerName(),
                request.getCustomerType(),
                request.getAmount().setScale(2, RoundingMode.HALF_UP),
                request.getOrderDate(),
                LocalDateTime.now()
        );

        return OrderResponse.from(repository.save(order));
    }

    public OrderResponse getOrderById(String id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return OrderResponse.from(order);
    }

    public List<OrderResponse> getOrdersByMonth(YearMonth yearMonth) {
        return repository.findByYearMonth(yearMonth).stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }
}
