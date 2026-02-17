package com.project.couponservice.domain.ports;

import com.project.couponservice.domain.Coupon;

import java.util.Optional;

public interface CouponPort {

    Coupon save(Coupon coupon);

    Optional<Coupon> findById(Long id);

    Coupon update(Coupon coupon);

    Optional<Coupon> findByCode(String code);
}