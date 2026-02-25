package com.project.couponservice.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RedeemCouponResponse {
    private Long id;
    private boolean redeemed;
}
