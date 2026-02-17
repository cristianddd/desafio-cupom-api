package com.project.couponservice.application.get;

import com.project.couponservice.domain.Coupon;
import com.project.couponservice.domain.NotFoundException;
import com.project.couponservice.domain.ports.CouponPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GetCouponService implements GetCouponUseCase {

    private final CouponPort couponPort;

    @Override
    public GetCouponOutput execute(GetCouponCommand command) {
        Long couponId = command.id();
        Coupon coupon = couponPort.findById(couponId)
                .orElseThrow(() -> new NotFoundException("Cupom com id " + couponId + " não encontrado"));

        return new GetCouponOutput(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getDiscountValue(),
                coupon.getExpirationDate(),
                resolveStatus(coupon),
                coupon.isPublished(),
                coupon.isDeleted()
        );
    }

    private String resolveStatus(Coupon coupon) {
        if (coupon.isDeleted()) {
            return "DELETED";
        }
        if (!coupon.getExpirationDate().isAfter(LocalDateTime.now())) {
            return "EXPIRED";
        }
        return "ACTIVE";
    }
}
