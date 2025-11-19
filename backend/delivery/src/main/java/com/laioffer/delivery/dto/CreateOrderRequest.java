package com.laioffer.delivery.order.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

/**
 * 前端下单时发送的 JSON 数据结构
 * 包含：取件地址、送件地址、包裹列表
 */
public record CreateOrderRequest(
        @NotBlank(message = "Pickup address is required")
        String pickupAddress,

        @NotBlank(message = "Delivery address is required")
        String deliveryAddress,

        @NotEmpty(message = "At least one package is required")
        List<PackageDto> packages,

        String notes // 备注 (可选)
) {
    // 内部类：定义单个包裹的参数
    public record PackageDto(
            @NotNull(message = "Weight is required")
            @DecimalMin(value = "0.1", message = "Weight must be greater than 0")
            BigDecimal weightKg,

            @DecimalMin(value = "1.0", message = "Length must be at least 1cm")
            BigDecimal lengthCm,

            @DecimalMin(value = "1.0", message = "Width must be at least 1cm")
            BigDecimal widthCm,

            @DecimalMin(value = "1.0", message = "Height must be at least 1cm")
            BigDecimal heightCm,

            String description
    ) {}
}