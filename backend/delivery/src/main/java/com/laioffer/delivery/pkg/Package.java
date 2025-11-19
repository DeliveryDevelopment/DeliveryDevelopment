package com.laioffer.delivery.pkg;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.laioffer.delivery.order.Order;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 关键：ToString 时排除 order 字段，防止打印日志时死循环
@ToString(exclude = "order")
public class Package {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 多对一关系：多个包裹属于一个订单
     * fetch = LAZY: 只有用到的时候才查询 Order 信息，提高性能
     * optional = false: 包裹必须归属于一个订单，不能独立存在
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    // 🔥 核心修复：防止 JSON 序列化时的死循环 (StackOverflowError)
    // 当前端获取 Package 信息时，不会再反向把 Order 完整信息打印出来
    @JsonIgnore
    private Order order;

    @Column(name = "length_cm", precision = 10, scale = 2)
    private BigDecimal lengthCm;

    @Column(name = "width_cm", precision = 10, scale = 2)
    private BigDecimal widthCm;

    @Column(name = "height_cm", precision = 10, scale = 2)
    private BigDecimal heightCm;

    // 重量不能为空，因为它是计算运费的基础
    @Column(name = "weight_kg", precision = 10, scale = 2, nullable = false)
    private BigDecimal weightKg;

    @Column(columnDefinition = "TEXT")
    private String description;
}