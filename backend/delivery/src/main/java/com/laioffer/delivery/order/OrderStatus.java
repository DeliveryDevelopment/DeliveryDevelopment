package com.laioffer.delivery.order;

public enum OrderStatus {
    CREATED,            // 已下单，未支付/未确认
    PENDING_PAYMENT,    // (可选)
    PAID,               // 已支付
    DISPATCHED,         // 已发货 (机器人/无人机接单)
    IN_TRANSIT,         // 运输中
    DELIVERED,          // 已送达
    COMPLETED,          // 订单结束
    CANCELLED           // 已取消
}