package com.project.couponservice.application.get;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GetCouponOutput(
        Long id,
        String code,
        String description,
        BigDecimal discountValue,
        LocalDateTime expirationDate,
        String status,
        boolean published,
        boolean deleted
) {
}
