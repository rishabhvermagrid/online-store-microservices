package com.rishabh.store.order.controller;

import com.rishabh.store.order.dto.request.CreateOrderRequest;
import com.rishabh.store.order.dto.response.ApiResponse;
import com.rishabh.store.order.dto.response.OrderResponse;
import com.rishabh.store.order.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 1. Place a new Order
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Order created successfully", response));
    }

    // 2. Get Order by Order ID
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable("orderId") String orderId) {
        OrderResponse response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Order fetched successfully", response));
    }

    // 3. Get all orders of a customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByCustomer(
            @PathVariable("customerId") String customerId) {
        List<OrderResponse> responses = orderService.getOrdersByCustomer(customerId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Customer orders fetched successfully", responses));
    }
}
