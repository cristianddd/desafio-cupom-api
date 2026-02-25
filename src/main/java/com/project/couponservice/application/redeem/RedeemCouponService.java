package com.project.couponservice.application.redeem;

import com.project.couponservice.domain.Coupon;
import com.project.couponservice.domain.NotFoundException;
import com.project.couponservice.domain.ports.CouponPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedeemCouponService implements RedeemCouponUseCase {

    private final CouponPort couponPort;

    @Override
    public RedeemCouponOutput execute(RedeemCouponCommand redeemCouponCommand) {
        Long couponId = redeemCouponCommand.id();
        Coupon coupon = couponPort.findById(couponId)
                .orElseThrow(() -> new NotFoundException("Cupom com id " + couponId + " não encontrado"));

        coupon.redeem();

        Coupon saved = couponPort.save(coupon);

        return new RedeemCouponOutput(saved.getId(), Boolean.TRUE);
    }
}
