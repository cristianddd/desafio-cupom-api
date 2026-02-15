package com.project.couponservice.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CouponTest {

    @Test
    void shouldCreateCouponWhenDataIsValid() {

        String rawCode = "AB#CD12";
        Coupon coupon = Coupon.newCoupon(
                rawCode,
                "Test description",
                BigDecimal.valueOf(1.0),
                LocalDateTime.now().plusDays(1),
                true
        );
        assertNotNull(coupon);
        assertEquals(6, coupon.getCode().length());
        assertFalse(coupon.isDeleted());
    }

    @Test
    void shouldThrowWhenDiscountValueBelowMinimum() {
        assertThrows(DomainException.class, () ->
                Coupon.newCoupon(
                        "ABC123",
                        "Desc",
                        BigDecimal.valueOf(0.49),
                        LocalDateTime.now().plusDays(1),
                        false
                )
        );
    }

    @Test
    void shouldThrowWhenExpirationDateInPast() {
        assertThrows(DomainException.class, () ->
                Coupon.newCoupon(
                        "ABC123",
                        "Desc",
                        BigDecimal.valueOf(1.0),
                        LocalDateTime.now().minusDays(1),
                        false
                )
        );
    }

    @Test
    void shouldThrowWhenSanitizedCodeNotLengthSix() {
        assertThrows(DomainException.class, () ->
                Coupon.newCoupon(
                        "A1@",
                        "Desc",
                        BigDecimal.valueOf(1.0),
                        LocalDateTime.now().plusDays(1),
                        false
                )
        );
    }

    @Test
    void deleteShouldMarkCouponDeleted() {
        Coupon coupon = Coupon.newCoupon(
                "ABC123",
                "Desc",
                BigDecimal.valueOf(1.0),
                LocalDateTime.now().plusDays(1),
                false
        );
        coupon.delete();
        assertTrue(coupon.isDeleted());
    }

    @Test
    void deleteShouldNotAllowDoubleDeletion() {
        Coupon coupon = Coupon.newCoupon(
                "ABC123",
                "Desc",
                BigDecimal.valueOf(1.0),
                LocalDateTime.now().plusDays(1),
                false
        );
        coupon.delete();
        assertThrows(DomainException.class, coupon::delete);
    }
}