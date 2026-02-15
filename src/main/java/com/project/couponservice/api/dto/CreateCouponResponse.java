package com.project.couponservice.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CreateCouponResponse {
    private Long id;
    private String code;
    private LocalDateTime expirationDate;
}