package com.prodapt.order.controller;

import com.prodapt.order.dto.CreateOrderRequest;
import com.prodapt.order.dto.OrderResponse;
import com.prodapt.order.exception.InvalidMonthFormatException;
import com.prodapt.order.exception.OrderNotFoundException;
import com.prodapt.order.enums.CustomerType;
import com.prodapt.order.model.Order;
import com.prodapt.order.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private static OrderResponse sampleResponse(String id) {
        Order order = new Order(id, "Alice", CustomerType.PREMIUM,
                new BigDecimal("250.00"), LocalDate.of(2024, 3, 1),
                LocalDateTime.of(2024, 3, 1, 10, 0));
        return OrderResponse.from(order);
    }

    // ---------- createOrder ----------

    @Test
    @DisplayName("createOrder returns 201 CREATED with the created order in the body")
    void createOrderReturns201() {
        CreateOrderRequest request = new CreateOrderRequest();
        OrderResponse created = sampleResponse("abc-123");
        when(orderService.createOrder(request)).thenReturn(created);

        ResponseEntity<OrderResponse> response = orderController.createOrder(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(created);
        assertThat(response.getBody().getId()).isEqualTo("abc-123");
        assertThat(response.getBody().getCustomerName()).isEqualTo("Alice");
        assertThat(response.getBody().getCustomerType()).isEqualTo(CustomerType.PREMIUM);
        verify(orderService).createOrder(request);
    }

    @Test
    @DisplayName("createOrder propagates exceptions raised by the service")
    void createOrderPropagatesServiceFailure() {
        CreateOrderRequest request = new CreateOrderRequest();
        when(orderService.createOrder(request)).thenThrow(new RuntimeException("boom"));

        assertThatThrownBy(() -> orderController.createOrder(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("boom");
    }

    // ---------- getOrderById ----------

    @Test
    @DisplayName("getOrderById returns 200 OK for an existing order")
    void getByIdReturns200() {
        OrderResponse existing = sampleResponse("abc-123");
        when(orderService.getOrderById("abc-123")).thenReturn(existing);

        ResponseEntity<OrderResponse> response = orderController.getOrderById("abc-123");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(existing);
        assertThat(response.getBody().getId()).isEqualTo("abc-123");
        assertThat(response.getBody().getCustomerName()).isEqualTo("Alice");
        verify(orderService).getOrderById("abc-123");
    }

    @Test
    @DisplayName("getOrderById propagates OrderNotFoundException when the order is missing")
    void getByIdMissingThrows() {
        when(orderService.getOrderById("missing"))
                .thenThrow(new OrderNotFoundException("missing"));

        assertThatThrownBy(() -> orderController.getOrderById("missing"))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("missing");
    }

    // ---------- getOrdersByMonth ----------

    @Test
    @DisplayName("getOrdersByMonth returns 200 OK with the matching orders")
    void getByMonthReturns200() {
        List<OrderResponse> orders = List.of(sampleResponse("abc-123"));
        when(orderService.getOrdersByMonth(YearMonth.of(2024, 3))).thenReturn(orders);

        ResponseEntity<List<OrderResponse>> response = orderController.getOrdersByMonth("2024-03");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getId()).isEqualTo("abc-123");
        verify(orderService).getOrdersByMonth(YearMonth.of(2024, 3));
    }

    @Test
    @DisplayName("getOrdersByMonth returns 200 OK with an empty list when nothing matches")
    void getByMonthReturnsEmptyList() {
        when(orderService.getOrdersByMonth(YearMonth.of(2024, 4))).thenReturn(List.of());

        ResponseEntity<List<OrderResponse>> response = orderController.getOrdersByMonth("2024-04");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    @DisplayName("getOrdersByMonth throws InvalidMonthFormatException for a bad month and never calls the service")
    void getByMonthInvalidFormatThrows() {
        assertThatThrownBy(() -> orderController.getOrdersByMonth("2024-13"))
                .isInstanceOf(InvalidMonthFormatException.class)
                .hasMessageContaining("Invalid month format")
                .hasMessageContaining("2024-13");

        verifyNoInteractions(orderService);
    }

    @Test
    @DisplayName("getOrdersByMonth throws InvalidMonthFormatException for a non-date value")
    void getByMonthGarbageThrows() {
        assertThatThrownBy(() -> orderController.getOrdersByMonth("not-a-month"))
                .isInstanceOf(InvalidMonthFormatException.class)
                .hasMessageContaining("not-a-month");

        verify(orderService, never()).getOrdersByMonth(any());
    }
}
