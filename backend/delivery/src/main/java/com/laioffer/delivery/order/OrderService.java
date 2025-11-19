package com.laioffer.delivery.order;

import com.laioffer.delivery.order.dto.CreateOrderRequest;
import com.laioffer.delivery.pkg.Package;
import com.laioffer.delivery.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * 创建订单的核心方法
     * @param user 当前登录用户 (如果是游客，传入 null)
     * @param request 前端传来的 DTO 数据
     * @return 保存成功后的 Order 实体
     */
    @Transactional
    public Order createOrder(User user, CreateOrderRequest request) {
        // 1. 初始化订单基础信息
        Order order = Order.builder()
                .fromAddress(request.pickupAddress())
                .toAddress(request.deliveryAddress())
                .notes(request.notes())
                .trackingNumber(UUID.randomUUID().toString()) // 生成唯一追踪号
                .status(OrderStatus.CREATED)                  // 初始状态
                .userId(user != null ? user.getId() : null)   // 关键：Guest 为 null，User 为 UUID
                .build();

        // 2. 处理包裹列表 (DTO -> Entity) 并计算总重
        BigDecimal totalWeight = BigDecimal.ZERO;

        if (request.packages() != null) {
            for (CreateOrderRequest.PackageDto pkgDto : request.packages()) {
                // 创建 Package 实体
                Package pkg = Package.builder()
                        .weightKg(pkgDto.weightKg())
                        .lengthCm(pkgDto.lengthCm())
                        .widthCm(pkgDto.widthCm())
                        .heightCm(pkgDto.heightCm())
                        .description(pkgDto.description())
                        .order(order) // 🔥 必须设置：将 Package 关联到 Order
                        .build();

                // 添加到 Order 的列表中 (为了级联保存)
                order.addPackage(pkg);

                // 累加重量
                totalWeight = totalWeight.add(pkgDto.weightKg());
            }
        }

        // 3. 计算价格 (Mock Logic: Base $10 + $2/kg)
        BigDecimal price = calculateMockPrice(totalWeight);
        order.setTotalPrice(price);

        // 4. 保存到数据库
        // 因为 Order 上配置了 CascadeType.ALL，所以保存 Order 时会自动保存所有的 Package
        return orderRepository.save(order);
    }

    // 简单的计价算法 (P0阶段使用)
    private BigDecimal calculateMockPrice(BigDecimal weight) {
        BigDecimal basePrice = new BigDecimal("10.00");
        BigDecimal ratePerKg = new BigDecimal("2.00");
        return basePrice.add(weight.multiply(ratePerKg));
    }

    /**
     * 根据 ID 获取订单 (包含安全校验逻辑的雏形)
     */
    public Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    /**
     * 根据 TrackingNumber 获取订单 (用于游客查询)
     */
    public Order getOrderByTrackingNumber(String trackingNumber) {
        return orderRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Order not found with tracking number: " + trackingNumber));
    }
}