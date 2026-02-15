package com.project.couponservice.application.create;

import java.time.LocalDateTime;

public record CreateCouponOutput(Long id, String code, LocalDateTime expirationDate) {
}