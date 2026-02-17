package com.project.couponservice.infra.port;

import com.project.couponservice.domain.Coupon;
import com.project.couponservice.domain.ports.CouponPort;
import com.project.couponservice.infra.entity.CouponJpaEntity;
import com.project.couponservice.infra.repository.CouponRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CouponJpaPort implements CouponPort {

    private final CouponRepository repository;

    public CouponJpaPort(CouponRepository repository) {
        this.repository = repository;
    }

    @Override
    public Coupon save(Coupon coupon) {
        CouponJpaEntity entity = CouponJpaEntity.fromDomain(coupon);
        CouponJpaEntity saved = repository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Coupon> findById(Long id) {
        return repository.findById(id).map(CouponJpaEntity::toDomain);
    }

    @Override
    public Coupon update(Coupon coupon) {
        CouponJpaEntity entity = CouponJpaEntity.fromDomain(coupon);
        CouponJpaEntity updated = repository.save(entity);
        return updated.toDomain();
    }

    @Override
    public Optional<Coupon> findByCode(String code) {
        return repository.findByCode(code).map(CouponJpaEntity::toDomain);
    }
}