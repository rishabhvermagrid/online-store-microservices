package com.rishabh.store.order.repository;

import com.rishabh.store.order.entity.OrderEntity;
import com.rishabh.store.order.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {
    //it will fetch order history for a customer: most recent orders first
    List<OrderEntity> findByCustomerIdOrderByCreatedAtDesc(String customerId);

    // fetch orders by status
    List<OrderEntity> findByStatus(OrderStatus status);
}
