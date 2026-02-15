package com.project.couponservice.application.create;

public interface CreateCouponUseCase {
    CreateCouponOutput execute(CreateCouponCommand command);
}