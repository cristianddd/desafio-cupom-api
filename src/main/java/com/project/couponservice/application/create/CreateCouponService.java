package com.project.couponservice.application.create;

import com.project.couponservice.domain.Coupon;
import com.project.couponservice.domain.ports.CouponPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCouponService implements CreateCouponUseCase {

    private final CouponPort couponPort;

    @Override
    public CreateCouponOutput execute(CreateCouponCommand command) {
        Coupon coupon = Coupon.newCoupon(command.code(), command.description(), command.discountValue(),
                command.expirationDate(), command.published());

        Coupon saved = couponPort.save(coupon);
        return new CreateCouponOutput(saved.getId(), saved.getCode(), saved.getExpirationDate());
    }
}