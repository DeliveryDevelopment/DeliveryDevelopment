package com.laioffer.delivery.order;

import com.laioffer.delivery.pkg.Package;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 允许为空！支持 Guest 下单
    @Column(name = "user_id")
    private UUID userId;

    // 必须有！用于前端查询和二维码扫描
    // 在 Service 创建订单时生成：UUID.randomUUID().toString()
    @Column(name = "tracking_number", unique = true, nullable = false)
    private String trackingNumber;

    @Column(name = "from_address", nullable = false, columnDefinition = "TEXT")
    private String fromAddress;

    @Column(name = "to_address", nullable = false, columnDefinition = "TEXT")
    private String toAddress;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // 新增：总价 (根据所有 Package 的重量计算)
    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    // 新增：订单状态
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // 级联保存：保存 Order 时会自动保存 List 里的 Package
    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Package> packages = new ArrayList<>();

    public void addPackage(Package pkg) {
        this.packages.add(pkg);
        pkg.setOrder(this);
    }
}