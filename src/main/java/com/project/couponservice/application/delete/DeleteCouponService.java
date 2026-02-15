package com.project.couponservice.application.delete;

import com.project.couponservice.domain.Coupon;
import com.project.couponservice.domain.NotFoundException;
import com.project.couponservice.domain.ports.CouponPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteCouponService implements DeleteCouponUseCase {

    private final CouponPort couponPort;

    @Override
    public DeleteCouponOutput execute(DeleteCouponCommand command) {
        Long couponId = command.id();
        Coupon coupon = couponPort.findById(couponId)
                .orElseThrow(() -> new NotFoundException("Cupom com id " + couponId + " não encontrado"));

        coupon.delete();
        Coupon updated = couponPort.update(coupon);
        return new DeleteCouponOutput(updated.getId());
    }
}