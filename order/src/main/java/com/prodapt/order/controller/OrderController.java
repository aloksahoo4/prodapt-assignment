package com.prodapt.order.controller;

import com.prodapt.order.dto.CreateOrderRequest;
import com.prodapt.order.dto.OrderResponse;
import com.prodapt.order.exception.InvalidMonthFormatException;
import com.prodapt.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable String id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrdersByMonth(
            @RequestParam String month) {

        YearMonth yearMonth = parseMonth(month);
        return ResponseEntity.ok(orderService.getOrdersByMonth(yearMonth));
    }

    private YearMonth parseMonth(String month) {
        try {
            return YearMonth.parse(month);   // expects YYYY-MM
        } catch (DateTimeParseException e) {
            throw new InvalidMonthFormatException(month);
        }
    }
}
