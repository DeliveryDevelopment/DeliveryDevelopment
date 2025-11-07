package com.laioffer.delivery.pkg;

import com.laioffer.delivery.order.Order;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PackageService {

    private final PackageRepository packageRepository;

    public List<Package> findByOrderId(Long orderId) {
        return packageRepository.findByOrderId(orderId);
    }

    @Transactional
    public void applyPackages(Order order, List<PackagePayload> payloads) {
        order.getPackages().clear();
        if (payloads == null || payloads.isEmpty()) {
            return;
        }
        List<Package> packages = new ArrayList<>();
        for (PackagePayload payload : payloads) {
            Package pkg = Package.builder()
                    .order(order)
                    .lengthCm(payload.lengthCm())
                    .widthCm(payload.widthCm())
                    .heightCm(payload.heightCm())
                    .weightKg(payload.weightKg())
                    .description(payload.description())
                    .build();
            packages.add(pkg);
        }
        order.getPackages().addAll(packages);
    }

    public record PackagePayload(
            BigDecimal lengthCm,
            BigDecimal widthCm,
            BigDecimal heightCm,
            BigDecimal weightKg,
            String description
    ) {
    }
}
