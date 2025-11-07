package com.laioffer.delivery.order;

import com.laioffer.delivery.common.NotFoundException;
import com.laioffer.delivery.pkg.Package;
import com.laioffer.delivery.pkg.PackageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PackageService packageService;

    @Transactional
    public Order createOrder(CreateOrderCommand command, UUID userId) {
        if (userId == null) {
            throw new AccessDeniedException("Authentication required");
        }
        if (!StringUtils.hasText(command.fromAddress()) || !StringUtils.hasText(command.toAddress())) {
            throw new IllegalArgumentException("From and to addresses are required");
        }

        Order order = Order.builder()
                .userId(userId)
                .fromAddress(command.fromAddress())
                .toAddress(command.toAddress())
                .weightKg(command.weightKg())
                .lengthCm(command.lengthCm())
                .widthCm(command.widthCm())
                .heightCm(command.heightCm())
                .notes(command.notes())
                .build();
        packageService.applyPackages(order, command.packages());
        return orderRepository.save(order);
    }

    @Transactional
    public Order confirmOrder(Long orderId, UUID userId) {
        Order order = getOrder(orderId, userId);
        if (order.getConfirmedAt() == null) {
            order.setConfirmedAt(Instant.now());
        }
        return order;
    }

    @Transactional
    public Order getOrder(Long orderId, UUID userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        if (!belongsToUser(order, userId)) {
            throw new AccessDeniedException("You do not have access to this order");
        }
        return order;
    }

    @Transactional
    public List<Order> getOrders(UUID userId) {
        if (userId == null) {
            throw new AccessDeniedException("Authentication required");
        }
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Package> loadPackages(Long orderId) {
        return packageService.findByOrderId(orderId);
    }

    public record CreateOrderCommand(
            String fromAddress,
            String toAddress,
            BigDecimal weightKg,
            BigDecimal lengthCm,
            BigDecimal widthCm,
            BigDecimal heightCm,
            String notes,
            List<PackageService.PackagePayload> packages
    ) {
    }

    private boolean belongsToUser(Order order, UUID userId) {
        return userId != null && userId.equals(order.getUserId());
    }
}
