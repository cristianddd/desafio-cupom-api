package com.project.couponservice.application.redeem;

public interface RedeemCouponUseCase {
    RedeemCouponOutput execute(RedeemCouponCommand getCouponCommand);
}
