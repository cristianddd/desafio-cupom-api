package com.project.couponservice.infra.entity;

import com.project.couponservice.domain.Coupon;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, length = 6, unique = true)
    private String code;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "discount_value", nullable = false)
    private BigDecimal discountValue;

    @Column(name = "expiration_date", nullable = false)
    private LocalDateTime expirationDate;

    @Column(name = "published", nullable = false)
    private boolean published;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static CouponJpaEntity fromDomain(Coupon coupon) {
        return new CouponJpaEntity(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getDiscountValue(),
                coupon.getExpirationDate(),
                coupon.isPublished(),
                coupon.isDeleted(),
                coupon.getCreatedAt(),
                coupon.getUpdatedAt()
        );
    }

    public Coupon toDomain() {
        return Coupon.with(
                this.id,
                this.code,
                this.description,
                this.discountValue,
                this.expirationDate,
                this.published,
                this.deleted,
                this.createdAt,
                this.updatedAt
        );
    }
}