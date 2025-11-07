package com.laioffer.delivery.order;

import com.laioffer.delivery.auth.UserPrincipal;
import com.laioffer.delivery.pkg.Package;
import com.laioffer.delivery.pkg.PackageService;
import com.laioffer.delivery.common.ValidationUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request,
                                     Authentication authentication) {
        UUID userId = requireUserId(authentication);
        Order order = orderService.createOrder(request.toCommand(), userId);
        List<Package> packages = orderService.loadPackages(order.getId());
        return toResponse(order, packages);
    }

    @GetMapping
    public List<OrderResponse> listOrders(Authentication authentication) {
        UUID userId = requireUserId(authentication);
        List<Order> orders = orderService.getOrders(userId);
        return orders.stream()
                .map(order -> toResponse(order, orderService.loadPackages(order.getId())))
                .toList();
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable Long id, Authentication authentication) {
        UUID userId = requireUserId(authentication);
        Order order = orderService.getOrder(id, userId);
        List<Package> packages = orderService.loadPackages(order.getId());
        return toResponse(order, packages);
    }

    @PostMapping("/{id}/confirm")
    public OrderResponse confirmOrder(@PathVariable Long id, Authentication authentication) {
        UUID userId = requireUserId(authentication);
        Order order = orderService.confirmOrder(id, userId);
        List<Package> packages = orderService.loadPackages(order.getId());
        return toResponse(order, packages);
    }

    private UUID requireUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.id();
        }
        throw new AccessDeniedException("Authentication required");
    }

    private OrderResponse toResponse(Order order, List<Package> packages) {
        List<PackageResponse> packageResponses = (packages == null ? Collections.<PackageResponse>emptyList()
                : packages.stream()
                .map(pkg -> new PackageResponse(
                        pkg.getId(),
                        pkg.getLengthCm(),
                        pkg.getWidthCm(),
                        pkg.getHeightCm(),
                        pkg.getWeightKg(),
                        pkg.getDescription()))
                .toList());

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getFromAddress(),
                order.getToAddress(),
                order.getWeightKg(),
                order.getLengthCm(),
                order.getWidthCm(),
                order.getHeightCm(),
                order.getNotes(),
                order.getConfirmedAt(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                packageResponses
        );
    }

    @Data
    public static class CreateOrderRequest {
        @NotBlank
        private String fromAddress;
        @NotBlank
        private String toAddress;

        @DecimalMin(value = "0.00", inclusive = false, message = "must be greater than 0")
        private BigDecimal weightKg;
        @DecimalMin(value = "0.00", inclusive = false, message = "must be greater than 0")
        private BigDecimal lengthCm;
        @DecimalMin(value = "0.00", inclusive = false, message = "must be greater than 0")
        private BigDecimal widthCm;
        @DecimalMin(value = "0.00", inclusive = false, message = "must be greater than 0")
        private BigDecimal heightCm;
        private String notes;

        @Valid
        private List<PackageRequest> packages;

        OrderService.CreateOrderCommand toCommand() {
            List<PackageService.PackagePayload> packagePayloads = CollectionUtils.isEmpty(packages)
                    ? Collections.emptyList()
                    : packages.stream()
                    .map(pkg -> new PackageService.PackagePayload(
                            pkg.getLengthCm(),
                            pkg.getWidthCm(),
                            pkg.getHeightCm(),
                            pkg.getWeightKg(),
                            ValidationUtil.trimToNull(pkg.getDescription())))
                    .toList();

            return new OrderService.CreateOrderCommand(
                    ValidationUtil.trimToNull(fromAddress),
                    ValidationUtil.trimToNull(toAddress),
                    weightKg,
                    lengthCm,
                    widthCm,
                    heightCm,
                    ValidationUtil.trimToNull(notes),
                    packagePayloads
            );
        }
    }

    @Data
    public static class PackageRequest {
        @DecimalMin(value = "0.00", inclusive = false, message = "must be greater than 0")
        private BigDecimal lengthCm;
        @DecimalMin(value = "0.00", inclusive = false, message = "must be greater than 0")
        private BigDecimal widthCm;
        @DecimalMin(value = "0.00", inclusive = false, message = "must be greater than 0")
        private BigDecimal heightCm;
        @DecimalMin(value = "0.00", inclusive = false, message = "must be greater than 0")
        private BigDecimal weightKg;
        private String description;
    }

    public record PackageResponse(
            Long id,
            BigDecimal lengthCm,
            BigDecimal widthCm,
            BigDecimal heightCm,
            BigDecimal weightKg,
            String description
    ) {
    }

    public record OrderResponse(
            Long id,
            UUID userId,
            String fromAddress,
            String toAddress,
            BigDecimal weightKg,
            BigDecimal lengthCm,
            BigDecimal widthCm,
            BigDecimal heightCm,
            String notes,
            Instant confirmedAt,
            Instant createdAt,
            Instant updatedAt,
            List<PackageResponse> packages
    ) {
    }
}
