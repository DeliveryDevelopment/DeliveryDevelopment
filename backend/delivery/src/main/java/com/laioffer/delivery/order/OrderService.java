package com.laioffer.delivery.order;

import com.laioffer.delivery.auth.RequestPrincipal;
import com.laioffer.delivery.common.NotFoundException;
import com.laioffer.delivery.pkg.Package;
import com.laioffer.delivery.pkg.PackageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final HexFormat HEX = HexFormat.of();

    private final OrderRepository orderRepository;
    private final PackageService packageService;

    @Transactional
    public OrderCreationResult createOrder(CreateOrderCommand command, RequestPrincipal principal) {
        UUID userId = principal.userId();
        String guestToken = principal.guestToken();
        boolean generatedGuestToken = false;

        if (!StringUtils.hasText(command.fromAddress()) || !StringUtils.hasText(command.toAddress())) {
            throw new IllegalArgumentException("From and to addresses are required");
        }
        if (userId == null) {
            if (!StringUtils.hasText(guestToken)) {
                guestToken = generateGuestToken();
                generatedGuestToken = true;
            }
        } else {
            guestToken = null;
        }

        Order order = Order.builder()
                .userId(userId)
                .guestToken(guestToken)
                .fromAddress(command.fromAddress())
                .toAddress(command.toAddress())
                .weightKg(command.weightKg())
                .lengthCm(command.lengthCm())
                .widthCm(command.widthCm())
                .heightCm(command.heightCm())
                .notes(command.notes())
                .build();
        packageService.applyPackages(order, command.packages());
        Order saved = orderRepository.save(order);
        return new OrderCreationResult(saved, generatedGuestToken ? guestToken : null);
    }

    @Transactional
    public Order confirmOrder(Long orderId, RequestPrincipal principal) {
        Order order = getOrder(orderId, principal);
        if (order.getConfirmedAt() == null) {
            order.setConfirmedAt(Instant.now());
        }
        return order;
    }

    @Transactional
    public Order getOrder(Long orderId, RequestPrincipal principal) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        if (!belongsToPrincipal(order, principal)) {
            throw new AccessDeniedException("You do not have access to this order");
        }
        return order;
    }

    @Transactional
    public List<Order> getOrders(RequestPrincipal principal) {
        if (principal.userId() != null) {
            return orderRepository.findByUserIdOrderByCreatedAtDesc(principal.userId());
        }
        if (StringUtils.hasText(principal.guestToken())) {
            return orderRepository.findByGuestTokenOrderByCreatedAtDesc(principal.guestToken());
        }
        throw new AccessDeniedException("Authentication required");
    }

    @Transactional
    public int claimGuestOrders(String guestToken, UUID userId) {
        if (!StringUtils.hasText(guestToken) || userId == null) {
            return 0;
        }
        List<Order> guestOrders = orderRepository.findByGuestTokenOrderByCreatedAtDesc(guestToken);
        for (Order order : guestOrders) {
            order.setUserId(userId);
            order.setGuestToken(null);
        }
        return guestOrders.size();
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

    public record OrderCreationResult(Order order, String newGuestToken) {

        public boolean hasNewGuestToken() {
            return newGuestToken != null;
        }
    }

    public List<Package> loadPackages(Long orderId) {
        return packageService.findByOrderId(orderId);
    }

    private boolean belongsToPrincipal(Order order, RequestPrincipal principal) {
        if (principal.userId() != null && principal.userId().equals(order.getUserId())) {
            return true;
        }
        return principal.guestToken() != null && principal.guestToken().equals(order.getGuestToken());
    }

    private String generateGuestToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return HEX.formatHex(bytes);
    }
}
