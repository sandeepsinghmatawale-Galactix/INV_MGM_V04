package com.barinventory.billing.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "brand_sizes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BrandSize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Column(name = "size_label", nullable = false)
    private String sizeLabel; // "180ml", "375ml", "750ml", "1L"

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // retail selling price (MRP)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Packaging packaging;

    @Column(name = "abv_percent")
    private Double abvPercent;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(nullable = false)
    private boolean active = true;

    public enum Packaging {
        GLASS_BOTTLE, PET, CAN
    }
}