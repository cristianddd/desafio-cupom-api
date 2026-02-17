package com.project.couponservice.infra.repository;

import com.project.couponservice.infra.entity.CouponJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<CouponJpaEntity, Long> {
    Optional<CouponJpaEntity> findByCode(String code);
}