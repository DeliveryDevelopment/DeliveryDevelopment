package com.laioffer.delivery.order;

import com.laioffer.delivery.auth.UserPrincipal;
import com.laioffer.delivery.order.dto.CreateOrderRequest;
import com.laioffer.delivery.pkg.Package;
import com.laioffer.delivery.user.User;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
// 1. 修正路径，匹配 SecurityConfig
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * P0 核心：下单接口
     * 支持 登录用户 (User) 和 游客 (Guest)
     */
    @PostMapping
    public OrderResponse createOrder(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CreateOrderRequest request) {

        // 2. 修正 Guest 逻辑：不要强制 requireUserId
        User user = null;
        if (userPrincipal != null) {
            user = userPrincipal.getUser();
        }

        // 调用 Service (Service 内部会处理 user 为 null 的情况)
        Order order = orderService.createOrder(user, request);

        // 3. 返回响应 (注意：Order 实体里已经没有 packages 列表了，因为是 Lazy Load，建议手动转)
        return toResponse(order);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        // 获取订单
        Order order = orderService.getOrder(id);

        // 4. 简单的安全检查
        // 如果订单属于某个用户，且当前用户不是该用户，则拒绝
        if (order.getUserId() != null) {
            if (userPrincipal == null || !order.getUserId().equals(userPrincipal.getUser().getId())) {
                throw new RuntimeException("Unauthorized");
            }
        }

        return toResponse(order);
    }

    // --- Helper Methods & DTOs ---

    private OrderResponse toResponse(Order order) {
        // 把 Entity 转为 Response DTO
        // 注意：这里要去读取 order.getPackages()，如果是 Lazy Load 需要在 Service 层加 @Transactional 确保 Session 还在
        List<PackageResponse> pkgResponses = order.getPackages().stream()
                .map(p -> new PackageResponse(
                        p.getId(),
                        p.getWeightKg(),
                        p.getLengthCm(),
                        p.getWidthCm(),
                        p.getHeightCm(),
                        p.getDescription()
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getTrackingNumber(), // 加上 Tracking Number
                order.getFromAddress(),
                order.getToAddress(),
                order.getNotes(),
                order.getTotalPrice(),     // 加上总价
                order.getStatus(),
                order.getCreatedAt(),
                pkgResponses
        );
    }

    // 响应体 DTO (放在类内部方便管理，也可以提出去)
    public record OrderResponse(
            Long id,
            UUID userId,
            String trackingNumber,
            String fromAddress,
            String toAddress,
            String notes,
            BigDecimal totalPrice,
            OrderStatus status,
            Instant createdAt,
            List<PackageResponse> packages
    ) {}

    public record PackageResponse(
            Long id,
            BigDecimal weightKg,
            BigDecimal lengthCm,
            BigDecimal widthCm,
            BigDecimal heightCm,
            String description
    ) {}
}