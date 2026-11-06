package com.rishabh.store.order.service;

import com.rishabh.store.order.client.InventoryClient;
import com.rishabh.store.order.dto.request.CreateOrderRequest;
import com.rishabh.store.order.dto.request.OrderItemRequest;
import com.rishabh.store.order.dto.response.ApiResponse;
import com.rishabh.store.order.dto.response.OrderResponse;
import com.rishabh.store.order.dto.response.ProductAvailabilityResponse;
import com.rishabh.store.order.entity.OrderEntity;
import com.rishabh.store.order.entity.OrderItemEntity;
import com.rishabh.store.order.exception.OrderNotFoundException;
import com.rishabh.store.order.exception.ProductOutOfStockException;
import com.rishabh.store.order.model.OrderStatus;
import com.rishabh.store.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;


    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order for customer: {}", request.customerId());
        //finding product ids from request
        List<String> productIds = request.items().stream()
                .map(OrderItemRequest::productId)
                .toList();

        verifyStock(productIds);


        //calculating total amount
        BigDecimal totalAmount = request.items().stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //creating order with items
        String orderId = UUID.randomUUID().toString();

        //
        OrderEntity order = OrderEntity.builder()
                .orderId(orderId)
                .customerId(request.customerId())
                .status(OrderStatus.CONFIRMED)
                .totalAmount(totalAmount)
                .shippingAddress(request.shippingAddress())
                .build();

        //adding each item to parent
        for (OrderItemRequest item : request.items()) {
            OrderItemEntity itemEntity = OrderItemEntity.builder()
                    .productId(item.productId())
                    .sku(item.sku())
                    .quantity(item.quantity())
                    .price(item.price())
                    .build();
            order.addItem(itemEntity);//it will create bi-direction link
        }

        //saving it to database
        OrderEntity savedOrder = orderRepository.save(order);

        //returning response dto to customer
        return OrderResponse.fromEntity(savedOrder);

    }

    private void verifyStock(List<String> productIds) {
        //calling inventory service to check stock
        ApiResponse<List<ProductAvailabilityResponse>> response =
                inventoryClient.getAvailability(productIds);

        //checking response
        if (response == null || response.data() == null) {
            throw new ProductOutOfStockException("stocks not verified from inventory service");
        }

        //checking if any item is out of stock or not, inventory service will give true/false for each item's availability
        Map<String, Boolean> availabilityMap = response.data().stream()
                .collect(Collectors.toMap(
                        ProductAvailabilityResponse::uniqId,
                        ProductAvailabilityResponse::availability
                ));

        List<String> outOfStockProducts = productIds.stream()
                .filter(id -> !Boolean.TRUE.equals(availabilityMap.get(id)))
                .toList();

        //if any item is out of stock then order can not be fulfilled
        if (!outOfStockProducts.isEmpty()) {
            throw new ProductOutOfStockException(" these products are out of stock: " + outOfStockProducts);
        }

    }

    public OrderResponse getOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .map(orderEntity -> OrderResponse.fromEntity(orderEntity))
                .orElseThrow(() -> new OrderNotFoundException("Order not found with Id: " + orderId));
    }

    public List<OrderResponse> getOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(orderEntity -> OrderResponse.fromEntity(orderEntity))
                .toList();
    }


}
