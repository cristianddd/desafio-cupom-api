package com.project.couponservice.infra.repository;

import com.project.couponservice.infra.entity.CouponJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<CouponJpaEntity, Long> {
}