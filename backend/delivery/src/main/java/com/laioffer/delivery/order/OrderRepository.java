package com.laioffer.delivery.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 1. 用于 "My Orders" 页面：查找某个用户的所有订单
    // 自动生成 SQL: SELECT * FROM orders WHERE user_id = ?
    List<Order> findAllByUserId(UUID userId);

    // 2. 用于游客/用户查询详情：根据追踪号查找订单
    // 自动生成 SQL: SELECT * FROM orders WHERE tracking_number = ?
    Optional<Order> findByTrackingNumber(String trackingNumber);

    // 3. (可选) 查找某个用户处于特定状态的订单
    List<Order> findAllByUserIdAndStatus(UUID userId, OrderStatus status);
}