package com.project.couponservice.application.create;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateCouponCommand(String code, String description, BigDecimal discountValue,
                                  LocalDateTime expirationDate, boolean published) {

}